package event.sourcing

import event.sourcing.domain.TransactionEvents.TransactionCreated
import event.sourcing.handler.{Transaction, Account}
import org.scalatest.{Matchers, WordSpecLike}

class TransactionSpec extends WordSpecLike with Matchers with CommonSpec {

  "A transaction" should {
    "correctly parse events" in new TestContext {
      val transaction = new Transaction(entityId, None, None, None, TransactionCreated)
      val newTransaction = transaction.handleEvent(createTransactionEvent)
      newTransaction.amount.get should be(createTransactionEvent.amount)
      newTransaction.from.get should be(createTransactionEvent.from)
      newTransaction.to.get should be(createTransactionEvent.to)
      newTransaction.state should be(createTransactionEvent.state)
    }

    "correctly be replayed" in new TestContext {
      val transaction = new Transaction(entityId, None, None, Some(0), TransactionCreated)
      val replayed = transaction.replayEvents(List())
      replayed.amount.get should be(0)
      replayed.entityId should be(entityId)

      val replayed2 = transaction.replayEvents(List(createTransactionEvent))
      replayed2.entityId should be(entityId)
      replayed2.amount.get should be(createTransactionEvent.amount)
      replayed2.from.get should be(createTransactionEvent.from)
      replayed2.to.get should be(createTransactionEvent.to)
      replayed2.state should be(createTransactionEvent.state)
    }
  }
}