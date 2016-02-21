package event.sourcing

import java.util.UUID

import event.sourcing.domain.AccountEvents.{DebitAccount, CreditAccount, OpenAccountEvent}

trait CommonSpec {
  trait TestContext {
    val entityId = UUID.randomUUID()
    val eventId = UUID.randomUUID()
    val eventId2 = UUID.randomUUID()
    val eventId3 = UUID.randomUUID()
    val event = OpenAccountEvent(eventId, 100)
    val event2 = CreditAccount(eventId2, 150)
    val event3 = DebitAccount(eventId3, 100)
  }
}