package event.sourcing.store

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.domain.AccountEvents.OpenAccountEvent
import event.sourcing.domain.Event
import event.sourcing.handler.Account

/**
  * Aggregates event requests and interfaces with the event store.
  */
object Aggregator {

  // the event store, can change based on configurations.
  private lazy val eventStore: EventStore = new InMemoryEventStore

  /**
    * find an account by entity in the event store, create one and replay all the events.
    */
  def findOrCreateAccount(entityId: EntityId): Account = {
    val toReplay = eventStore.findOrCreate(entityId)
    createAccount(entityId).replayEvents(toReplay)
  }

  /**
    * Store the new event and replay.
    */
  def updateAccount(entityId: EntityId, event: Event): Account = {
    eventStore.save(entityId, event)
    createAccount(entityId).replayEvents(eventStore.findOrCreate(entityId))
  }

  /**
    * Helper method.
    */
  private def createAccount(entityId: EntityId = UUID.randomUUID()): Account = {
    new Account(entityId, 0)
  }
}