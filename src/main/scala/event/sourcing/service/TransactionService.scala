package event.sourcing.service

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.domain.Event
import event.sourcing.domain.TransactionEvents.{CreateTransactionEvent, TransactionCreated}
import event.sourcing.handler.{Account, Transaction}
import event.sourcing.store.Aggregator

object TransactionService {

  def createTransaction(from: Account, to: Account, amount: Long): Transaction = {
    val transactionId = UUID.randomUUID()
    recreateTransaction(transactionId, Aggregator.update(transactionId, CreateTransactionEvent(UUID.randomUUID(), from, to, amount, TransactionCreated)))
  }

  def findTransaction(transactionId: EntityId): Transaction =
    recreateTransaction(transactionId, Aggregator.find(transactionId))

  /**
    * Nones are used as placeholders but we assume that at least the CreateTransactionEvent
    * exists which will set all the right parameters.
    */
  private def recreateTransaction(id: EntityId, events: List[Event]) =
    new Transaction(id, None, None, None, TransactionCreated).replayEvents(events)

}