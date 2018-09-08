package example.api
import play.api.libs.json.{Format, Json}

case class KeyWatchUpdate[K, V](key: K, value: V)

object KeyWatchUpdate {
  implicit def format[K: Format, V: Format]: Format[KeyWatchUpdate[K, V]] = Json.format[KeyWatchUpdate[K, V]]
}
