ThisBuild / scalaVersion := "2.13.13"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """SocialNetwork""",
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "org.playframework.anorm" %% "anorm" % "2.7.0",
      "mysql" % "mysql-connector-java" % "8.0.28"
    )

  )