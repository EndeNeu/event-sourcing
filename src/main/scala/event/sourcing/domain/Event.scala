package event.sourcing.domain

import java.util.UUID

import event.sourcing.EventId
import org.joda.time.DateTime

/**
  * Events superclass.
  */
trait Event {
  def id: EventId
  def ts: Long
}

/**
  * All account related events.
  */
object AccountEvents {
  case class OpenAccountEvent(id: EventId, initialBalance: Long, ts: Long = DateTime.now.getMillis) extends Event
  case class CreditAccount(id: EventId, credit: Long, ts: Long = DateTime.now.getMillis) extends Event
}