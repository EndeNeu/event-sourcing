package event.sourcing.domain

import event.sourcing.entity.Account

object TransactionCommands {
  case class TransactionCreateCommand(from: Account, to: Account, amount: Long) extends CommandLike
  case class TransactionExecuteCommand() extends CommandLike
  case class TransactionCompleteCommand() extends CommandLike
}
