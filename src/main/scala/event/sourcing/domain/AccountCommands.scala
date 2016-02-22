package event.sourcing.domain

import event.sourcing._
import org.joda.time.DateTime

object AccountCommands {

  case class AccountOpenCommand(val id: CommandId, initialBalance: Long) extends Command
  case class AccountDebitCommand(id: EventId, debit: Long, ts: Long = DateTime.now.getMillis) extends Command
  case class AccountDebitFromTransactionCommand(id: EventId, transactionId: EntityId, debit: Long, ts: Long = DateTime.now.getMillis) extends Command
  case class AccountCreditCommand(id: EventId, credit: Long, ts: Long = DateTime.now.getMillis) extends Command
  case class AccountCreditFromTransactionCommand(id: EventId, transactionId: EntityId, credit: Long, ts: Long = DateTime.now.getMillis) extends Command
}
