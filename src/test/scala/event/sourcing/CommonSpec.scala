package event.sourcing

import java.util.UUID

trait CommonSpec {
  trait TestContext {
    val entityId = UUID.randomUUID()
  }
}
