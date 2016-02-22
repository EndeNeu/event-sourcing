package event.sourcing.domain

import event.sourcing.{EntityId, EventId}
import org.joda.time.DateTime

/**
  * All account related events.
  */
object AccountEvents {
  case class AccountOpenEvent(id: EventId, entityId: EntityId, initialBalance: Long, ts: Long = DateTime.now.getMillis) extends EventLike

  case class AccountDebitEvent(id: EventId, entityId: EntityId, debit: Long, ts: Long = DateTime.now.getMillis) extends EventLike
  case class AccountDebitFromTransferEvent(id: EventId, entityId: EntityId, transferId: EntityId, debit: Long, ts: Long = DateTime.now.getMillis) extends EventLike

  case class AccountCreditEvent(id: EventId, entityId: EntityId, credit: Long, ts: Long = DateTime.now.getMillis) extends EventLike
  case class AccountCreditFromTransferEvent(id: EventId, entityId: EntityId, transferId: EntityId, credit: Long, ts: Long = DateTime.now.getMillis) extends EventLike
}
