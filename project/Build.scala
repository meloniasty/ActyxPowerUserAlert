import com.typesafe.sbt.GitPlugin
import com.typesafe.sbt.GitPlugin.autoImport._
import de.heikoseeberger.sbtheader.HeaderPlugin
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import de.heikoseeberger.sbtheader.license._
import org.scalafmt.sbt.ScalaFmtPlugin
import org.scalafmt.sbt.ScalaFmtPlugin.autoImport._
import org.scalastyle.sbt.ScalastylePlugin._
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Build extends AutoPlugin {

  override def requires =
    JvmPlugin && HeaderPlugin && GitPlugin && ScalaFmtPlugin

  override def trigger = allRequirements

  override def projectSettings : Seq[sbt.Def.Setting[_]] =
    reformatOnCompileSettings ++
    Vector(
      // Core settings
      organization := "actyxpoweruseralert",
      licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
      mappings.in(Compile, packageBin) += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
      scalaVersion := Version.Scala,
      crossScalaVersions := Vector(scalaVersion.value),
      scalacOptions ++= Vector(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-target:jvm-1.8",
        "-encoding", "UTF-8"
      ),
      unmanagedSourceDirectories.in(Compile) := Vector(scalaSource.in(Compile).value),
      unmanagedSourceDirectories.in(Test) := Vector(scalaSource.in(Test).value),

      // scalafmt settings
      formatSbtFiles := false,
      scalafmtConfig := Some(baseDirectory.in(ThisBuild).value / ".scalafmt.conf"),

      // Git settings
      git.useGitDescribe := true,

      // Header settings
      headers := Map("scala" -> MIT("2016", "dawid.melewski")),

      //scalastyle-sbt-plugin
      scalastyleConfig.in(Compile) := baseDirectory.in(ThisBuild).value / "project" / "scalastyle-config.xml"
    )
}
