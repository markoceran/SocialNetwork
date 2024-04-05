ThisBuild / scalaVersion := "2.13.13"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """SocialNetwork""",
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      evolutions,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "org.playframework.anorm" %% "anorm" % "2.7.0",
      "mysql" % "mysql-connector-java" % "8.0.28",
      "org.mindrot" % "jbcrypt" % "0.4",
      "com.typesafe.play" %% "play-filters-helpers" % "2.8.0"
    )

  )