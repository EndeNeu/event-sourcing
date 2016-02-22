package event.sourcing.aggregator

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.domain.EventLike
import event.sourcing.entity.Account
import event.sourcing.store.{EventStoreLike, InMemoryEventStore}

/**
  * Aggregates event requests and interfaces with the event store.
  */
object Aggregator {

  // the event store, can change based on configurations.
  private lazy val eventStore: EventStoreLike = new InMemoryEventStore

  /**
    * find all the events for an entity
    */
  def find(entityId: EntityId): List[EventLike] =
    eventStore.find(entityId)

  /**
    * Store the new event and return all the events.
    */
  def update(event: EventLike): List[EventLike] =
    eventStore.update(event)

  /**
    * Store a list of events and return all the events.
    */
  def update(event: List[EventLike]): List[EventLike] =
    event.flatMap(e => eventStore.update(e))

}