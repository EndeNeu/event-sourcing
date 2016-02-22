package event.sourcing.domain

import event.sourcing.{EntityId, EventId}

/**
  * Events superclass.
  */
trait Event {
  def id: EventId
  def entityId: EntityId
  def ts: Long
}



