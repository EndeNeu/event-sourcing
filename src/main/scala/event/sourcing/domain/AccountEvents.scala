package event.sourcing.domain

import event.sourcing.EntityId

/**
  * All account related events.
  */
object AccountEvents {
  case class AccountOpenEvent(entityId: EntityId, initialBalance: Long) extends EventLike

  case class AccountDebitEvent(entityId: EntityId, debit: Long) extends EventLike
  case class AccountDebitFromTransferEvent(entityId: EntityId, transferId: EntityId, debit: Long) extends EventLike

  case class AccountCreditEvent(entityId: EntityId, credit: Long) extends EventLike
  case class AccountCreditFromTransferEvent(entityId: EntityId, transferId: EntityId, credit: Long) extends EventLike
}
