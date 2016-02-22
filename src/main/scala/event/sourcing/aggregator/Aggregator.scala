package event.sourcing.aggregator

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.domain.Event
import event.sourcing.entity.Account
import event.sourcing.store.{EventStore, InMemoryEventStore}

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
  def update(event: Event): List[Event] =
    eventStore.update(event)

  def update(event: List[Event]): List[Event] =
    event.flatMap(e => eventStore.update(e))

  /**
    * Helper method.
    */
  private def createAccount(entityId: EntityId = UUID.randomUUID()): Account = {
    new Account(entityId, 0)
  }
}