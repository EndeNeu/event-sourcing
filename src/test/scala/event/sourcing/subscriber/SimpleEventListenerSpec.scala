package event.sourcing.subscriber

import event.sourcing.CommonSpec
import event.sourcing.domain.TransactionCommands.TransactionCreateCommand
import event.sourcing.domain.TransactionEvents.{TransactionCompletedState, TransactionInProgressEvent, TransactionInProgressState}
import event.sourcing.service.{AccountService, TransactionService}
import org.scalatest.{Matchers, WordSpecLike}

class SimpleEventListenerSpec extends WordSpecLike with Matchers with CommonSpec {

  val eventListener = new SimpleEventListener

  "SimpleEventListener" should {
    "react on transaciton execution" in new TestContext {
      val acc1 = AccountService.openAccount(accountOpenCommand)
      val acc2 = AccountService.openAccount(accountOpenCommand2)
      val transaciton = TransactionService.createTransaction(TransactionCreateCommand(acc1, acc2, 50))

      eventListener.notifyEvent(TransactionInProgressEvent(transaciton.entityId, acc1, acc2, 50, TransactionInProgressState))

      val t = TransactionService.findTransaction(transaciton.entityId)
      t.state should be(TransactionCompletedState)
      t.amount should be(50)
      t.from should be(acc1)
      t.to should be(acc2)

    }
  }

}