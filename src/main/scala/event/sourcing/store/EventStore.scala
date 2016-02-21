package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.domain.Event

/**
  * Event store interface, there can be multiple event stores, in memory, using databases etc.
  */
trait EventStore {

  def save(entityId: EntityId, event: Event): Unit

  def findOrCreate(entityId: EntityId): List[Event]

}
