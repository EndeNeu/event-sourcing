package event.sourcing.service

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.domain.AccountEvents.{CreditAccount, DebitAccount, OpenAccountEvent}
import event.sourcing.handler.Account
import event.sourcing.store.Aggregator

/**
  * Account manager.
  */
object AccountService {

  /**
    * Create a new account with an inital balance.
    */
  def openAccount(initialBalance: Long): Account = {
    val accountId = UUID.randomUUID()
    // add the opened event to this account
    Aggregator.updateAccount(accountId, OpenAccountEvent(UUID.randomUUID(), initialBalance))
  }

  /**
    * Decrease the balance of an account.
    */
  @throws[IllegalArgumentException]("Cannot have a negative balance.")
  def debitAccount(accountId: EntityId, debit: Long): Account = {
    val account = Aggregator.findOrCreateAccount(accountId)

    if(account.balance < debit) throw new IllegalArgumentException("Not enough money")
    else Aggregator.updateAccount(account.entityId, DebitAccount(UUID.randomUUID(), debit))
  }

  /**
    * Increase an account balance.
    */
  def creditAccount(accountId: EntityId, credit: Long): Account = {
    Aggregator.updateAccount(accountId, CreditAccount(UUID.randomUUID(), credit))
  }
}