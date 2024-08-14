package seng202.team0;

import java.util.function.Consumer;

public class WinoManager {

    /**
     * Launcher for the initial GUI screen.
     */
    private final Consumer<WinoManager> homeScreenLauncher;
    private final Consumer<WinoManager> wineScreenLauncher;
    private final Consumer<WinoManager> listsScreenLauncher;
    private final Consumer<WinoManager> vineyardsScreenLauncher;
    private final Consumer<WinoManager> dataSetsScreenLauncher;
    private final Consumer<WinoManager> consumptionCalculatorScreenLauncher;

    /**
     * Manages the Winos state by creating a GameManager.
     *
     * @param homeScreenLauncher is the launcher for the initial Gui Screen
     */
    public WinoManager(final Consumer<WinoManager> homeScreenLauncher,
        final Consumer<WinoManager> wineScreenLauncher,
        final Consumer<WinoManager> listsScreenLauncher,
        final Consumer<WinoManager> vineyardsScreenLauncher,
        final Consumer<WinoManager> dataSetsScreenLauncher,
        final Consumer<WinoManager> consumptionCalculatorScreenLauncher) {
        this.homeScreenLauncher = homeScreenLauncher;
        this.wineScreenLauncher = wineScreenLauncher;
        this.listsScreenLauncher = listsScreenLauncher;
        this.vineyardsScreenLauncher = vineyardsScreenLauncher;
        this.dataSetsScreenLauncher = dataSetsScreenLauncher;
        this.consumptionCalculatorScreenLauncher = consumptionCalculatorScreenLauncher;
        launchHomeScreen();
    }

    /**
     * Launches the home screen.
     */
    public void launchHomeScreen() {
        homeScreenLauncher.accept(this);
    }
    /**
     * Launches the wine screen.
     */
    public void launchWineScreen() {
        wineScreenLauncher.accept(this);
    }
    /**
     * Launches the list screen.
     */
    public void launchListsScreen() {
        listsScreenLauncher.accept(this);
    }
    /**
     * Launches the vineyard screen.
     */
    public void launchVineyardsScreen() {
        vineyardsScreenLauncher.accept(this);
    }
    /**
     * Launches the data set screen.
     */
    public void launchDataSetsScreen() {
        dataSetsScreenLauncher.accept(this);
    }
    /**
     * Launches the consumption calculator screen.
     */
    public void launchConsumptionCalculatorScreen() {
        consumptionCalculatorScreenLauncher.accept(this);
    }
}
