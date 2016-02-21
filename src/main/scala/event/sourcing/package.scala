package event

import event.sourcing.domain.Event

package object sourcing {

  type HandleCommand[T] = PartialFunction[Event, T]

}
