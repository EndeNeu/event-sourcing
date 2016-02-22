package event.sourcing.domain

import event.sourcing.{EntityId, EventId}
import org.joda.time.DateTime

/**
  * Interface for errors.
  */
trait ErrorEventLike extends EventLike

case class AccountInsufficientFoundEvent(val id: EventId, val entityId: EntityId, ts: Long = DateTime.now.getMillis) extends ErrorEventLike
case class AccountInsufficientFoundFromTransactionEvent(val id: EventId, val entityId: EntityId, transactionId: EntityId, ts: Long = DateTime.now.getMillis) extends ErrorEventLike
