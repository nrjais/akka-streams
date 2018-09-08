package example.server
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.Future

class KVServer(kvRoutes: KVRoutes)(implicit actorSystem: ActorSystem) {
  implicit val mat: ActorMaterializer   = ActorMaterializer()
  def start: Future[Http.ServerBinding] = Http().bindAndHandle(kvRoutes.routes, "0.0.0.0", 8080)
}
