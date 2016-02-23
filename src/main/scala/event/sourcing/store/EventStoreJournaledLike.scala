package event.sourcing.store

import event.sourcing._
import event.sourcing.domain.EventLike

/**
  * A Journaled store must take care not to discard events and store them somewhere
  */
trait EventStoreJournaledLike {

  def dumpEvents(entityId: EntityId, newEvents: List[EventLike]): Unit

}