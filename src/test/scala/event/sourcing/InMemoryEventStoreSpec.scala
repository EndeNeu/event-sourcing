package event.sourcing

import java.util.UUID

import event.sourcing.domain.AccountEvents.{DebitAccount, CreditAccount, OpenAccountEvent}
import event.sourcing.store.InMemoryEventStore
import org.scalatest.{Matchers, WordSpecLike}

class InMemoryEventStoreSpec extends WordSpecLike with Matchers with CommonSpec {

  val store = new InMemoryEventStore()

  "InMemoryEventStore" should {
    "correctly store and find an event" in new TestContext {
      val eventId = UUID.randomUUID()
      val eventId2 = UUID.randomUUID()
      val eventId3 = UUID.randomUUID()
      val event = OpenAccountEvent(eventId, 100)
      val event2 = CreditAccount(eventId2, 150)
      val event3 = DebitAccount(eventId3, 100)
      store.save(entityId, event)

      store.findOrCreate(entityId).head should be(event)
      store.save(entityId, event2)
      store.save(entityId, event3)
      store.findOrCreate(entityId) should be(List(event, event2, event3))
    }
  }

}
