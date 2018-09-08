package example.api

import akka.stream.KillSwitch
import akka.stream.scaladsl.Source

import scala.concurrent.Future

trait KVStore[K, V] {
  def set(key: K, value: V): Future[Boolean]
  def get(key: K): Future[Option[V]]
  def watch(key: K): Source[KeyWatchUpdate[K, V], KillSwitch]
}
