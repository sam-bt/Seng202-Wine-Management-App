![WINO](./src/main/resources/logo-red-bg.png)

# WINO: Your All-In-One Wine Experience

WINO is an intuitive kiosk application designed for wine enthusiasts, casual drinkers, and tourists alike. With a user-friendly interface, WINO provides a comprehensive platform for all things wine, making it easy to explore and engage with the world of viticulture.

This Application was produced for the Seng202 Course at the University of Canterbury

## Features
- **Wine & Vineyard Information**: Access detailed information about various wines and vineyards, including tasting notes, production methods, and more.
- **Geographical Mapping**: Visualize the locations of wineries and vineyards on an interactive map, making it simple to discover new favorites.
- **Tour Planning**: Easily plan your vineyard tours by selecting from a curated list of wineries, ensuring a memorable experience.
- **User Engagement**: Share your thoughts by leaving wine reviews and connect with fellow wine lovers through user profiles.

WINO is designed for quick and hassle-free installation on kiosks, with straightforward setup procedures for administrators.

# Admin Credentials
The application ships with a default admin account. The username is 'admin' and the password is 'admin'. Upon first time log in, you will be prompted to change this password.

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

# Cloning
1. Open a terminal window
2. Use the `git clone` command to clone the repository
```
git clone https://eng-git.canterbury.ac.nz/seng202-2024/team-6.git
```
This will create a new directory named `team-6` containing the project files.


## Importing Project (Using IntelliJ)
IntelliJ has built-in support for Gradle. To import the repository:

- Launch IntelliJ and choose `Open` from the start-up window.
- Select the `team-6` directory and click open
- At this point in the bottom right notifications you may be prompted to 'load gradle scripts', If so, click load


## Run Project
1. Open a terminal inside the project directory.
2. Run the following command:
```
./gradlew run
```
This will build the project and launch the application in a new window.


## Build and Run Jar
1. Open a command line interface inside the project directory.
2. Run the following command
```
./gradlew jar
```
This will create a JAR file in the `build/libs/` directory named `wine-2.0-snapshot.jar`
3. Navigate to the `build/libs/` directory inside the project.
4. Run the following command to open the application.
```
java -jar wine-2.0-snapshot.jar
```
This will launch the application.


## Run Tests
1. Open a command line interface inside the project directory.
2. Run the following command(s).
   - To run unit tests:
   ```
   ./gradlew test
   ```
   - To run cucumber tests:
   ```
   ./gradlew cucumber
   ```

**Note:** *If you run into dependency issues when running the app or the Gradle pop up doesn't
appear then open the Gradle sidebar and click the Refresh icon.*

# Attributions

- [Wine Icons Designed by Freepik](https://www.freepik.com/free-vector/realistic-wine-collection-bottles-glasses-with-white-red-rose-beverages-isolated_10347028.htm)
- New Zealand Geolocations composed by AI
