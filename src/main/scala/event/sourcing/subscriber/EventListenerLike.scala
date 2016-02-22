package event.sourcing.subscriber

import event.sourcing.domain.EventLike

/**
  * Interface for event listeners.
  */
trait EventListenerLike {
  def notifyEvent(e: EventLike): Unit
}
