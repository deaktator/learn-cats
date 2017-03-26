name := "learn-cats"

homepage := Some(url("https://github.com/deaktator/learn-cats"))

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

description := """library for easily turning expressions into functions"""

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
    <url>https://scifn.github.io</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:deaktator/learn-cats</url>
      <connection>scm:git:git@github.com:deaktatr/learn-cats.git</connection>
    </scm>
    <developers>
      <developer>
        <id>deaktator</id>
        <name>R M Deak</name>
        <url>https://deaktator.github.io</url>
      </developer>
    </developers>
)

lazy val commonSettings = Seq(
  organization := "com.github.deaktator",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.11.8", "2.10.5"),
  crossPaths := true,
  incOptions := incOptions.value.withNameHashing(true),
  javacOptions ++= Seq("-Xlint:unchecked"),
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
//    "-Yinline",
//    "-Yinline-warnings",
    "-Yclosure-elim",
    "-Ydead-code",
    "-Xverify",
    "-Ywarn-inaccessible",
    "-Ywarn-dead-code"
  ),

  scalacOptions <++= scalaVersion map {
    case v: String if v.split("\\.")(1).toInt >= 11 =>
      Seq(
        "-Ywarn-unused",
        "-Ywarn-unused-import",

        // These options don't play nice with IntelliJ.  Comment them out to debug.
        "-Ybackend:GenBCode",
        "-Ydelambdafy:method",
        "-Yopt:l:project",
        "-Yconst-opt"
      )
    case _ =>
      Seq()
  }
)

// ====================   Disable packaging root project   ====================
//  Paul P: http://stackoverflow.com/a/25653777
Keys.`package` :=  file("")

packageBin in Global :=  file("")

packagedArtifacts :=  Map()
// ====================   Disable packaging root project   ====================

lazy val root = project.in( file(".") ).
  // To run benchmarks with tests, add 'bench' to the aggregate list
  settings(commonSettings: _*).
  settings (
    name := "learn-cats",

    parallelExecution in Test := false,

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats" % "0.9.0",
      "io.monix" %% "monix-eval" % "2.2.2",
      "io.monix" %% "monix-cats" % "2.2.2",
      "org.scalatest" %% "scalatest" % "2.2.5" % "test",
      "org.slf4j" % "slf4j-log4j12" % "1.7.10" % "test"
    )
  )
