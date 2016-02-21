package event.sourcing

import java.util.UUID

import event.sourcing.store.Aggregator
import org.scalatest.{Matchers, WordSpecLike}

class AggregatorSpec extends WordSpecLike with Matchers with CommonSpec {

  "the aggregator" should {
    "correctly create an account" in new TestContext {
      val account = Aggregator.findOrCreateAccount(entityId)
      account.entityId should be(entityId)
      account.balance should be(0)
    }
  }
}
