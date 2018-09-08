package example.server
import java.util.concurrent.Executors

import akka.stream._
import akka.stream.scaladsl.{Keep, Source}
import example.api.{KVStore, KeyWatchUpdate}

import scala.concurrent.{ExecutionContext, Future}

class KVStoreImpl[K, V](implicit val mat: Materializer) extends KVStore[K, V] {
  private[this] var data: Map[K, V] = Map.empty
  implicit val ec: ExecutionContext = ExecutionContext
    .fromExecutorService(Executors.newSingleThreadExecutor())

  val (queue, source) = Source.queue[KeyWatchUpdate[K, V]](256, OverflowStrategy.dropHead).preMaterialize()

  override def set(key: K, value: V): Future[Boolean] = Future {
    data = data + (key -> value)
    queue.offer(KeyWatchUpdate(key, value))
    true
  }

  override def get(key: K): Future[Option[V]] = Future {
    data.get(key)
  }

  override def watch(key: K): Source[KeyWatchUpdate[K, V], KillSwitch] = {
    source.viaMat(KillSwitches.single)(Keep.right).filter(_.key == key)
  }
}
