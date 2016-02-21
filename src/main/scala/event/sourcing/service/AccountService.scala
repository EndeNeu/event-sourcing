package event.sourcing.service

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.domain.AccountEvents.{CreditAccount, OpenAccountEvent}
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

  def creditAccount(accountId: EntityId, credit: Long): Account = {
    val account = Aggregator.findOrCreateAccount(accountId)

    if(account.balance < credit) throw new IllegalArgumentException("Not enough money")
    else Aggregator.updateAccount(account.entityId, CreditAccount(UUID.randomUUID(), credit))
  }
}