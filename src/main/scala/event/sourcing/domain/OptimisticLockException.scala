package event.sourcing.domain

class OptimisticLockException extends Exception("Optimistic lock failed.")
