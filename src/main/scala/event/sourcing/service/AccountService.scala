package event.sourcing.service

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.AccountCommands._
import event.sourcing.domain.ErrorEvents.ErrorEventLike
import event.sourcing.domain.EventLike
import event.sourcing.entity.Account
import event.sourcing.util.DisjunctionUtil

import scalaz.{-\/, \/, \/-}

/**
  * Account manager.
  */
object AccountService {

  /**
    * Create a new account with an initial balance.
    */
  def openAccount(command: AccountOpenCommand): Account = {
    val account = new Account(UUID.randomUUID())
    DisjunctionUtil.createEntityFromDisjunction(account.handleCommand(command), account.entityId)(recreateAccount)
  }

  /**
    * Look for an account finding all the events and replaying
    */
  def findAccount(accountId: EntityId): Account =
    recreateAccount(accountId, Aggregator.find(accountId))

  /**
    * Store an event to decrease the balance of an account, the new account is returned.
    */
  def debitAccount(accountId: EntityId, command: AccountDebitCommand): Account = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))
    DisjunctionUtil.createEntityFromDisjunction(account.handleCommand(command), account.entityId)(recreateAccount)
  }

  /**
    * Store an event to decrease the balance of an account when a transaction is executed, the new account is returned.
    */
  def debitAccountFromTransfer(accountId: EntityId, command: AccountDebitFromTransactionCommand): \/[ErrorEventLike, Account] = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))
    account.handleCommand(command) match {
      case \/-(successEvent) =>
        \/-(recreateAccount(accountId, Aggregator.update(successEvent)))
      case failure @ -\/(_) =>
        failure
    }
  }

  /**
    * Store an event to increase the balance of an account the new account is returned.
    */
  def creditAccount(accountId: EntityId, command: AccountCreditCommand): Account = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))
    DisjunctionUtil.createEntityFromDisjunction(account.handleCommand(command), account.entityId)(recreateAccount)
  }

  /**
    * Store an event to increase the balance of an account when a transaction is executed, the new account is returned.
    */
  def creditAccountFromTransfer(accountId: EntityId, command: AccountCreditFromTransactionCommand): \/[ErrorEventLike, Account] = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))
    account.handleCommand(command) match {
      case \/-(event) =>
        \/-(recreateAccount(accountId, Aggregator.update(event)))
      case failure @ -\/(_) =>
        failure
    }
  }

  /**
    * Just a helper method, replays the events for an entity.
    */
  private final def recreateAccount(accountId: EntityId, events: List[EventLike]): Account =
    new Account(accountId).replayEvents(events)

}