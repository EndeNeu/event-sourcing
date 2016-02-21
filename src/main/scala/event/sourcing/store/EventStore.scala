package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.domain.Event

/**
  * Event store interface, there can be multiple event stores, in memory, using databases etc.
  */
trait EventStore {

  // Store an event
  def save(entityId: EntityId, event: Event): List[Event]

  // Find an event
  def find(entityId: EntityId): List[Event]

}
