package event.sourcing.domain

import event.sourcing._
import event.sourcing.entity.Account
import org.joda.time.DateTime

object TransactionEvents {
  trait TransactionState
  case object TransactionCreatedState extends TransactionState
  case object TransactionInProgressState extends TransactionState
  case object TransactionFailedState extends TransactionState
  case object TransactionCompletedState extends TransactionState

  case class TransactionCreateEvent(id: EventId, entityId: EntityId, from: Account, to: Account, amount: Long, state: TransactionState, ts: Long = DateTime.now.getMillis) extends Event
  case class TransactionInProgressEvent(id: EventId, entityId: EntityId, from: Account, to: Account, amount: Long, state: TransactionState, ts: Long = DateTime.now.getMillis) extends Event
  case class TransactionFailedEvent(id: EventId, entityId: EntityId, state: TransactionState, ts: Long = DateTime.now.getMillis) extends Event
  case class TransactionExecutedEvent(id: EventId, entityId: EntityId, state: TransactionState, ts: Long = DateTime.now.getMillis) extends Event
}