package event.sourcing.handler

import event.sourcing.HandleCommand
import event.sourcing.domain.Event

/**
  * Interface for an event handler
  */
trait EventHandler[T] {

  // event handler
  def handleEvent: HandleCommand[T]

  // reconstruct a T from a list of events.
  def replayEvents(events: List[Event]): T
}
