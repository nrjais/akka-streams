package example.client
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import example.api.KVStore
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{Await, Future}

class KVClientTest extends FunSuite with Matchers with PlayJsonSupport with DomainFormats {

  test("set-get") {
    implicit val actorSystem: ActorSystem = ActorSystem("test")
    val client: KVStore[Id, Person]       = new KVClient[Id, Person]("http://localhost:8080")

    val id     = Id("1")
    val person = Person("Neeraj", 21)

    client.get(id).block shouldBe None
    client.set(id, person).block shouldBe true
    client.get(id).block shouldBe Some(person)
  }

  test("watch") {
    implicit val actorSystem: ActorSystem = ActorSystem("test")
    implicit val mat: ActorMaterializer   = ActorMaterializer()
    val client: KVStore[Id, Person]       = new KVClient[Id, Person]("http://localhost:8080")

    val id     = Id("1")
    val person = Person("Neeraj", 21)

    client.watch(id).runForeach(println)

    client.set(id, person)
    client.set(id, Person("N", 20))
    client.set(id, Person("J", 25))

    Thread.sleep(5000)
  }

  implicit class BlockingFuture[T](f: Future[T]) {
    def block: T = Await.result(f, 5.seconds)
  }
}
