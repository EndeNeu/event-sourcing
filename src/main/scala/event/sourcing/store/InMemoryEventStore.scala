package event.sourcing.store

import event.sourcing.EntityId
import event.sourcing.domain.AccountCommands.AccountSnapshotCommand
import event.sourcing.domain.AccountEvents.AccountEventLike
import event.sourcing.domain.TransactionCommands.TransactionSnapshotCommand
import event.sourcing.domain.TransactionEvents.TransactionEventLike
import event.sourcing.domain.{EventLike, OptimisticLockException}
import event.sourcing.entity.{Account, Transaction}
import event.sourcing.subscriber.{EventListenerLike, SimpleEventListener}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * Store implementation using in memory storage with an HashMap
  */
class InMemoryEventStore(implicit val executionContext: ExecutionContext) extends EventStoreLike with EventStoreJournaledLike {

  case class EventsWithVersion(version: Int, events: List[EventLike]) {
    def this(version: Int, event: EventLike) = this(version, List(event))
  }

  // map a entity id to a list of events.
  private lazy val events = mutable.HashMap.empty[EntityId, EventsWithVersion]
  // store all the older events for future search.
  private lazy val eventDump = mutable.HashMap.empty[EntityId, EventsWithVersion]
  // after how many events we need to snapshot.
  private lazy val snapshotThreshold = 10

  // subscriber to events being stored.
  override def eventListeners: List[EventListenerLike] =
    List(new SimpleEventListener)

  /**
    * if the entity is in the map, append the event to the events else create a new entity.
    * For each event notify the listeners
    */
  override def updateOrInsert(event: EventLike): List[EventLike] =
    events.get(event.entityId) match {
      case Some(evs) => // if there are already some events for this entity
        val newEvents = evs.events :+ event // append the new event
        if (newEvents.length > snapshotThreshold)
          putWitOptimisticLock(event.entityId, new EventsWithVersion(1, snapshot(event.entityId, event, newEvents)), evs.events)
        else
          putWitOptimisticLock(event.entityId, EventsWithVersion(newEvents.length, newEvents), evs.events)

        // notify the listeners that a new event has happened
        notifyListeners(event)
        newEvents
      case None =>
        // this is basically a save.
        val newEvents = List(event)
        putWitOptimisticLock(event.entityId, EventsWithVersion(1, newEvents), newEvents)
        notifyListeners(event)
        newEvents
    }

  /**
    * Check that the version we are processing has not been changed, if so throw an exception.
    * if it's an insert, use 1 since on insert for a new entity 1 event only is used (the getOrElse part)
    */
  def putWitOptimisticLock(entityId: EntityId, eventWitVersion: EventsWithVersion, oldEvents: List[EventLike]) =
    if (events.get(entityId).map(_.version).getOrElse(1) == oldEvents.length) events.put(entityId, eventWitVersion)
    else throw new OptimisticLockException

  /**
    * Find all the latest events for a entity, if the entity is not in the map
    * an exception is thrown.
    */
  override def find(entityId: EntityId): List[EventLike] = {
    events.get(entityId) match {
      case Some(evs) => evs.events
      case None => throw new IllegalArgumentException("Entity doesn't exists.")
    }
  }

  /**
    * Finds all the events for an entity with an offset and a limit
    */
  override def find(entityId: EntityId, offset: Int, limit: Int): List[EventLike] =
    find(entityId).slice(offset, offset + limit)

  /**
    * Find all the events for an entity, included dumped ones.
    */
  override def findAllEvents(entityId: EntityId): List[EventLike] =
    eventDump.get(entityId).map(_.events).getOrElse(List()) ++ find(entityId)

  override def notifyListeners(e: EventLike): Unit =
    eventListeners.foreach(listener => Future(listener.notifyEvent(e)))

  /**
    * Create a snapshot of an entity, the last event is brought along to match on the type of snapshot we need to take,
    * entities are recreated from events and a snapshot is created and stored overwriting the list of events we had.
    */
  override def snapshot(entityId: EntityId, e: EventLike, newEvents: List[EventLike]): List[EventLike] = {
    dumpEvents(entityId, newEvents)
    e match {
      case a: AccountEventLike =>
        // fail fast in case snapshotting doesn't work
        new Account(entityId).replayEvents(newEvents).handleCommand(AccountSnapshotCommand(entityId)).toOption.get
      case t: TransactionEventLike =>
        // fail fast in case snapshotting doesn't work
        new Transaction(entityId).replayEvents(newEvents).handleCommand(TransactionSnapshotCommand(entityId)).toOption.get
    }
  }

  /**
    * Dump the events to a secondary collection to avoid loosing track of an account movements.
    */
  override def dumpEvents(entityId: EntityId, newEvents: List[EventLike]): Unit = {
    // if there are already some events, chain them to this batch.
    val toDumpEvents = eventDump.get(entityId)
      .map(olderEvents => EventsWithVersion(olderEvents.version + newEvents.length, olderEvents.events ++ newEvents))
      .getOrElse(EventsWithVersion(newEvents.length, newEvents))

    eventDump.put(entityId, toDumpEvents)
  }

}