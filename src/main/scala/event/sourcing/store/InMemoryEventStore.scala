package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.domain.{OptimisticLockException, EventLike}
import event.sourcing.subscriber.{EventListenerLike, SimpleEventListener}

import scala.collection.mutable

/**
  * Store implementation using in memory storage with an HashMap
  */
class InMemoryEventStore extends EventStoreLike {

  case class EventsWithVersion(version: Int, events: List[EventLike])

  // map a entity id to a list of events. TODO use TreeSet for sorted events
  private lazy val events = mutable.HashMap.empty[EntityId, EventsWithVersion]

  // subscriber to events being stored.
  override def eventListeners: List[EventListenerLike] = List(new SimpleEventListener)

  /**
    * if the entity is in the map, append the event to the events else create a new entity.
    * For each event notify the listeners
    */
  override def update(event: EventLike): List[EventLike] =
    events.get(event.entityId) match {
      case Some(evs) =>
        val newEvents = evs.events :+ event
        val version = evs.version
        if(version == evs.events.length) {
          events.put(event.entityId, EventsWithVersion(newEvents.length, newEvents))
          notifyListeners(event)
          newEvents
        }
        else throw new OptimisticLockException
      case None => // this is basically a save.
        val newEvents = List(event)
        events.put(event.entityId, EventsWithVersion(1, newEvents))
        notifyListeners(event)
        newEvents
    }

  /**
    * Find all the events for a entity, if the entity is not in the map
    * an exception is thrown.
    */
  override def find(entityId: EntityId): List[EventLike] = {
    events.get(entityId) match {
      case Some(evs) => evs.events
      case None => throw new IllegalArgumentException("Entity doesn't exists.")
    }
  }

  override def find(entityId: EntityId, offset: Int, limit: Int): List[EventLike] =
    find(entityId).slice(offset, offset + limit)

  override def notifyListeners(e: EventLike): Unit =
    eventListeners.foreach(_.notifyEvent(e))

}