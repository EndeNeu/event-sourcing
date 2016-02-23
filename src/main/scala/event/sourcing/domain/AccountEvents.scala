package event.sourcing.domain

import event.sourcing.EntityId

/**
  * All account related events.
  */
object AccountEvents {

  trait AccountEventLike extends EventLike

  case class AccountOpenEvent(entityId: EntityId, initialBalance: Long) extends AccountEventLike

  case class AccountDebitEvent(entityId: EntityId, debit: Long) extends AccountEventLike
  case class AccountDebitFromTransactionEvent(entityId: EntityId, transferId: EntityId, debit: Long) extends AccountEventLike

  case class AccountCreditEvent(entityId: EntityId, credit: Long) extends AccountEventLike
  case class AccountCreditFromTransactionEvent(entityId: EntityId, transferId: EntityId, credit: Long) extends AccountEventLike

  case class AccountSnapshotEvent(entityId: EntityId, balance: Long) extends AccountEventLike
}