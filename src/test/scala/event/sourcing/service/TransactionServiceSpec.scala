package event.sourcing.service

import java.util.UUID

import event.sourcing.CommonSpec
import event.sourcing.domain.AccountCommands.AccountOpenCommand
import event.sourcing.domain.TransactionCommands.{TransactionCreateCommand, TransactionExecuteCommand}
import event.sourcing.domain.TransactionEvents.{TransactionCompletedState, TransactionCreatedState, TransactionFailedState, TransactionInProgressState}
import org.scalatest.{Matchers, WordSpecLike}

class TransactionServiceSpec extends WordSpecLike with Matchers with CommonSpec {

  "TransactionService" should {
    "correctly open/restore a transaction" in new TestContext {
      val from = AccountService.openAccount(AccountOpenCommand(0))
      val to = AccountService.openAccount(AccountOpenCommand(0))
      val transaction = TransactionService.createTransaction(TransactionCreateCommand(from, to, 100))
      transaction.state should be(TransactionCreatedState)
    }

    "correctly find a transaction" in new TestContext {
      intercept[IllegalArgumentException] {
        TransactionService.findTransaction(UUID.randomUUID())
      }

      val from = AccountService.openAccount(AccountOpenCommand(0))
      val to = AccountService.openAccount(AccountOpenCommand(0))
      val transaction = TransactionService.createTransaction(TransactionCreateCommand(from, to, 0))
      // re-find tha previous account
      val replayedAccount = TransactionService.findTransaction(transaction.entityId)
      // check that events are replayed.
      replayedAccount.state should be(TransactionCreatedState)
      replayedAccount.from should be(from)
      replayedAccount.to should be(to)
      replayedAccount.amount should be(0)
    }

    "correctly mark a transaction as failed" in new TestContext {
      val from = AccountService.openAccount(AccountOpenCommand(0))
      val to = AccountService.openAccount(AccountOpenCommand(0))
      val transaction = TransactionService.createTransaction(TransactionCreateCommand(from, to, 100))
      TransactionService.executeTransaction(transaction.entityId, TransactionExecuteCommand()).state should be(TransactionInProgressState)
      TransactionService.findTransaction(transaction.entityId).state should be(TransactionFailedState)

    }
    "correctly mark a transaction as completed" in new TestContext {
      val from2 = AccountService.openAccount(AccountOpenCommand(150))
      val to2 = AccountService.openAccount(AccountOpenCommand(0))
      val transaction2 = TransactionService.createTransaction(TransactionCreateCommand(from2, to2, 100))
      TransactionService.executeTransaction(transaction2.entityId, TransactionExecuteCommand()).state should be(TransactionInProgressState)
      TransactionService.findTransaction(transaction2.entityId).state should be(TransactionCompletedState)

      AccountService.findAccount(from2.entityId).balance should be(50)
      AccountService.findAccount(to2.entityId).balance should be(100)
    }
  }
}