# SENG202 Project: WINO

TODO - add attribution
https://www.freepik.com/free-vector/realistic-wine-collection-bottles-glasses-with-white-red-rose-beverages-isolated_10347028.htm****

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

### Geolocation API Key Setup
To use the Map functionality in this application, you need to obtain an API key from the OpenRouteService (ORS).

1. Sign up for OpenRouteService
    * Go to https://openrouteservice.org/dev/#/login
    * Create a new account or log in if you already have one
    * One logged in, navigate to the dashboard
2. Generate an API key
    * In the dashboard, go to the **"Request a Token"** section
    * Select **"Standard"** as the token type
    * Choose a name for the token
    * Copy the generated key
3. Create the environment file
    * **Option 1**: If you are building the application, create a `.env` file in the `/src/main/resources/` directory
    * **Option 2**: If you are running the application via a JAR file, create a `.env` file in the same directory as the JAR
4. Add the API key to the environment file
    * Open the `.env` file and add in the following line, replacing `YOUR_API_KEY` with the API key you generated:
        ```
       ORS_API_KEY=YOUR_API_KEY
        ```

#### Important Note
Make sure to keep your API key confidential. If you are using version control, add `.env` to your `.gitignore` file to prevent it from being tracked.

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
