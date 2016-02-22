package event.sourcing.service

import java.util.UUID

import event.sourcing.CommonSpec
import event.sourcing.domain.AccountCommands.AccountOpenCommand
import event.sourcing.domain.TransactionCommands.{TransactionExecuteCommand, TransactionCreateCommand}
import event.sourcing.domain.TransactionEvents.TransactionCreatedState
import event.sourcing.entity.Account
import org.scalatest.{Matchers, WordSpecLike}

class TransactionServiceSpec  extends WordSpecLike with Matchers with CommonSpec {

  "TransactionService" should {
    "correctly open/restore a transaction" in new TestContext {
      val from = new Account(UUID.randomUUID(), 0)
      val to = new Account(UUID.randomUUID(), 0)
      val transaction = TransactionService.createTransaction(TransactionCreateCommand(UUID.randomUUID(), from, to, 100))
      transaction.state should be(TransactionCreatedState)
    }

    "correctly find a transaction" in new TestContext {
      intercept[IllegalArgumentException] {
        TransactionService.findTransaction(UUID.randomUUID())
      }

      val from = new Account(UUID.randomUUID(), 0)
      val to = new Account(UUID.randomUUID(), 0)
      val transaction = TransactionService.createTransaction(TransactionCreateCommand(UUID.randomUUID(), from, to, 0))
      // re-find tha previous account
      val replayedAccount = TransactionService.findTransaction(transaction.entityId)
      // check that events are replayed.
      replayedAccount.state should be(TransactionCreatedState)
      replayedAccount.from should be(from)
      replayedAccount.to should be(to)
      replayedAccount.amount should be(0)

    }

    "correctly execute a transaction" in new TestContext {
      val from = AccountService.openAccount(AccountOpenCommand(UUID.randomUUID(), 0))
      val to = AccountService.openAccount(AccountOpenCommand(UUID.randomUUID(), 0))
      val transaction = TransactionService.createTransaction(TransactionCreateCommand(UUID.randomUUID(), from, to, 100))
      println(TransactionService.executeTransaction(transaction.entityId, TransactionExecuteCommand(UUID.randomUUID())).state)


      println("----------------")

      val from2 = AccountService.openAccount(AccountOpenCommand(UUID.randomUUID(), 150))
      println(from2.balance)
      val to2 = AccountService.openAccount(AccountOpenCommand(UUID.randomUUID(), 0))
      println(to2.balance)
      val transaction2 = TransactionService.createTransaction(TransactionCreateCommand(UUID.randomUUID(), from2, to2, 100))
      println("id " + transaction2.entityId)
      println(TransactionService.executeTransaction(transaction2.entityId, TransactionExecuteCommand(UUID.randomUUID())).state)
      println(TransactionService.findTransaction(transaction2.entityId).state)

    }
  }
}