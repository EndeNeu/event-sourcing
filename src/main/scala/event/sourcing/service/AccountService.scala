package event.sourcing.service

import java.util.UUID

import event.sourcing.domain.AccountEvents.OpenAccountEvent
import event.sourcing.handler.Account
import event.sourcing.store.Aggregator

/**
  * Account manager.
  */
object AccountService {

  def openAccount(initialBalance: Long): Account = {
    val accountId = UUID.randomUUID()
    Aggregator.findOrCreateAccount(accountId)
    Aggregator.updateAccount(accountId, OpenAccountEvent(UUID.randomUUID(), initialBalance))
    new Account(accountId, initialBalance)
  }

}
