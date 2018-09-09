package example.client

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, KillSwitch, KillSwitches}
import akka.stream.scaladsl.{Keep, Source}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

import example.api.{KVStore, KeyWatchUpdate, Payload}
import play.api.libs.json.{Format, Json}

import scala.async.Async._
import scala.concurrent.Future

class KVClient[K: Format, V: Format](baseUri: String)(implicit ac: ActorSystem) extends KVStore[K, V] {
  import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
  import ac.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  override def set(key: K, value: V): Future[Boolean] = async {
    val payload = Payload(key, value)
    val request = HttpRequest()
      .withMethod(HttpMethods.POST)
      .withUri(s"$baseUri/kvstore/set")
      .withEntity(await(Marshal(payload).to[RequestEntity]))

    val response = await(Http().singleRequest(request))

    await(Unmarshal(response.entity).to[Boolean])
  }

  override def get(key: K): Future[Option[V]] = async {
    val request = HttpRequest()
      .withMethod(HttpMethods.POST)
      .withUri(s"$baseUri/kvstore/get")
      .withEntity(await(Marshal(key).to[RequestEntity]))

    val response = await(Http().singleRequest(request))

    response.status match {
      case StatusCodes.OK       => Some(await(Unmarshal(response.entity).to[V]))
      case StatusCodes.NotFound => None
      case _                    => throw new RuntimeException(response.toString())
    }
  }

  override def watch(key: K): Option[Source[KeyWatchUpdate[K, V], KillSwitch]] = {
    val dd = async {
      val request = HttpRequest()
        .withMethod(HttpMethods.POST)
        .withUri(s"$baseUri/kvstore/watch")
        .withEntity(await(Marshal(key).to[RequestEntity]))

      val response = await(Http().singleRequest(request))

      await(
        Unmarshal(response.entity)
          .to[Source[ServerSentEvent, NotUsed]]
      ).map(x => Json.parse(x.data).as[KeyWatchUpdate[K, V]])
    }

    Option(dd)
      .map(Source.fromFutureSource)
      .map(_.viaMat(KillSwitches.single)(Keep.right))
  }
}
