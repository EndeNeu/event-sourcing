package event.sourcing.entity

import java.util.UUID

import event.sourcing.domain.AccountCommands._
import event.sourcing.domain.AccountEvents._
import event.sourcing.domain.{AccountInsufficientFoundEvent, AccountInsufficientFoundFromTransactionEvent}
import event.sourcing.{EntityId, HandleCommand, HandleEvent}

import scalaz.{-\/, \/-}

class Account private (val entityId: EntityId, val balance: Long) extends EventHandlerLike[Account] {

  def this(entityId: EntityId) = this(entityId, null.asInstanceOf[Long])

  /**
    * Command handler, a response to a command is a list of events.
    */
  override def handleCommand: HandleCommand = {
    case msg: AccountOpenCommand =>
      \/-(List(AccountOpenEvent(UUID.randomUUID(), entityId, msg.initialBalance)))

    case msg: AccountDebitCommand =>
      if (balance > msg.debit) \/-(List(AccountDebitEvent(UUID.randomUUID(), entityId, msg.debit)))
      else -\/(AccountInsufficientFoundEvent(UUID.randomUUID(), entityId))

    case msg: AccountCreditCommand =>
      \/-(List(AccountCreditEvent(UUID.randomUUID(), entityId, msg.credit)))

    case msg: AccountDebitFromTransactionCommand =>
      if (balance > msg.debit) \/-(List(AccountDebitFromTransferEvent(UUID.randomUUID(), entityId, msg.transactionId, msg.debit)))
      else -\/(AccountInsufficientFoundFromTransactionEvent(UUID.randomUUID(), entityId, msg.transactionId))

    case msg: AccountCreditFromTransactionCommand =>
      \/-(List(AccountCreditFromTransferEvent(UUID.randomUUID(), entityId, msg.transactionId, msg.credit)))

    case _ =>
      throw new IllegalArgumentException("Unknown command in account.")
  }

  /**
    * Event handler, a response to an event is a copy of this object
    */
  override def handleEvent: HandleEvent[Account] = {
    case msg: AccountOpenEvent =>
      new Account(entityId, msg.initialBalance)

    case msg: AccountCreditEvent =>
      new Account(entityId, balance + msg.credit)

    case msg: AccountDebitEvent =>
      new Account(entityId, balance - msg.debit)

    case msg: AccountCreditFromTransferEvent =>
      new Account(entityId, balance + msg.credit)

    case msg: AccountDebitFromTransferEvent =>
      new Account(entityId, balance - msg.debit)

    case msg: AccountInsufficientFoundEvent =>
      this

    case msg: AccountInsufficientFoundFromTransactionEvent =>
      this

    case _ =>
      throw new IllegalArgumentException("Unknown event in transaction.")
  }

}