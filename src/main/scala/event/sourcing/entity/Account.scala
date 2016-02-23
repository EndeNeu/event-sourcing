package event.sourcing.entity

import event.sourcing.domain.AccountCommands._
import event.sourcing.domain.AccountEvents._
import event.sourcing.domain.ErrorEvents.{AccountInsufficientFoundEvent, AccountInsufficientFoundFromTransactionEvent}
import event.sourcing.{EntityId, HandleCommand, HandleEvent}

import scalaz.{-\/, \/-}

class Account private(val entityId: EntityId, val balance: Long) extends EventHandlerLike[Account] {

  def this(entityId: EntityId) = this(entityId, null.asInstanceOf[Long])

  /**
    * Command handler, a response to a command is a list of events.
    */
  override def handleCommand: HandleCommand = {
    case AccountOpenCommand(initialBalance) =>
      \/-(List(AccountOpenEvent(entityId, initialBalance)))

    case AccountDebitCommand(debit) =>
      if (balance > debit) \/-(List(AccountDebitEvent(entityId, debit)))
      else -\/(AccountInsufficientFoundEvent(entityId))

    case AccountCreditCommand(credit) =>
      \/-(List(AccountCreditEvent(entityId, credit)))

    case AccountDebitFromTransactionCommand(transactionId, debit) =>
      if (balance > debit) \/-(List(AccountDebitFromTransactionEvent(entityId, transactionId, debit)))
      else -\/(AccountInsufficientFoundFromTransactionEvent(entityId, transactionId))

    case AccountCreditFromTransactionCommand(transactionId, credit) =>
      \/-(List(AccountCreditFromTransactionEvent(entityId, transactionId, credit)))

    case AccountSnapshotCommand(_) =>
      \/-(List(AccountSnapshotEvent(entityId, balance)))

    case _ =>
      throw new IllegalArgumentException("Unknown command in account.")
  }

  /**
    * Event handler, a response to an event is a copy of this object
    */
  override def handleEvent: HandleEvent[Account] = {
    case AccountOpenEvent(_, initialBalance) =>
      new Account(entityId, initialBalance)

    case AccountCreditEvent(_, credit) =>
      new Account(entityId, balance + credit)

    case AccountDebitEvent(_, debit) =>
      new Account(entityId, balance - debit)

    case AccountCreditFromTransactionEvent(_, _, credit) =>
      new Account(entityId, balance + credit)

    case AccountDebitFromTransactionEvent(_, _, debit) =>
      new Account(entityId, balance - debit)

    case msg: AccountInsufficientFoundEvent =>
      this

    case msg: AccountInsufficientFoundFromTransactionEvent =>
      this

    case AccountSnapshotEvent(_, _balance) =>
      new Account(entityId, _balance)

    case _ =>
      throw new IllegalArgumentException("Unknown event in transaction.")
  }

}