package event.sourcing

import event.sourcing.service.AccountService
import event.sourcing.store.Aggregator
import org.scalatest.{Matchers, WordSpecLike}

class AccountServiceSpec extends WordSpecLike with Matchers with CommonSpec {

  "AccountService" should {
    "correctly open/restore an account" in new TestContext {
      val account = AccountService.openAccount(100)
      account.balance should be(100)

      // re-find tha previous account
      val replayedAccount = Aggregator.findOrCreateAccount(account.entityId)
      // check that events are replayed.
      replayedAccount.balance should be(100)
    }
  }
}