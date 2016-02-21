package event.sourcing.handler

import event.sourcing.HandleCommand
import event.sourcing.store.Aggregator

class Account(aggregator: Aggregator) extends EventHandler[Account] {

  def handleCommand: HandleCommand[Account] = {
    case _ => this
  }
}
