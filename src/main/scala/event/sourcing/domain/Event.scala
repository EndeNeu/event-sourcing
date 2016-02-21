package event.sourcing.domain

import java.util.UUID

import event.sourcing.EventId
import event.sourcing.handler.Account
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
  case class DebitAccount(id: EventId, debit: Long, ts: Long = DateTime.now.getMillis) extends Event
  case class CreditAccount(id: EventId, credit: Long, ts: Long = DateTime.now.getMillis) extends Event
}

object TransactionEvents {
  trait TransactionState
  case object TransactionCreated extends TransactionState
  case object TransactionCompleted extends TransactionState

  case class CreateTransactionEvent(id: EventId, from: Account, to: Account, amount: Long, state: TransactionState, ts: Long = DateTime.now.getMillis) extends Event
}