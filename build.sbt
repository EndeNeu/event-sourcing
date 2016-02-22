name := "event-sourcing-cqrs"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "joda-time" % "joda-time" % "2.7"

libraryDependencies += "org.scalatest"      %% "scalatest" % "2.2.2" % "test" withSources()

libraryDependencies += "org.scalaz"         %% "scalaz-core" % "7.1.1" withSources()