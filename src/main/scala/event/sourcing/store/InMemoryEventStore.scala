package event.sourcing.store

import java.util.concurrent.{Executor, ConcurrentHashMap}

import event.sourcing.EntityId
import event.sourcing.domain.EventLike
import event.sourcing.subscriber.{EventListenerLike, SimpleEventListener}

import scala.collection.mutable

/**
  * Store implementation using in memory storage with an HashMap
  */
class InMemoryEventStore extends EventStoreLike {

  // map a entity id to a list of events. TODO use TreeSet for sorted events
  private lazy val events = mutable.HashMap.empty[EntityId, List[EventLike]]

  // subscriber to events being stored.
  override def eventListeners: List[EventListenerLike] = List(new SimpleEventListener)

  /**
    * if the entity is in the map, append the event to the events else create a new entity.
    * For each event notify the listeners
    */
  override def update(event: EventLike): List[EventLike] =
    events.get(event.entityId) match {
      case Some(evs) =>
        val newEvents = evs :+ event
        events.put(event.entityId, newEvents)
        notifyListeners(event)
        newEvents
      case None =>
        val newEvents = List(event)
        events.put(event.entityId, newEvents)
        notifyListeners(event)
        newEvents
    }

  /**
    * Find all the events for a entity, if the entity is not in the map
    * an exception is thrown.
    */
  override def find(entityId: EntityId): List[EventLike] = {
    events.get(entityId) match {
      case Some(evs) => evs
      case None => throw new IllegalArgumentException("Entity doesn't exists.")
    }
  }

  override def notifyListeners(e: EventLike): Unit =
    eventListeners.foreach(_.notifyEvent(e))
}