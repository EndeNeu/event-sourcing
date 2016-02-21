package event.sourcing

import event.sourcing.store.Aggregator
import org.scalatest.{Matchers, WordSpecLike}

class AggregatorSpec extends WordSpecLike with Matchers with CommonSpec {

  "the aggregator" should {
    "correctly create an account" in new TestContext {
      Aggregator.find(entityId) should be(List())
    }
    "correctly update an account" in new TestContext {
      Aggregator.update(entityId, event)
      Aggregator.find(entityId) should be(List(event))
      Aggregator.update(entityId, event2)
      Aggregator.update(entityId, event3)
      Aggregator.find(entityId) should be(List(event, event2, event3))
    }
  }
}