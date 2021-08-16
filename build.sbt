enablePlugins(GatlingPlugin)

scalaVersion := "2.13.6"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

val gatlingVersion = "3.6.0"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test,it"
libraryDependencies += "io.gatling" % "gatling-test-framework" % gatlingVersion % "test,it"
libraryDependencies += "junit" % "junit" % "4.8.1" % "test"
libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % "9.4.0.jre8"

libraryDependencies += "io.gatling"            % "gatling-test-framework"    % gatlingVersion % "test,it"
libraryDependencies ++= Seq(
  "junit" % "junit" % "4.13.2" % "test"
)