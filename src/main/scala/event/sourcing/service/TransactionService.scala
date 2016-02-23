package event.sourcing.service

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.EventLike
import event.sourcing.domain.TransactionCommands.{TransactionCompleteCommand, TransactionCreateCommand, TransactionExecuteCommand}
import event.sourcing.entity.Transaction
import event.sourcing.util.DisjunctionUtil

object TransactionService {

  /**
    * Create a new transaction.
    */
  def createTransaction(command: TransactionCreateCommand): Transaction = {
    val transaction = new Transaction(UUID.randomUUID())
    DisjunctionUtil.createEntityFromDisjunction(transaction.handleCommand(command), transaction.entityId)(recreateTransaction)
  }

  /**
    * Find a transaction finding all the events and replaying
    */
  def findTransaction(transactionId: EntityId): Transaction =
    recreateTransaction(transactionId, Aggregator.find(transactionId))

  /**
    * Store an event to mark a transaction in progress, the even listener will catch this event and
    * fire the appropriate event to decrease and increase the amount from the accounts.
    * A transaction in progress is returned.
    */
  def executeTransaction(transactionId: EntityId, command: TransactionExecuteCommand): Transaction = {
    val transaction = findTransaction(transactionId)
    DisjunctionUtil.createEntityFromDisjunction(transaction.handleCommand(command), transaction.entityId)(recreateTransaction)
  }

  /**
    * Mark a transaction as completed with an event,
    * called from the event listener after the transaction is successful.
    */
  def completeTransaction(transactionId: EntityId, command: TransactionCompleteCommand): Transaction = {
    val transaction = findTransaction(transactionId)
    DisjunctionUtil.createEntityFromDisjunction(transaction.handleCommand(command), transaction.entityId)(recreateTransaction)
  }

  /**
    * Helper method that creates an empty account and replays the events on it.
    */
  private def recreateTransaction(id: EntityId, events: List[EventLike]) =
    new Transaction(id).replayEvents(events)

}