package event.sourcing.entity

import event.sourcing.CommonSpec
import event.sourcing.domain.AccountCommands.{AccountCreditFromTransactionCommand, AccountDebitFromTransactionCommand, AccountOpenCommand}
import event.sourcing.domain.AccountEvents._
import event.sourcing.domain.ErrorEvents.AccountInsufficientFoundEvent
import event.sourcing.service.AccountService
import org.scalatest.{Matchers, WordSpecLike}

class AccountSpec extends WordSpecLike with Matchers with CommonSpec {

  "An account" should {
    "correctly parse commands" in new TestContext {
      val account = AccountService.openAccount(AccountOpenCommand(300))
      account.handleCommand(accountOpenCommand).toOption.get.head shouldBe a[AccountOpenEvent]
      account.handleCommand(accountCreditCommand).toOption.get.head shouldBe a[AccountCreditEvent]
      account.handleCommand(accountDebitCommand).toOption.get.head shouldBe a[AccountDebitEvent]
      account.handleCommand(accountDebitFromTransactionCommand).toOption.get.head shouldBe a[AccountDebitFromTransactionEvent]
      account.handleCommand(accountCreditFromTransactionCommand).toOption.get.head shouldBe a[AccountCreditFromTransactionEvent]

      intercept[IllegalArgumentException] {
        account.handleCommand(null)
      }

      val poorAccount = AccountService.openAccount(AccountOpenCommand(0))
      poorAccount.handleCommand(accountDebitCommand).swap.toOption.get shouldBe a[AccountInsufficientFoundEvent]
    }

    "correctly parse events" in new TestContext {
      val account = AccountService.openAccount(AccountOpenCommand(0))
      account.handleEvent(openAccountEvent).balance should be(100)
      account.handleEvent(creditAccountEvent).balance should be(150)
      account.handleEvent(creditAccountEvent).handleEvent(debitAccountEvent).balance should be(50)
      account.handleEvent(accountSnapshotEvent).balance should be(150)

      account.handleEvent(openAccountEvent).handleEvent(accountDebitFromTransactionEvent).balance should be(50)
      account.handleEvent(accountCreditFromTransactionEvent).balance should be(50)

      intercept[IllegalArgumentException] {
        account.handleEvent(null)
      }
    }

    "correctly be replayed" in new TestContext {
      val account = AccountService.openAccount(AccountOpenCommand(0))
      val replayed = account.replayEvents(List())
      replayed.balance should be(0)
      replayed.entityId should be(account.entityId)

      val replayed2 = account.replayEvents(List(openAccountEvent, creditAccountEvent, debitAccountEvent, accountCreditFromTransactionEvent, accountDebitFromTransactionEvent))
      replayed2.entityId should be(account.entityId)
      replayed2.balance should be(150)
    }
  }
}
