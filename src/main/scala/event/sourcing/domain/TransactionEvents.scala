package event.sourcing.domain

import event.sourcing._
import event.sourcing.entity.Account

object TransactionEvents {
  trait TransactionState
  case object TransactionCreatedState extends TransactionState
  case object TransactionInProgressState extends TransactionState
  case object TransactionFailedState extends TransactionState
  case object TransactionCompletedState extends TransactionState

  case class TransactionCreateEvent(entityId: EntityId, from: Account, to: Account, amount: Long, state: TransactionState) extends EventLike
  // this one needs from and to to allow the event listener to respond to a transaction in progress.
  case class TransactionInProgressEvent(entityId: EntityId, from: Account, to: Account, amount: Long, state: TransactionState) extends EventLike
  case class TransactionFailedEvent(entityId: EntityId, state: TransactionState) extends EventLike
  case class TransactionExecutedEvent(entityId: EntityId, state: TransactionState) extends EventLike
}
