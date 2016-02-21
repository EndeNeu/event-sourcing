package event.sourcing

import java.util.UUID

import event.sourcing.domain.TransactionEvents.TransactionCreated
import event.sourcing.handler.Account
import event.sourcing.service.{TransactionService, AccountService}
import org.scalatest.{Matchers, WordSpecLike}

class TransactionServiceSpec  extends WordSpecLike with Matchers with CommonSpec {

  "TransactionService" should {
    "correctly open/restore a transaction" in new TestContext {
      val from = new Account(UUID.randomUUID(), 0)
      val to = new Account(UUID.randomUUID(), 0)
      val transaction = TransactionService.createTransaction(from, to, 100)
      transaction.state should be(TransactionCreated)
    }

    "correctly find a transaction" in new TestContext {
      intercept[IllegalArgumentException] {
        TransactionService.findTransaction(UUID.randomUUID())
      }

      val from = new Account(UUID.randomUUID(), 0)
      val to = new Account(UUID.randomUUID(), 0)
      val transaction = TransactionService.createTransaction(from, to, 0)
      // re-find tha previous account
      val replayedAccount = TransactionService.findTransaction(transaction.entityId)
      // check that events are replayed.
      replayedAccount.state should be(TransactionCreated)
      replayedAccount.from.get should be(from)
      replayedAccount.to.get should be(to)
      replayedAccount.amount.get should be(0)

    }
  }
}