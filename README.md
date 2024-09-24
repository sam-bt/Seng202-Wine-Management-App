# SENG202 Project: WINO

Wino is an application to help wine drinkers keep track of their wine.

## Authors

- SENG202 Teaching team
- Samuel Beattie
- Mitchell Perrin
- Corey Hines
- Max Russell
- Jake Miller
- Angus McDougall


## Prerequisites

- JDK >=
  21 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Gradle [Download](https://gradle.org/releases/) and [Install](https://gradle.org/install/)

## Building
- WINO can be run by running `./gradlew build`
- Tests can be run by `./gradlew check`
- The JAR can be built by `./gradlew jar`
  - The built application can be found in build/libs

## Importing Project (Using IntelliJ)

IntelliJ has built-in support for Gradle. To import WINO:

- Launch IntelliJ and choose `Open` from the start up window.
- Select the project and click open
- At this point in the bottom right notifications you may be prompted to 'load gradle scripts', If
  so, click load

**Note:** *If you run into dependency issues when running the app or the Gradle pop up doesn't
appear then open the Gradle sidebar and click the Refresh icon.*
