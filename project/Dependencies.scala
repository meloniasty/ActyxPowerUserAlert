import sbt._

object Version {
  final val Scala     = "2.11.8"
  final val ScalaTest = "3.0.1"
  final val Play = "2.5.9"
  final val Akka = "2.4.12"
  final val ScalaLogging = "3.5.0"
  final val Logback = "1.1.7"
  final val IheartFicus = "1.3.3"
}

object Library {
  val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest
  val playWs = "com.typesafe.play" %% "play-ws" % Version.Play
  val playJson = "com.typesafe.play" %% "play-json" % Version.Play
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.Akka
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % Version.Akka
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.Akka
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % Version.Akka
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Version.Akka
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % Version.ScalaLogging
  val logback = "ch.qos.logback" %  "logback-classic" % Version.Logback
  val iheartFicus = "com.iheart" %% "ficus" % Version.IheartFicus
}
