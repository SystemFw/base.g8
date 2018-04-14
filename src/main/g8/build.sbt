lazy val root = (project in file(".")).
  settings(
    commonSettings,
    consoleSettings,
    compilerOptions,
    typeSystemEnhancements,
    dependencies,
    tests
  )

lazy val commonSettings = Seq(
  name := "$name;format="lower,word"$",
  scalaVersion := "2.11.12",
  crossScalaVersions := Seq("2.11.12", "2.12.5")
)

val consoleSettings = Seq(
  initialCommands := s"import $defaultImportPath$",
  scalacOptions in (Compile, console) -= "-Ywarn-unused-import"
)

lazy val compilerOptions =
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-encoding",
    "utf8",
    "-target:jvm-1.8",
    "-feature",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-Ypartial-unification",
    "-Ywarn-unused-import",
    "-Ywarn-value-discard"
  )

lazy val typeSystemEnhancements =
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

def dep(org: String)(version: String)(modules: String*) =
    Seq(modules:_*) map { name =>
      org %% name % version
    }

lazy val dependencies = {
  // brings in cats and cats-effect
  val fs2 = dep("co.fs2")("$fs2Version$")(
    "fs2-core",
    "fs2-io"
  )

  val mixed = Seq(
  )

  def extraResolvers =
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    )

  val deps =
    libraryDependencies ++= Seq(
      fs2,
      mixed
    ).flatten

  Seq(deps, extraResolvers)
}

lazy val tests = {
  val dependencies = {
    val specs2 = dep("org.specs2")("$specs2Version$")(
      "specs2-core",
      "specs2-scalacheck"
    )

    val mixed = Seq(
      "org.scalacheck" %% "scalacheck" % "$scalacheckVersion$"
    )

    libraryDependencies ++= Seq(
      specs2,
      mixed
    ).flatten.map(_ % "test")
  }

  val frameworks =
    testFrameworks := Seq(TestFrameworks.Specs2)

  Seq(dependencies, frameworks)
}
