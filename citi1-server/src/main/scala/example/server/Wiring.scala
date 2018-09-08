package example.server
import akka.actor.ActorSystem
import example.api.KVStore
import play.api.libs.json.JsValue

class Wiring {
  implicit val actorSystem                    = ActorSystem()
  lazy val kvStore: KVStore[JsValue, JsValue] = new KVStoreImpl[JsValue, JsValue]
  lazy val routes                             = new KVRoutes(kvStore)
  lazy val server                             = new KVServer(routes)
}
