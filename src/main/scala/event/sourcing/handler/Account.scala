package event.sourcing.handler

import event.sourcing.domain.AccountEvents.{CreditAccount, DebitAccount, OpenAccountEvent}
import event.sourcing.{EntityId, HandleCommand}

class Account(val entityId: EntityId, val balance: Long) extends EventHandlerLike[Account] {

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

}