README

Prerequisites: Full list to be found here https://gatling.io/docs/gatling/tutorials/installation/, the main ones for our purposes are

Java installed (Built against 1.8.0)
sbt installed (Built against 1.5.4)
IntelliJ installed with Scala and SBT plugins downloaded

Getting started:

Run sbt clean compile rebuild in project root folder

Run sbt in the command line of the top directory of the project to start the sbt service

To run the tests run Gatling / test in the sbt console

Environment config:

The file Constants.scala contains a list of constant values used in the tests

These should be self explanatory, and should be set to environment and manufacturer configs for a given environment

Reporting:

At the end of a run the test will specify an output file location, ending with a full numerical timestamp

This report can be found in a correspondingly named folder in target/gatling

Within the folder select index.html, open it in IntelliJ and click the Chrome (or preferred browser) icon in the top right corner of the html file

The report itself is very user friendly with graphs and tables displaying total run times and averages, with standard deviations
