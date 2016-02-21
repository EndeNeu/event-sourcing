package event.sourcing

import event.sourcing.handler.Account
import event.sourcing.store.{InMemoryEventStore, Aggregator}

object Boot {

  val store = new InMemoryEventStore
  val aggregator = new Aggregator(store)

  val account = new Account(aggregator)

}
