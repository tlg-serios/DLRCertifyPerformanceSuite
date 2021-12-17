
DLR Performance tests
=========================

Contains tests for creating Blackware stock, testing Saga and DPC APIs and some frontend journeys

Prerequisites
---------------

Java installed (Built against 1.8.0)

sbt installed (Built against 1.5.4)

IntelliJ installed with Scala and SBT plugins downloaded

Getting started
---------------
Compile project in root folder
```bash
sbt clean compile rebuild
```
Start sbt in root folder of project
```bash
sbt
```
To run the tests run the below in the sbt console
```bash
Gatling / test
```

Run all simulations
-------------------

```bash
> Gatling / test
```

Run a single simulation
-----------------------

```bash
> Gatling / testOnly computerdatabase.BasicSimulation
```

Reporting
--------------------

At the end of a run the test will specify an output file location, ending with a full numerical timestamp

This report can be found in a correspondingly named folder in target/gatling

Within the folder select index.html, open it in IntelliJ and click the Chrome (or preferred browser) icon in the top right corner of the html file

The report itself is very user friendly with graphs and tables displaying total run times and averages, with standard deviations
