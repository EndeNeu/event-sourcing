package event.sourcing.service

import java.util.UUID

import event.sourcing.CommonSpec
import event.sourcing.domain.AccountCommands.{AccountDebitCommand, AccountOpenCommand}
import org.scalatest.{Matchers, WordSpecLike}

class AccountServiceSpec extends WordSpecLike with Matchers with CommonSpec {

  "AccountService" should {
    "correctly open/restore an account" in new TestContext {
      val account = AccountService.openAccount(accountOpenCommand)
      account.balance should be(100)
    }

    "correctly find an account" in new TestContext {
      intercept[IllegalArgumentException] {
        AccountService.findAccount(UUID.randomUUID())
      }

      val account = AccountService.openAccount(accountOpenCommand)
      val replayedAccount = AccountService.findAccount(account.entityId)
      // check that events are replayed.
      replayedAccount.balance should be(100)
    }

    "correctly debit an account" in new TestContext {
      val account = AccountService.openAccount(AccountOpenCommand(100))
      AccountService.debitAccount(account.entityId, AccountDebitCommand(50)).balance should be(50)
    }

    "correctly credit an account" in new TestContext {
      val account = AccountService.openAccount(accountOpenCommand)
      AccountService.creditAccount(account.entityId, accountCreditCommand).balance should be(250)

      // re-find tha previous account
      val replayedAccount = AccountService.findAccount(account.entityId)
      // check that events are replayed.
      replayedAccount.balance should be(250)
    }
  }
}