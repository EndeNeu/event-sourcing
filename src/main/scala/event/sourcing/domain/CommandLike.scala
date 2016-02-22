package event.sourcing.domain

import java.util.UUID

import event.sourcing._
import org.joda.time.DateTime

/**
  * Interface for commands.
  */
trait CommandLike {
  def id: CommandId = UUID.randomUUID()
  def ts: Long = DateTime.now.getMillis
}
