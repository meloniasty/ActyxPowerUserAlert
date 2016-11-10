lazy val ActyxPowerUserAlert =
  project.in(file(".")).enablePlugins(AutomateHeaderPlugin, GitVersioning)

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Vector(
  Library.akkaActor,
  Library.akkaContrib,
  Library.akkaSlf4j,
  Library.akkaStream,
  Library.playWs,
  Library.playJson,
  Library.iheartFicus,
  Library.logback,
  Library.scalaLogging,
  Library.scalaTest % "test",
  Library.akkaTestkit % "test"
)

initialCommands := """|import it.impossible.actyxpoweruseralert._
                      |""".stripMargin

addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.16")
