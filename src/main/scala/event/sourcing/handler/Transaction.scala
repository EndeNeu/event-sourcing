package event.sourcing.handler

import event.sourcing._
import event.sourcing.domain.TransactionEvents.{CreateTransactionEvent, TransactionCreated, TransactionState}

class Transaction(val entityId: EntityId, val from: Option[Account], val to: Option[Account], val amount: Option[Long], val state: TransactionState) extends EventHandlerLike[Transaction] {

  override def handleEvent: HandleCommand[Transaction] = {
    case CreateTransactionEvent(_, _from, _to, _amount, _state, _) =>
      new Transaction(entityId, Some(_from), Some(_to), Some(_amount), _state)
  }

}