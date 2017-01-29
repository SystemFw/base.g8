lazy val root = (project in file(".")).
  settings(
    commonSettings,
    compilerOptions,
    typeSystemEnhancements,
    dependencies
  )

lazy val commonSettings = Seq(
  organization := "$organization$",
  name := "$name$",
  scalaVersion := "2.11.8"
)


lazy val compilerOptions = {
  val options = Seq(
    "-unchecked",
    "-deprecation",
    "-target:jvm-1.8",
    "-feature",
    "-language:implicitConversions",
    "-language:higherKinds"
  )

  scalacOptions ++= options
}


lazy val typeSystemEnhancements = Seq(
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3"),
  addCompilerPlugin("com.milessabin" % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full)
)


lazy val dependencies = {
  val scalazVersion = "$scalazVersion$"
  val deps = Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion, // obviously
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion
  )

  libraryDependencies ++= deps
}


