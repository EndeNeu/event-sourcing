package event

import java.util.UUID

import event.sourcing.domain.Event

package object sourcing {
  type HandleCommand[T] = PartialFunction[Event, T]
  type EntityId = UUID
  type EventId = UUID
}
