package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.aggregator.EventListenerLike
import event.sourcing.domain.Event

/**
  * Event store interface, there can be multiple event stores, in memory, using databases etc.
  */
trait EventStore {

  def eventListener: EventListenerLike

  // Store an event
  def update(event: Event): List[Event]

  // Find an event
  def find(entityId: EntityId): List[Event]

}