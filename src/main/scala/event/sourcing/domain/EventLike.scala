package event.sourcing.domain

import event.sourcing.{EntityId, EventId}

/**
  * Interface for events.
  */
trait EventLike {
  def id: EventId
  def entityId: EntityId
  def ts: Long
}



