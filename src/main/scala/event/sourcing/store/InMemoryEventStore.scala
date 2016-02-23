package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.domain.AccountEvents.{AccountSnapshotEvent, AccountEventLike}
import event.sourcing.domain.TransactionEvents.{TransactionSnapshotEvent, TransactionEventLike}
import event.sourcing.domain.{OptimisticLockException, EventLike}
import event.sourcing.entity.{Transaction, Account}
import event.sourcing.subscriber.{EventListenerLike, SimpleEventListener}

import scala.collection.mutable

/**
  * Store implementation using in memory storage with an HashMap
  */
class InMemoryEventStore extends EventStoreLike {

  case class EventsWithVersion(version: Int, events: List[EventLike]) {
    def this(version: Int, event: EventLike) = this(version, List(event))
  }

  // map a entity id to a list of events. TODO use TreeSet for sorted events
  private lazy val events = mutable.HashMap.empty[EntityId, EventsWithVersion]
  private lazy val eventDump = mutable.HashMap.empty[EntityId, EventsWithVersion]
  private lazy val snapshotThreshold = 10

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
        if (version == evs.events.length) {
          // if we are over the threshold, create a snapshot and dump the older events to another collection.
          if (newEvents.length > snapshotThreshold)
            events.put(event.entityId, new EventsWithVersion(1, snapshot(event.entityId, event, newEvents)))
          else
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
      case Some(evs) => eventDump.get(entityId).map(_.events).getOrElse(List()) ++ evs.events
      case None => throw new IllegalArgumentException("Entity doesn't exists.")
    }
  }

  override def find(entityId: EntityId, offset: Int, limit: Int): List[EventLike] =
    find(entityId).slice(offset, offset + limit)

  override def notifyListeners(e: EventLike): Unit =
    eventListeners.foreach(_.notifyEvent(e))

  /**
    * Create a snapshot of an entity, the last event is brought along to match on the type of snapshot we need to take,
    * entities are recreated from events and a snapshot is created.
    */
  def snapshot(entityId: EntityId, e: EventLike, newEvents: List[EventLike]): EventLike = {
    dumpEvents(entityId, newEvents)
    e match {
      case a: AccountEventLike =>
        AccountSnapshotEvent(entityId, new Account(entityId).replayEvents(newEvents).balance)
      case t: TransactionEventLike =>
        val transaction = new Transaction(entityId).replayEvents(newEvents)
        TransactionSnapshotEvent(entityId, transaction.from, transaction.to, transaction.amount, transaction.state)
    }
  }

  def dumpEvents(entityId: EntityId, newEvents: List[EventLike]): Unit = {
    val toDumpEvents = eventDump.get(entityId)
      .map(olderEvents => EventsWithVersion(olderEvents.version + newEvents.length, olderEvents.events ++ newEvents))
      .getOrElse(EventsWithVersion(newEvents.length, newEvents))

    eventDump.put(entityId, toDumpEvents)
  }

}