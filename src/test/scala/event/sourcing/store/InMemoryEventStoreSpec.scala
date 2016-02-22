package event.sourcing.store

import event.sourcing.CommonSpec
import org.scalatest.{Matchers, WordSpecLike}

class InMemoryEventStoreSpec extends WordSpecLike with Matchers with CommonSpec {

  val store = new InMemoryEventStore()

  "InMemoryEventStore" should {
    "correctly store and find an event" in new TestContext {
      store.update(openAccountEvent)

      store.find(entityId).head should be(openAccountEvent)
      store.update(creditAccountEvent)
      store.update(debitAccountEvent)
      store.find(entityId) should be(List(openAccountEvent, creditAccountEvent, debitAccountEvent))
    }
  }
}
