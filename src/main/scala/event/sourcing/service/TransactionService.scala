package event.sourcing.service

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.Event
import event.sourcing.domain.TransactionCommands.{TransactionCompleteCommand, TransactionCreateCommand, TransactionExecuteCommand}
import event.sourcing.entity.Transaction
import event.sourcing.util.DisjunctionUtil

import scalaz.{-\/, \/-}

object TransactionService {

  def createTransaction(command: TransactionCreateCommand): Transaction = {
    val transaction = new Transaction(UUID.randomUUID())
    DisjunctionUtil.createEntityFromDisjunction(transaction.handleCommand(command), transaction.entityId)(recreateTransaction)
  }

  def findTransaction(transactionId: EntityId): Transaction =
    recreateTransaction(transactionId, Aggregator.find(transactionId))

  def executeTransaction(transactionId: EntityId, command: TransactionExecuteCommand): Transaction = {
    val transaction = findTransaction(transactionId)
    transaction.handleCommand(command) match {
      case \/-(events) =>
        recreateTransaction(transactionId, Aggregator.update(events))
      case -\/(failedEvent) =>
        recreateTransaction(transactionId, Aggregator.update(failedEvent))
    }
  }

  def completeTransaction(transactionId: EntityId, command: TransactionCompleteCommand): Transaction = {
    val trans = findTransaction(transactionId)
    trans.handleCommand(command) match {
      case \/-(events) =>
        // not used because contains events also for the account and we want to retrieve only the transaction.
        Aggregator.update(events)
        recreateTransaction(transactionId, Aggregator.find(transactionId))
      case -\/(failedEvent) =>
        recreateTransaction(transactionId, Aggregator.update(failedEvent))
    }
  }

  /**
    * Nones are used as placeholders but we assume that at least the CreateTransactionEvent
    * exists (that should be the only way to create a transaction) which will set all the right parameters.
    */
  private def recreateTransaction(id: EntityId, events: List[Event]) =
    new Transaction(id).replayEvents(events)

}