package event.sourcing.handler

import event.sourcing._
import event.sourcing.domain.Event

/**
  * Interface for an event handler
  */
trait EventHandlerLike[T <: EventHandlerLike[T]] {

  def entityId: EntityId

  // event handler
  def handleEvent: HandleCommand[T]

  /**
    * reconstruct a T from a list of events.
    *
    * @Note: the cast is necessary because the scala compiler is not smart enough to figure out
    *       that `this` is actually a T-like because of the context bound on the trait type parameter.
    */
  def replayEvents(events: List[Event]): T = events match {
    case event :: tail => handleEvent(event).replayEvents(tail)
    case Nil => this.asInstanceOf[T]
  }
}
