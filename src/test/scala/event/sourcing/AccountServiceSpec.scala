package event.sourcing

import java.util.UUID

import event.sourcing.service.AccountService
import org.scalatest.{Matchers, WordSpecLike}

class AccountServiceSpec extends WordSpecLike with Matchers with CommonSpec {

  "AccountService" should {
    "correctly open/restore an account" in new TestContext {
      val account = AccountService.openAccount(100)
      account.balance should be(100)
    }

    "correctly find an account" in new TestContext {
      intercept[IllegalArgumentException] {
        AccountService.findAccount(UUID.randomUUID())
      }

      val account = AccountService.openAccount(100)
      val replayedAccount = AccountService.findAccount(account.entityId)
      // check that events are replayed.
      replayedAccount.balance should be(100)
    }

    "correctly debit an account" in new TestContext {
      val account = AccountService.openAccount(100)
      AccountService.debitAccount(account.entityId, 50).balance should be(50)

      intercept[IllegalArgumentException] {
        val emptyAccount = AccountService.openAccount(0)
        AccountService.debitAccount(emptyAccount.entityId, 50)
      }
    }

    "correctly credit an account" in new TestContext {
      val account = AccountService.openAccount(100)
      AccountService.creditAccount(account.entityId, 50).balance should be(150)

      // re-find tha previous account
      val replayedAccount = AccountService.findAccount(account.entityId)
      // check that events are replayed.
      replayedAccount.balance should be(150)
    }
  }
}