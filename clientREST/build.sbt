name := "clientREST-Smarthepia"

organization := "ch.hepia"
version := "2016"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.quartz-scheduler" % "quartz" % "2.2.1",
  "org.reactivemongo" %% "reactivemongo" % "0.11.9",
  "com.typesafe.akka" %% "akka-actor" % "2.2.5",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.0"
)

fork in run := true

cancelable in Global := true

javaOptions in run += "-Xmx2G"

scalacOptions ++= Seq( "-deprecation", "-unchecked", "-Xfatal-warnings", "-Xlint" )

scalaSource in Compile <<= baseDirectory(_ / "src")

javaSource in Compile <<= baseDirectory(_ / "java" / "src" )

scalaSource in Test <<= baseDirectory(_ / "test")


