package event.sourcing.handler

import event.sourcing.domain.AccountEvents.{CreditAccount, DebitAccount, OpenAccountEvent}
import event.sourcing.domain.Event
import event.sourcing.{EntityId, HandleCommand}

import scala.annotation.tailrec

class Account(val entityId: EntityId, val balance: Long) extends EventHandler[Account] {

  /**
    * Event handling.
    */
  override def handleEvent: HandleCommand[Account] = {
    case msg: OpenAccountEvent =>
      new Account(entityId, msg.initialBalance)
    case msg: DebitAccount =>
      new Account(entityId, balance - msg.debit)
    case msg: CreditAccount =>
      new Account(entityId, balance + msg.credit)
  }

  /**
    * Replay an array of events recursively.
    */
  @tailrec
  override final def replayEvents(events: List[Event]): Account = events match {
    case event :: tail => handleEvent(event).replayEvents(tail)
    case Nil => this
  }
}