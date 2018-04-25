name := """timesheet"""
organization := "com.timesheet"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  ws,
  specs2 % Test,
  "mysql" % "mysql-connector-java" % "5.1.37",
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0"
)


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.timesheet.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.timesheet.binders._"
