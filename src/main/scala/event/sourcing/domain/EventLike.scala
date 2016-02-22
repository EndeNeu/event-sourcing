package event.sourcing.domain

import java.util.UUID

import event.sourcing.{EntityId, EventId}
import org.joda.time.DateTime

/**
  * Interface for events.
  */
trait EventLike {
  def id: EventId = UUID.randomUUID()
  def entityId: EntityId
  def ts: Long = DateTime.now.getMillis
}



