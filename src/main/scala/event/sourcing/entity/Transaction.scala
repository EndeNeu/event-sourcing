package event.sourcing.entity

import event.sourcing._
import event.sourcing.domain.TransactionCommands.{TransactionCompleteCommand, TransactionCreateCommand, TransactionExecuteCommand}
import event.sourcing.domain.TransactionEvents._

import scalaz.\/-

class Transaction private(val entityId: EntityId, val from: Account, val to: Account, val amount: Long, val state: TransactionState) extends EventHandlerLike[Transaction] {

  def this(entityId: EntityId) = this(entityId, null, null, null.asInstanceOf[Long], TransactionCreatedState)

  /**
    * Command handler, a response to a command is a list of events.
    */
  override def handleCommand: HandleCommand = {
    case TransactionCreateCommand(_from, _to, _amount) =>
      \/-(List(TransactionCreateEvent(entityId, _from, _to, _amount, TransactionCreatedState)))

    case TransactionExecuteCommand() =>
      \/-(List(TransactionInProgressEvent(entityId, from, to, amount, TransactionInProgressState)))

    case TransactionCompleteCommand() =>
      \/-(List(TransactionExecutedEvent(entityId, TransactionCompletedState)))

    case _ =>
      throw new IllegalArgumentException("Unknown command in transaction.")
  }

  /**
    * Event handler, a response to an event is a copy of this object
    */
  override def handleEvent: HandleEvent[Transaction] = {
    case TransactionCreateEvent(_, _from, _to, _amount, _state) =>
      new Transaction(entityId, _from, _to, _amount, _state)

    case TransactionFailedEvent(_, _state) =>
      new Transaction(entityId, from, to, amount, _state)

    case TransactionInProgressEvent(_, _, _, _, _state) =>
      new Transaction(entityId, from, to, amount, _state)

    case TransactionExecutedEvent(_, _state) =>
      new Transaction(entityId, from, to, amount, _state)

    case TransactionSnapshotEvent(_, _from, _to, _amount, _state) =>
      new Transaction(entityId, _from, _to, _amount, _state)

    case _ =>
      throw new IllegalArgumentException("Unknown event in transaction.")
  }

}