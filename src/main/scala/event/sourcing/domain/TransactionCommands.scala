package event.sourcing.domain

import event.sourcing.CommandId
import event.sourcing.entity.Account
import org.joda.time.DateTime

object TransactionCommands {
  case class TransactionCreateCommand(id: CommandId, from: Account, to: Account, amount: Long, ts: Long = DateTime.now.getMillis) extends CommandLike
  case class TransactionExecuteCommand(id: CommandId, ts: Long = DateTime.now.getMillis) extends CommandLike
  case class TransactionCompleteCommand(id: CommandId, ts: Long = DateTime.now.getMillis) extends CommandLike
}
