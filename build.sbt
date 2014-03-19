name := "djc-lang"

version := "0.0.0"

scalaVersion := "2.10.3"

scalacOptions ++= List(
	"-deprecation",
	"-encoding", "UTF-8",
	"-unchecked",
	"-feature",
	"-target:jvm-1.6",
	"-language:implicitConversions",
	"-language:reflectiveCalls",
	"-Xlint"
)

resolvers ++= Seq()

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

