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
      store.save(entityId, openAccountEvent)

      store.find(entityId).head should be(openAccountEvent)
      store.save(entityId, creditAccountEvent)
      store.save(entityId, debitAccountEvent)
      store.find(entityId) should be(List(openAccountEvent, creditAccountEvent, debitAccountEvent))
    }
  }
}
