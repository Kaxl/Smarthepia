name := """serverREST-Smarthepia"""

organization := "ch.hepia"
version := "2016"

version := "1.0-SNAPSHOT"

lazy val root = project.in(file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "javax.inject" % "javax.inject" % "1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "com.github.nscala-time" %% "nscala-time" % "2.6.0"
)

fork in run := true
