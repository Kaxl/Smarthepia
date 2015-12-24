organization := "ch.hepia"

name := "client"

version := "2015"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.reactivemongo" %% "reactivemongo" % "0.11.9",
  "io.spray" %%  "spray-json" % "1.3.2",
  "net.liftweb" %% "lift-json" % "2.5+"
)


fork in run := true

javaOptions in run += "-Xmx2G"

scalacOptions ++= Seq( "-deprecation", "-unchecked", "-Xfatal-warnings", "-Xlint" )

scalaSource in Compile <<= baseDirectory(_ / "src")

javaSource in Compile <<= baseDirectory(_ / "java" / "src" )

scalaSource in Test <<= baseDirectory(_ / "test")


