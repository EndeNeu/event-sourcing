package event.sourcing

import event.sourcing.store.Aggregator
import org.scalatest.{Matchers, WordSpecLike}

class AggregatorSpec extends WordSpecLike with Matchers with CommonSpec {

  "the aggregator" should {
    "correctly create an account" in new TestContext {
      Aggregator.find(entityId) should be(List())
    }
    "correctly update an account" in new TestContext {
      Aggregator.update(entityId, openAccountEvent)
      Aggregator.find(entityId) should be(List(openAccountEvent))
      Aggregator.update(entityId, creditAccountEvent)
      Aggregator.update(entityId, debitAccountEvent)
      Aggregator.find(entityId) should be(List(openAccountEvent, creditAccountEvent, debitAccountEvent))
    }
  }
}