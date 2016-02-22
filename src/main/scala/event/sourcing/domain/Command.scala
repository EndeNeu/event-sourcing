package event.sourcing.domain

import event.sourcing._

trait Command {
  def id: CommandId
}
