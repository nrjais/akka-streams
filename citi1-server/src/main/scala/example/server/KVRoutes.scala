package example.server
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import example.api.{KVStore, Payload}
import play.api.libs.json.{JsValue, Json}

class KVRoutes(kvStore: KVStore[JsValue, JsValue]) extends Directives with PlayJsonSupport {
  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
  val routes: Route = pathPrefix("kvstore") {
    post {
      path("get") {
        entity(as[JsValue]) { key =>
          rejectEmptyResponse {
            complete(kvStore.get(key))
          }
        }
      } ~
      path("watch") {
        entity(as[JsValue]) { key =>
          rejectEmptyResponse {
            complete(kvStore.watch(key).map(x => Json.toJson(x).toString()).map(x => ServerSentEvent(x)))
          }
        }
      } ~
      path("set") {
        entity(as[Payload[JsValue, JsValue]]) { payload =>
          complete(kvStore.set(payload.key, payload.value))
        }
      }
    }
  }
}
