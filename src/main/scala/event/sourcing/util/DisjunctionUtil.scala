package event.sourcing.util

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.ErrorEvents.ErrorEventLike
import event.sourcing.domain.EventLike

import scalaz.{-\/, \/, \/-}

object DisjunctionUtil {

  def createEntityFromDisjunction[T](disj: \/[ErrorEventLike, List[EventLike]], entityId: EntityId)(builder: (UUID, List[EventLike]) => T): T =
    disj match {
      case \/-(event) =>
        builder(entityId, Aggregator.update(event))
      case -\/(failureEvent) =>
        builder(entityId, Aggregator.update(failureEvent))
    }

}
