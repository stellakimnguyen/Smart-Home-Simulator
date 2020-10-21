name := """SOEN343-F2020"""
organization := "Mohamed Amine Kihal (40046046), Pierre-Alexis Barras (40022016), Rodrigo Zanini (40077727), Stella Nguyen (40065803)"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.3"

libraryDependencies += guice
Compile / doc / scalacOptions := Seq("-groups", "-implicits", "-author")
