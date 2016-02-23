package event.sourcing.util

import java.util.UUID

import event.sourcing.EntityId
import event.sourcing.aggregator.Aggregator
import event.sourcing.domain.ErrorEvents.ErrorEventLike
import event.sourcing.domain.EventLike

import scalaz.{-\/, \/, \/-}

object DisjunctionUtil {

  /**
    * Utility method to create an event from a disjunction
    *
    * @param builder: a function that takes a entity id and a list of events and replays those events
    *               creating an entity
    */
  def createEntityFromDisjunction[T](disj: \/[ErrorEventLike, List[EventLike]], entityId: EntityId)(builder: (EntityId, List[EventLike]) => T): T =
    disj match {
      case \/-(event) =>
        builder(entityId, Aggregator.updateOrInsert(event))
      case -\/(failureEvent) =>
        builder(entityId, Aggregator.updateOrInsert(failureEvent))
    }

}
