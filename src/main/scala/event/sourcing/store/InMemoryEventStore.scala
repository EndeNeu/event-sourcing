package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.domain.Event

import scala.collection.mutable

class InMemoryEventStore extends EventStore {

  // map a entity id to a list of events. TODO use TreeSet for sorted events
  private lazy val events = mutable.HashMap.empty[EntityId, List[Event]]

  /**
    * if the entity is in the map, append the event to the events
    * else create a new entity.
    */
  override def save(entityId: EntityId, event: Event): List[Event] =
    events.get(entityId) match {
      case Some(evs) =>
        val newEvents = evs :+ event
        events.put(entityId, newEvents)
        newEvents
      case None =>
        events.put(entityId, List(event))
        List(event)
    }

  /**
    * Find all the events for a entity, if the entity is not in the map
    * it gets added.
    */
  override def find(entityId: EntityId): List[Event] = {
    events.get(entityId) match {
      case Some(evs) =>
        evs
      case None =>
        events.put(entityId, List())
        List()
    }
  }
}