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
    val accountOpenCommand = AccountOpenCommand(100)
    val a1 = AccountService.openAccount(AccountOpenCommand(10))
    val a2 = AccountService.openAccount(AccountOpenCommand(20))
    val accountOpenCommandWithZero = AccountOpenCommand(0)
    val accountDebitCommand = AccountDebitCommand(50)
    val accountCreditCommand = AccountCreditCommand(150)
    val openAccountEvent = AccountOpenEvent(entityId, 100)
    val creditAccountEvent = AccountCreditEvent(entityId, 150)
    val debitAccountEvent = AccountDebitEvent(entityId, 100)
    val createTransactionEvent = TransactionCreateEvent(entityId, a1, a2, 5, TransactionCreatedState)
    val completeTransactionEvent = TransactionExecutedEvent(entityId, TransactionCreatedState)
  }
}