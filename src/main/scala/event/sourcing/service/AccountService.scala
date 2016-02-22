package event.sourcing.service

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.AccountCommands._
import event.sourcing.domain.{ErrorEvent, Event}
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
    val accountCreatedEvent = account.handleCommand(command)

    DisjunctionUtil.createEntityFromDisjunction(account.handleCommand(command), account.entityId)(recreateAccount)
  }

  /**
    * Look for an account
    */
  def findAccount(accountId: EntityId): Account =
    recreateAccount(accountId, Aggregator.find(accountId))

  /**
    * Decrease the balance of an account.
    */
  def debitAccount(accountId: EntityId, command: AccountDebitCommand): Account = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))
    DisjunctionUtil.createEntityFromDisjunction(account.handleCommand(command), account.entityId)(recreateAccount)
  }

  def debitAccountFromTransfer(accountId: EntityId, command: AccountDebitFromTransactionCommand): \/[ErrorEvent, Account] = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))
    println("acc")
    println(account.balance)
    account.handleCommand(command) match {
      case \/-(successEvent) =>
        \/-(recreateAccount(accountId, Aggregator.update(successEvent)))
      case failure @ -\/(_) =>
        failure
    }
  }

  /**
    * Increase an account balance.
    */
  def creditAccount(accountId: EntityId, command: AccountCreditCommand): Account = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))
    DisjunctionUtil.createEntityFromDisjunction(account.handleCommand(command), account.entityId)(recreateAccount)
  }

  def creditAccountFromTransfer(accountId: EntityId, command: AccountCreditFromTransactionCommand): \/[ErrorEvent, Account] = {
    val account = recreateAccount(accountId, Aggregator.find(accountId))
    account.handleCommand(command) match {
      case \/-(event) =>
        \/-(recreateAccount(accountId, Aggregator.update(event)))
      case failure @ -\/(_) =>
        failure
    }
  }


  // Just a helper method
  private final def recreateAccount(accountId: EntityId, events: List[Event]): Account =
    new Account(accountId).replayEvents(events)

}