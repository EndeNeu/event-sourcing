package event.sourcing

import java.util.UUID

import event.sourcing.domain.AccountCommands.{AccountCreditCommand, AccountDebitCommand, AccountOpenCommand}
import event.sourcing.domain.AccountEvents.{AccountDebitEvent, AccountCreditEvent, AccountOpenEvent}
import event.sourcing.domain.TransactionEvents.{TransactionExecutedEvent, TransactionInProgressState, TransactionCreatedState, TransactionCreateEvent}
import event.sourcing.entity.Account
import event.sourcing.service.AccountService

trait CommonSpec {
  trait TestContext {
    val entityId = UUID.randomUUID()
    val eventId = UUID.randomUUID()
    val eventId2 = UUID.randomUUID()
    val eventId3 = UUID.randomUUID()
    val accountOpenCommand = AccountOpenCommand(UUID.randomUUID(), 100)
    val a1 = AccountService.openAccount(AccountOpenCommand(UUID.randomUUID(), 10))
    val a2 = AccountService.openAccount(AccountOpenCommand(UUID.randomUUID(), 20))
    val accountOpenCommandWithZero = AccountOpenCommand(UUID.randomUUID(), 0)
    val accountDebitCommand = AccountDebitCommand(UUID.randomUUID(), 50)
    val accountCreditCommand = AccountCreditCommand(UUID.randomUUID(), 150)
    val openAccountEvent = AccountOpenEvent(eventId, entityId, 100)
    val creditAccountEvent = AccountCreditEvent(eventId2, entityId, 150)
    val debitAccountEvent = AccountDebitEvent(eventId3, entityId, 100)
    val createTransactionEvent = TransactionCreateEvent(UUID.randomUUID(), entityId, a1, a2, 5, TransactionCreatedState)
    val completeTransactionEvent = TransactionExecutedEvent(UUID.randomUUID(), entityId, TransactionCreatedState)
  }
}