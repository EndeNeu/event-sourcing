package event.sourcing.entity

import event.sourcing._
import event.sourcing.domain.EventLike

/**
  * Interface for an event handler
  */
trait EventHandlerLike[T <: EventHandlerLike[T]] {

  def entityId: EntityId

  /**
    * Event handler, a response to an event is a copy of this object
    */
  def handleEvent: HandleEvent[T]

  /**
    * Command handler, a response to a command is a list of events.
    */
  def handleCommand: HandleCommand

  /**
    * reconstruct a T from a list of events.
    *
    * @Note: the cast is necessary because the scala compiler is not smart enough to figure out
    *       that `this` is actually a T-like because of the context bound on the trait type parameter.
    */
  def replayEvents(events: List[EventLike]): T = events match {
    case event :: tail => handleEvent(event).replayEvents(tail)
    case Nil => this.asInstanceOf[T]
  }
}
