package event.sourcing.util

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.{ErrorEvent, Event}

import scalaz.{-\/, \/, \/-}

object DisjunctionUtil {

  def createEntityFromDisjunction[T](disj: \/[ErrorEvent, List[Event]], entityId: EntityId)(builder: (UUID, List[Event]) => T): T =
    disj match {
      case \/-(event) =>
        builder(entityId, Aggregator.update(event))
      case -\/(failureEvent) =>
        builder(entityId, Aggregator.update(failureEvent))
    }

}
