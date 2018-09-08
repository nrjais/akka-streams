package example.client
import akka.actor.ActorSystem
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import example.api.KVStore
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{Await, Future}

class KVClientTest extends FunSuite with Matchers with PlayJsonSupport with Domain {

  test("set-get") {
    implicit val actorSystem: ActorSystem = ActorSystem("test")
    val client: KVStore[Id, Person]       = new KVClient[Id, Person]("http://localhost:8080/")

    val id     = Id("1")
    val person = Person("Neeraj", 21)

    client.get(id).block shouldBe None
    client.set(id, person).block shouldBe None
    client.get(id).block shouldBe person
  }

  implicit class BlockingFuture[T](f: Future[T]) {
    def block: T = Await.result(f, 5.seconds)
  }
}
