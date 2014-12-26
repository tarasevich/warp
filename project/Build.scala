import sbt._
import Keys._

import sbtassembly.AssemblyPlugin.autoImport._

import com.typesafe.sbt.SbtGhPages.ghpages
import com.typesafe.sbt.SbtSite.site
import com.typesafe.sbt.SbtGit.git

object Build extends Build{
  //import sbtassembly.Plugin._
  import Dependencies._

  val SCALA_MAJOR_VERSION = "2.11"
  val SCALA_MINOR_VERSION = "4"
  val SCALA_VERSION = SCALA_MAJOR_VERSION + "." + SCALA_MINOR_VERSION

  val WARP_VERSION = "0.5"
  val SCALAVRO_VERSION = "0.6.2-patched"

  val loggingLibs = Seq(scala_logging, logback_core, logback)


  def warpCommonSettings(projectName: String) =
    Defaults.coreDefaultSettings ++
    ghpages.settings ++
    site.settings ++
    site.includeScaladoc("latest/api/" + projectName) ++
    Seq(
      organization  := "org.flowforwarding",
      version       := "0.5",
      scalaVersion  := SCALA_VERSION,
      scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
      git.remoteRepo := "git@github.com:Vitaliy-Savkin/warp.git",
      resolvers     ++= Dependencies.resolutionRepos,
      libraryDependencies ++= loggingLibs
    )

  lazy val driver_api =  Project("driver-api", file("./driver-api"), settings = warpCommonSettings("driver-api"))
    .settings(
      libraryDependencies ++= compile(spire, config) ++ test(scalatest)
    )


  lazy val driver_api_ofp13 = Project("driver-api-ofp13", file("./driver-api-ofp13"), settings = warpCommonSettings("driver-api-ofp13"))
    .dependsOn(driver_api)
    .settings(
      libraryDependencies ++= compile(spire, config) ++ test(scalatest)
    )


  lazy val driver_api_ofp13_adapter = Project("driver-api-ofp13-adapter", file("./driver-api-ofp13-adapter"), settings = warpCommonSettings("driver-api-ofp13-adapter"))
    .dependsOn(controller, driver_api, driver_api_ofp13)
    .settings(
      libraryDependencies ++= compile(spire, config) ++ test(scalatest)
    )


  lazy val of_driver_assemblySettings = Seq(mainClass := Some("org.flowforwarding.warp.jcontroller.JController"))

  lazy val of_driver = Project("of_driver", file("./of_driver"), settings = warpCommonSettings("of_driver") ++ of_driver_assemblySettings)
    .dependsOn(driver_api)
    .settings(
      libraryDependencies ++= compile(akka, netty, jackson_core_asl, jackson_mapper_asl)
    )

  lazy val controller = Project("controller", file("./controller"), settings = warpCommonSettings("controller"))
    .dependsOn(driver_api)
    .settings(
      libraryDependencies ++= compile(akka, akka_slf4j, spray_json, spray_http, spray_can, spray_routing, spire) ++ test(scalatest)
    )

//  lazy val jdriver = Project("jdriver", file("./jdriver"), settings = warpCommonSettings ++ assemblySettings)
//    .settings(
//      unmanagedBase <<= baseDirectory { base => base / "lib" }
//    ).dependsOn(controller)

  lazy val sdriver = Project("sdriver", file("./sdriver"), settings = warpCommonSettings("sdriver"))
    .settings(
      libraryDependencies ++= compile(scala_reflect, scala_compiler, spire)
    ).dependsOn(util, core, driver_api)


  lazy val sdriver_ofp13 = Project("sdriver-ofp13", file("./sdriver-ofp13"), settings = warpCommonSettings("sdriver-ofp13"))
    .settings(
      libraryDependencies ++= compile(scala_reflect, scala_compiler)
    ).dependsOn(util, core, sdriver, driver_api_ofp13)


  lazy val sdriver_ofp13_adapter = Project("sdriver-ofp13-adapter", file("./sdriver-ofp13-adapter"), settings = warpCommonSettings("sdriver-ofp13-adapter"))
    .settings(
      libraryDependencies ++= compile(scala_reflect, scala_compiler)
    ).dependsOn(controller, sdriver, sdriver_ofp13)

  lazy val demo = Project("demo", file("./demo"), settings = warpCommonSettings("demo"))
    .settings(
      libraryDependencies ++= compile(spray_client, spray_json, spray_httpx)
    )
    .dependsOn(controller, driver_api, driver_api_ofp13, driver_api_ofp13_adapter, sdriver, sdriver_ofp13, sdriver_ofp13_adapter)

//  lazy val idriver = Project("idriver", file("./idriver"), settings = warpCommonSettings ++ assemblySettings)
//  .settings(
//      libraryDependencies ++= compile(scala_pc)
//    )

  //////////////////////////////////////////////////////////////////////////////
  // SCALAVRO PROJECT INFO
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // SUB-PROJECTS
  //////////////////////////////////////////////////////////////////////////////

  def scalavroSubProject(name: String) = "%s-%s".format("scalavro", name)

  lazy val core = Project(
    id = scalavroSubProject("core"),
    base = file("./scalavro/core"),
    settings = commonSettings
  ) settings (
    // use patched version for now, the actual one hides it and breaks jdriver functionality
    unmanagedBase <<= baseDirectory { base => base / ".." / "jdriver" / "lib" },
    libraryDependencies ++= Seq(spray_json, scala_xml, avro)
    //libraryDependencies ++= Seq(spray_json, jackson_core_asl, jackson_mapper_asl, avro)
  ) dependsOn util

  lazy val util = Project(
    id = scalavroSubProject("util"),
    base = file("./scalavro/util"),
    settings = commonSettings
  ) settings(
    libraryDependencies ++= Seq(reflections, config)
  )

  //////////////////////////////////////////////////////////////////////////////
  // SHARED SETTINGS
  //////////////////////////////////////////////////////////////////////////////

  lazy val commonSettings = Project.defaultSettings ++
    basicSettings ++
    publishSettings

  lazy val basicSettings = Seq(
    version := SCALAVRO_VERSION,
    organization := "com.gensler",
    scalaVersion := SCALA_VERSION,
    resolvers ++= Dependencies.resolutionRepos,
    libraryDependencies ++= loggingLibs ++ Seq(scala_reflect, scalatest % "test"),
    scalacOptions in Compile ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    parallelExecution in Test := false,
    fork in Test := true
  )

  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    licenses := Seq(
      "BSD-style" -> url("http://opensource.org/licenses/BSD-2-Clause")
    ),
    homepage := Some(url("http://genslerappspod.github.io/scalavro/")),
    pomExtra :=
      <scm>
        <url>git@github.com:GenslerAppsPod/scalavro.git</url>
        <connection>scm:git:git@github.com:GenslerAppsPod/scalavro.git</connection>
      </scm>
        <developers>
          <developer>
            <id>ConnorDoyle</id>
            <name>Connor Doyle</name>
            <url>http://gensler.com</url>
          </developer>
        </developers>,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    }
  )
}
