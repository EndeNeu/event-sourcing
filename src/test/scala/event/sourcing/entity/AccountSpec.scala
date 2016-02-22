package event.sourcing.entity

import event.sourcing.CommonSpec
import event.sourcing.domain.AccountEvents.{AccountDebitEvent, AccountCreditEvent, AccountOpenEvent}
import event.sourcing.domain.AccountInsufficientFoundEvent
import org.scalatest.{Matchers, WordSpecLike}

class AccountSpec extends WordSpecLike with Matchers with CommonSpec {

  "An account" should {
    "correctly parse commands" in new TestContext {
      val account = new Account(entityId, 300)
      account.handleCommand(accountOpenCommand).toOption.get.head shouldBe a[AccountOpenEvent]
      account.handleCommand(accountCreditCommand).toOption.get.head shouldBe a[AccountCreditEvent]
      account.handleCommand(accountDebitCommand).toOption.get.head shouldBe a[AccountDebitEvent]

      val poorAccount = new Account(entityId, 0)
      poorAccount.handleCommand(accountDebitCommand).swap.toOption.get shouldBe a[AccountInsufficientFoundEvent]
    }

    "correctly parse events" in new TestContext {
      val account = new Account(entityId, 0)
      account.handleEvent(openAccountEvent).balance should be(100)
      account.handleEvent(creditAccountEvent).balance should be(150)
      account.handleEvent(creditAccountEvent).handleEvent(debitAccountEvent).balance should be(50)
    }

    "correctly be replayed" in new TestContext {
      val account = new Account(entityId, 0)
      val replayed = account.replayEvents(List())
      replayed.balance should be(0)
      replayed.entityId should be(entityId)

      val replayed2 = account.replayEvents(List(openAccountEvent, creditAccountEvent, debitAccountEvent))
      replayed2.entityId should be(entityId)
      replayed2.balance should be(150)
    }
  }
}
