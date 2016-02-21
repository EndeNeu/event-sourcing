package event.sourcing.store

import java.util.UUID

import event.sourcing.domain.Event

trait EventStore {

  def save(event: Event): Event

  def find(id: UUID): Option[Event]

}
