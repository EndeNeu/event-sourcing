package event.sourcing.domain

import event.sourcing.{EntityId, EventId}
import org.joda.time.DateTime

trait ErrorEvent extends Event

case class AccountInsufficientFoundEvent(val id: EventId, val entityId: EntityId, ts: Long = DateTime.now.getMillis) extends ErrorEvent
case class AccountInsufficientFoundFromTransactionEvent(val id: EventId, val entityId: EntityId, transactionId: EntityId, ts: Long = DateTime.now.getMillis) extends ErrorEvent
