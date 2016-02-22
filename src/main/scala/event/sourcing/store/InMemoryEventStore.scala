package event.sourcing.store

import java.util.concurrent.{Executor, ConcurrentHashMap}

import event.sourcing.EntityId
import event.sourcing.aggregator.EventListenerLike
import event.sourcing.domain.Event
import event.sourcing.subscriber.SimpleEventListener

import scala.collection.mutable

class InMemoryEventStore extends EventStore {

  // map a entity id to a list of events. TODO use TreeSet for sorted events
  private lazy val events = mutable.HashMap.empty[EntityId, List[Event]]

  // subscriber to events being stored.
  override def eventListener: EventListenerLike = new SimpleEventListener

  /**
    * if the entity is in the map, append the event to the events
    * else create a new entity.
    */
  override def update(event: Event): List[Event] =
    events.get(event.entityId) match {
      case Some(evs) =>
        val newEvents = evs :+ event
        events.put(event.entityId, newEvents)
        eventListener.notifyEvent(event)
        newEvents.sortBy(_.ts)
      case None =>
        val newEvents = List(event)
        events.put(event.entityId, newEvents)
        eventListener.notifyEvent(event)
        newEvents.sortBy(_.ts)
    }

  /**
    * Find all the events for a entity, if the entity is not in the map
    * an exception is thrown.
    */
  override def find(entityId: EntityId): List[Event] = {
    events.get(entityId) match {
      case Some(evs) => evs
      case None => throw new IllegalArgumentException("Entity doesn't exists.")
    }
  }

}