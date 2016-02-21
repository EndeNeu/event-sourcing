package event.sourcing.handler

import event.sourcing.HandleCommand

trait EventHandler[T] {

  def handleCommand: HandleCommand[T]

}
