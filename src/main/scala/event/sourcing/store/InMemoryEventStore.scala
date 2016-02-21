package event.sourcing.store

import java.util.UUID

import event.sourcing.domain.Event

import scala.collection.mutable

class InMemoryEventStore extends EventStore {

  val events = mutable.HashMap.empty[UUID, Event]

  override def save(event: Event): Event =
    events.put(event.id, event).getOrElse(event)

  override def find(id: UUID): Option[Event] =
    events.get(id)
}