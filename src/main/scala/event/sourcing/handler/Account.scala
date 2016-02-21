package event.sourcing.handler

import java.util.UUID

import event.sourcing.HandleCommand
import event.sourcing.domain.AccountEvents.{CreditAccount, OpenAccountEvent}
import event.sourcing.domain.Event

import scala.annotation.tailrec

class Account(val entityId: UUID, val balance: Long) extends EventHandler[Account] {

  /**
    * Event handling.
    */
  def handleEvent: HandleCommand[Account] = {
    case msg: OpenAccountEvent =>
      new Account(entityId, msg.initialBalance)
    case msg: CreditAccount =>
      new Account(entityId, balance - msg.credit)
  }

  /**
    * Replay an array of events recursively.
    */
  @tailrec
  final def replayEvents(events: List[Event]): Account = events match {
    case event :: tail => handleEvent(event).replayEvents(tail)
    case Nil => this
  }
}