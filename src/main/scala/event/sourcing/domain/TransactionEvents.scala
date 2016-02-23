package event.sourcing.domain

import event.sourcing._
import event.sourcing.entity.Account

object TransactionEvents {
  trait TransactionState
  case object TransactionCreatedState extends TransactionState
  case object TransactionInProgressState extends TransactionState
  case object TransactionFailedState extends TransactionState
  case object TransactionCompletedState extends TransactionState

  trait TransactionEventLike extends EventLike
  case class TransactionCreateEvent(entityId: EntityId, from: Account, to: Account, amount: Long, state: TransactionState) extends TransactionEventLike
  // this one needs from and to to allow the event listener to respond to a transaction in progress.
  case class TransactionInProgressEvent(entityId: EntityId, from: Account, to: Account, amount: Long, state: TransactionState) extends TransactionEventLike
  case class TransactionFailedEvent(entityId: EntityId, state: TransactionState) extends TransactionEventLike
  case class TransactionExecutedEvent(entityId: EntityId, state: TransactionState) extends TransactionEventLike

  case class TransactionSnapshotEvent(entityId: EntityId, form: Account, to: Account, amount: Long, state: TransactionState) extends TransactionEventLike
}
