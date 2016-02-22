package event.sourcing.entity

import java.util.UUID

import event.sourcing._
import event.sourcing.domain.AccountEvents.{AccountCreditFromTransferEvent, AccountDebitFromTransferEvent}
import event.sourcing.domain.TransactionCommands.{TransactionCompleteCommand, TransactionCreateCommand, TransactionExecuteCommand}
import event.sourcing.domain.TransactionEvents._

import scalaz.\/-

class Transaction(val entityId: EntityId, val from: Account, val to: Account, val amount: Long, val state: TransactionState) extends EventHandlerLike[Transaction] {

  def this(entityId: EntityId) = this(entityId, null, null, null.asInstanceOf[Long], TransactionCreatedState)

  /**
    * Command handler, a response to a command is a list of events.
    */
  override def handleCommand: HandleCommand = {
    case TransactionCreateCommand(_, _from, _to, _amount, _) =>
      \/-(List(TransactionCreateEvent(UUID.randomUUID(), entityId, _from, _to, _amount, TransactionCreatedState)))

    case TransactionExecuteCommand(_, _) =>
      \/-(List(TransactionInProgressEvent(UUID.randomUUID(), entityId, from, to, amount, TransactionInProgressState)))

    case TransactionCompleteCommand(_, _) =>
      \/-(List(TransactionExecutedEvent(UUID.randomUUID(), entityId, TransactionCompletedState)))

    case _ =>
      throw new IllegalArgumentException("Unknown command in transaction.")
  }

  /**
    * Event handler, a response to an event is a copy of this object
    */
  override def handleEvent: HandleEvent[Transaction] = {
    case TransactionCreateEvent(_, _, _from, _to, _amount, _state, _) =>
      new Transaction(entityId, _from, _to, _amount, _state)

    case TransactionFailedEvent(_, _, _state, _) =>
      new Transaction(entityId, from, to, amount, _state)

    case TransactionInProgressEvent(_, _, _, _, _, _state, _) =>
      new Transaction(entityId, from, to, amount, _state)

    case TransactionExecutedEvent(_, _, _state, _) =>
      new Transaction(entityId, from, to, amount, _state)

    case _ =>
      throw new IllegalArgumentException("Unknown event in transaction.")
  }

}