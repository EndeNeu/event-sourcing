package event.sourcing.domain

import java.util.UUID

import org.joda.time.DateTime

/**
  * Events superclass.
  */
trait Event {
  def id: UUID
  def ts: Long
}

/**
  * All account related events.
  */
object AccountEvents {
  case class OpenAccountEvent(id: UUID, initialBalance: Long, ts: Long = DateTime.now.getMillis) extends Event
}
