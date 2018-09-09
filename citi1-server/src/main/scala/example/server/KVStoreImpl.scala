package example.server
import java.util.concurrent.Executors

import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl.{Keep, Source, SourceQueueWithComplete}
import example.api.{KVStore, KeyWatchUpdate}

import scala.concurrent.{ExecutionContext, Future}

class KVStoreImpl[K, V](implicit val mat: Materializer) extends KVStore[K, V] {

  private[this] var data: Map[K, SourceData[K, V]] = Map.empty

  implicit val ec: ExecutionContext = ExecutionContext
    .fromExecutorService(Executors.newSingleThreadExecutor())

  private def source: (SourceQueueWithComplete[KeyWatchUpdate[K, V]], Source[KeyWatchUpdate[K, V], NotUsed]) =
    Source
      .queue[KeyWatchUpdate[K, V]](256, OverflowStrategy.dropHead)
      .preMaterialize()

  override def set(key: K, value: V): Future[Boolean] = Future {
    val lastData = {
      val s = source
      data.getOrElse(key, SourceData(value, s._1, s._2))
    }
    data = data + (key -> lastData.copy(value = value))
    lastData.queue.offer(KeyWatchUpdate(key, value))
    true
  }

  override def get(key: K): Future[Option[V]] = Future {
    data.get(key).map(_.value)
  }

  override def watch(key: K): Option[Source[KeyWatchUpdate[K, V], KillSwitch]] = {
    data
      .get(key)
      .map(_.source)
      .map(_.viaMat(KillSwitches.single)(Keep.right))
  }
}
