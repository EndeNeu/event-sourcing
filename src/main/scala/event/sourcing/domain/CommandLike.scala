package event.sourcing.domain

import event.sourcing._

/**
  * Interface for commands.
  */
trait CommandLike {
  def id: CommandId
}
