package event.sourcing.subscriber

import java.util.UUID

import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.AccountCommands.{AccountCreditFromTransactionCommand, AccountDebitFromTransactionCommand}
import event.sourcing.domain.TransactionCommands.TransactionCompleteCommand
import event.sourcing.domain.TransactionEvents.{TransactionFailedEvent, TransactionFailedState, TransactionInProgressEvent}
import event.sourcing.domain.{ErrorEventLike, EventLike}
import event.sourcing.entity.Transaction
import event.sourcing.service.{AccountService, TransactionService}

import scalaz.{\/, \/-}

/**
  * A simple event listener, only acts on transaction execution.
  */
class SimpleEventListener extends EventListenerLike {

  override def notifyEvent(e: EventLike): Unit = e match {
    case TransactionInProgressEvent(entityId, from, to, amount, _) =>
      val result: \/[ErrorEventLike, Transaction] = for {
        debit <- AccountService.debitAccountFromTransfer(from.entityId, AccountDebitFromTransactionCommand(entityId, amount))
        credit <- AccountService.creditAccountFromTransfer(to.entityId, AccountCreditFromTransactionCommand(entityId, amount))
        transactionCompleted <- \/-(TransactionService.completeTransaction(entityId, TransactionCompleteCommand()))
      } yield transactionCompleted

      // in case an error occurred update the account and the transaction with the failure
      result.leftMap(errorEvent =>
        Aggregator.update(List(errorEvent, TransactionFailedEvent(entityId, TransactionFailedState)))
      )

    // ignore other events
    case _ =>

  }
}