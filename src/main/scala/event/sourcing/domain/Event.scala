package event.sourcing.domain

import java.sql.Timestamp
import java.util.UUID

import org.joda.time.DateTime

trait Event {

  def id: UUID
  def ts: Long

}

case class OpenAccountEvent(id: UUID, ts: Long = DateTime.now.getMillis ) extends Event
