organization := "ch.hepia"

name := "client"

version := "2015"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.reactivemongo" %% "reactivemongo" % "0.11.9",
  "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT"
)

fork in run := true

javaOptions in run += "-Xmx2G"

scalacOptions ++= Seq( "-deprecation", "-unchecked", "-Xfatal-warnings", "-Xlint" )

scalaSource in Compile <<= baseDirectory(_ / "src")

javaSource in Compile <<= baseDirectory(_ / "java" / "src" )

scalaSource in Test <<= baseDirectory(_ / "test")


