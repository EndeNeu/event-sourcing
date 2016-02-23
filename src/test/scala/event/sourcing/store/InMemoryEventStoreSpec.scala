package event.sourcing.store

import event.sourcing.CommonSpec
import event.sourcing.domain.AccountEvents.AccountSnapshotEvent
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
    "correctly find with offset and limit" in new TestContext {
      store.update(openAccountEvent)
      store.update(creditAccountEvent)
      store.update(debitAccountEvent)
      store.find(entityId, 0, 100) should be(List(openAccountEvent, creditAccountEvent, debitAccountEvent))
      store.find(entityId, 1, 100) should be(List(creditAccountEvent, debitAccountEvent))
      store.find(entityId, 2, 100) should be(List(debitAccountEvent))
      store.find(entityId, 1, 1) should be(List(creditAccountEvent))
    }

    "correctly snapshot" in new TestContext {
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)
      store.update(creditAccountEvent)

      store.find(entityId, 11, 1).head shouldBe a[AccountSnapshotEvent]
    }
  }
}