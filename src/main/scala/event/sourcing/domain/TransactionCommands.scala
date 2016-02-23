package event.sourcing.domain

import event.sourcing._
import event.sourcing.entity.Account

/**
  * All transaction commands.
  */
object TransactionCommands {
  case class TransactionCreateCommand(from: Account, to: Account, amount: Long) extends CommandLike
  case class TransactionExecuteCommand() extends CommandLike
  case class TransactionCompleteCommand() extends CommandLike

  case class TransactionSnapshotCommand(entityId: EntityId) extends CommandLike

}