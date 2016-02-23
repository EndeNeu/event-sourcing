package event.sourcing.domain

import event.sourcing.{EntityId, EventId}
import org.joda.time.DateTime

/**
  * Interface for errors.
  */

object ErrorEvents {

  trait ErrorEventLike extends EventLike

  case class AccountInsufficientFoundEvent(val entityId: EntityId) extends ErrorEventLike
  case class AccountInsufficientFoundFromTransactionEvent(val entityId: EntityId, transactionId: EntityId) extends ErrorEventLike

}
