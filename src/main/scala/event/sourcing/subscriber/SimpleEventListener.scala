package event.sourcing.subscriber

import java.util.UUID

import event.sourcing.aggregator.{Aggregator, EventListenerLike}
import event.sourcing.domain.AccountCommands.{AccountCreditFromTransactionCommand, AccountDebitFromTransactionCommand}
import event.sourcing.domain.TransactionCommands.TransactionCompleteCommand
import event.sourcing.domain.TransactionEvents.{TransactionFailedEvent, TransactionFailedState, TransactionInProgressEvent}
import event.sourcing.domain.{ErrorEvent, Event}
import event.sourcing.entity.Transaction
import event.sourcing.service.{AccountService, TransactionService}

import scalaz.{\/, \/-}

class SimpleEventListener extends EventListenerLike {

  override def notifyEvent(e: Event): Unit = e match {
    case TransactionInProgressEvent(_, entityId, from, to, amount, _, _) =>
      val result: \/[ErrorEvent, Transaction] = for {
        debit <- AccountService.debitAccountFromTransfer(from.entityId, AccountDebitFromTransactionCommand(UUID.randomUUID(), entityId, amount))
        credit <- AccountService.creditAccountFromTransfer(to.entityId, AccountCreditFromTransactionCommand(UUID.randomUUID(), entityId, amount))
        transactionCompleted <- \/-(TransactionService.completeTransaction(entityId, TransactionCompleteCommand(UUID.randomUUID())))
      } yield transactionCompleted

      // in case an error occurred update the account and the transaction with the failure
      result.leftMap(errorEvent =>
        Aggregator.update(List(errorEvent, TransactionFailedEvent(UUID.randomUUID(), entityId, TransactionFailedState)))
      )

    // ignore other events
    case _ =>

  }
}