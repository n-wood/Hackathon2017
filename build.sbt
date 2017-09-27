name := "Hackathon2017"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.190"
