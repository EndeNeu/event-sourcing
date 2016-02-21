package event.sourcing.handler

import event.sourcing.HandleCommand

/**
  * Interface for an event handler
  */
trait EventHandler[T] {

  def handleEvent: HandleCommand[T]

}
