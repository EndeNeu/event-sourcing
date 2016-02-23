package event

import java.util.UUID

import event.sourcing.domain.ErrorEvents.ErrorEventLike
import event.sourcing.domain.{CommandLike, EventLike}

import scalaz.\/

package object sourcing {
  type HandleEvent[T] = PartialFunction[EventLike, T]
  type HandleCommand = PartialFunction[CommandLike, \/[ErrorEventLike, List[EventLike]]]
  type EntityId = UUID
  type EventId = UUID
  type CommandId = UUID

}
