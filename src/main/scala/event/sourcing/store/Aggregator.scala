package event.sourcing.store

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.domain.Event
import event.sourcing.handler.Account

/**
  * Aggregates event requests and interfaces with the event store.
  */
object Aggregator {

  // the event store, can change based on configurations.
  private lazy val eventStore: EventStore = new InMemoryEventStore

  /**
    * find all the events for an entity
    */
  def find(entityId: EntityId): List[Event] =
    eventStore.find(entityId)

  /**
    * Store the new event and return all the events.
    */
  def update(entityId: EntityId, event: Event): List[Event] =
    eventStore.save(entityId, event)

  /**
    * Helper method.
    */
  private def createAccount(entityId: EntityId = UUID.randomUUID()): Account = {
    new Account(entityId, 0)
  }
}