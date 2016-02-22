package event.sourcing.domain

import event.sourcing._

object AccountCommands {

  case class AccountOpenCommand(initialBalance: Long) extends CommandLike

  case class AccountDebitCommand(debit: Long) extends CommandLike
  case class AccountDebitFromTransactionCommand(transactionId: EntityId, debit: Long) extends CommandLike

  case class AccountCreditCommand(credit: Long) extends CommandLike
  case class AccountCreditFromTransactionCommand(transactionId: EntityId, credit: Long) extends CommandLike
}
