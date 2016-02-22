package event.sourcing.aggregator

import event.sourcing.CommonSpec
import org.scalatest.{Matchers, WordSpecLike}

class AggregatorSpec extends WordSpecLike with Matchers with CommonSpec {

  "the aggregator" should {
    "correctly throw on non existent entities" in new TestContext {
      intercept[IllegalArgumentException] {
        Aggregator.find(entityId) should be(List())
      }
    }
    "correctly update an account" in new TestContext {
      Aggregator.update(openAccountEvent)
      Aggregator.find(entityId) should be(List(openAccountEvent))
      Aggregator.update(creditAccountEvent)
      Aggregator.update(debitAccountEvent)
      Aggregator.find(entityId) should be(List(openAccountEvent, creditAccountEvent, debitAccountEvent))
    }
  }
}