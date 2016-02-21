package event.sourcing.service

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.domain.AccountEvents.{CreditAccount, DebitAccount, OpenAccountEvent}
import event.sourcing.domain.Event
import event.sourcing.handler.Account
import event.sourcing.store.Aggregator

/**
  * Account manager.
  */
object AccountService {

  /**
    * Create a new account with an initial balance.
    */
  def openAccount(initialBalance: Long): Account = {
    val accountId = UUID.randomUUID()
    // add the opened event to this account
    recreateAccount(accountId, Aggregator.update(accountId, OpenAccountEvent(UUID.randomUUID(), initialBalance)))
  }

  /**
    * Look for an account
    */
  def findAccount(accountId: EntityId): Account =
    recreateAccount(accountId, Aggregator.find(accountId))

  /**
    * Decrease the balance of an account.
    */
  @throws[IllegalArgumentException]("Cannot have a negative balance.")
  def debitAccount(accountId: EntityId, debit: Long): Account = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))

    if (account.balance < debit) throw new IllegalArgumentException("Not enough money")
    else recreateAccount(accountId, Aggregator.update(account.entityId, DebitAccount(UUID.randomUUID(), debit)))
  }

  /**
    * Increase an account balance.
    */
  def creditAccount(accountId: EntityId, credit: Long): Account =
    recreateAccount(accountId, Aggregator.update(accountId, CreditAccount(UUID.randomUUID(), credit)))

  // Just a helper method
  private final def recreateAccount(accountId: EntityId, events: List[Event]): Account =
    new Account(accountId, 0).replayEvents(events)
}