package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.domain.EventLike
import event.sourcing.subscriber.EventListenerLike

/**
  * Event store interface, there can be multiple event stores, in memory, using databases etc.
  */
trait EventStoreLike {

  def eventListeners: List[EventListenerLike]

  // Store an event
  def update(event: EventLike): List[EventLike]

  // Find an event
  def find(entityId: EntityId): List[EventLike]

  def notifyListeners(e: EventLike): Unit

}