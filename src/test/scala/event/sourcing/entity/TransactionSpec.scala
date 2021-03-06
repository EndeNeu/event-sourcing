package event.sourcing.entity

import java.util.UUID

import event.sourcing.CommonSpec
import event.sourcing.domain.TransactionCommands.{TransactionCompleteCommand, TransactionExecuteCommand, TransactionCreateCommand}
import event.sourcing.domain.TransactionEvents._
import org.scalatest.{Matchers, WordSpecLike}

class TransactionSpec extends WordSpecLike with Matchers with CommonSpec {

  "A transaction" should {
    "correctly parse commands" in new TestContext {
      val transaction = new Transaction(entityId)
      transaction.handleCommand(TransactionCreateCommand(a1, a2, 100)).toOption.get.head shouldBe a[TransactionCreateEvent]
      transaction.handleCommand(TransactionExecuteCommand()).toOption.get.head shouldBe a[TransactionInProgressEvent]
      transaction.handleCommand(TransactionCompleteCommand()).toOption.get.head shouldBe a[TransactionExecutedEvent]
    }

    "correctly parse events" in new TestContext {
      val transaction = new Transaction(entityId)
      val newTransaction = transaction.handleEvent(createTransactionEvent)
      newTransaction.amount should be(createTransactionEvent.amount)
      newTransaction.from should be(createTransactionEvent.from)
      newTransaction.to should be(createTransactionEvent.to)
      newTransaction.state should be(createTransactionEvent.state)


      val newTransaction2 = newTransaction.handleEvent(TransactionExecutedEvent(entityId, TransactionInProgressState))
      newTransaction2.amount should be(5)
      newTransaction2.from.balance should be(a1.balance)
      newTransaction2.from.entityId should be(a1.entityId)
      newTransaction2.to.balance should be(a2.balance)
      newTransaction2.to.entityId should be(a2.entityId)
      newTransaction2.state should be(TransactionInProgressState)

      val newTransaction3 = transaction.handleEvent(TransactionSnapshotEvent(entityId, a1, a2, 10, TransactionCompletedState))
      newTransaction3.entityId should be(entityId)
      newTransaction3.from should be(a1)
      newTransaction3.to should be(a2)
      newTransaction3.amount should be(10)
      newTransaction3.state should be(TransactionCompletedState)
    }

    "correctly be replayed" in new TestContext {
      val transaction = new Transaction(entityId)
      val replayed = transaction.replayEvents(List())
      replayed.amount should be(0)
      replayed.entityId should be(entityId)

      val replayed2 = transaction.replayEvents(List(createTransactionEvent, completeTransactionEvent))
      replayed2.entityId should be(entityId)
      replayed2.state should be(completeTransactionEvent.state)
    }
  }
}