package event.sourcing.aggregator

import event.sourcing.domain.Event

trait EventListenerLike {
  def notifyEvent(e: Event): Unit
}
