package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.domain.AccountEvents.OpenAccountEvent
import event.sourcing.domain.Event

import scala.collection.mutable

class InMemoryEventStore extends EventStore {

  // map a entity id to a list of events.
  val events = mutable.HashMap.empty[EntityId, List[Event]]

  /**
    * if the entity is in the map, update that entity events with this new event
    * else create a new entity.
    */
  override def save(entityId: EntityId, event: Event): Unit =
    events.get(entityId) match {
      case Some(evs) => events.put(entityId, evs :+ event)
      case None => events.put(entityId, List(event))
    }

  override def findOrCreate(entityId: EntityId): List[Event] = {
    events.get(entityId) match {
      case Some(evs) =>
        evs
      case None =>
        events.put(entityId, List())
        List()
    }
  }
}