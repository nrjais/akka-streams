package example.server

import akka.NotUsed
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import example.api.KeyWatchUpdate

case class SourceData[K, V](value: V,
                            queue: SourceQueueWithComplete[KeyWatchUpdate[K, V]],
                            source: Source[KeyWatchUpdate[K, V], NotUsed])
