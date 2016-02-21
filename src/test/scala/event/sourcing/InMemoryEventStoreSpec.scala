package event.sourcing

import java.util.UUID

import event.sourcing.domain.AccountEvents.{DebitAccount, CreditAccount, OpenAccountEvent}
import event.sourcing.store.InMemoryEventStore
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class InMemoryEventStoreSpec extends WordSpecLike with Matchers with CommonSpec {

  val store = new InMemoryEventStore()

  "InMemoryEventStore" should {
    "correctly store and find an event" in new TestContext {
      store.save(entityId, event)

      store.find(entityId).head should be(event)
      store.save(entityId, event2)
      store.save(entityId, event3)
      store.find(entityId) should be(List(event, event2, event3))
    }
  }
}
