package event

import java.util.UUID

import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.{Command, ErrorEvent, Event}

import scalaz.{-\/, \/, \/-}

package object sourcing {
  type HandleEvent[T] = PartialFunction[Event, T]
  type HandleCommand = PartialFunction[Command, \/[ErrorEvent, List[Event]]]
  type EntityId = UUID
  type EventId = UUID
  type CommandId = UUID

}
