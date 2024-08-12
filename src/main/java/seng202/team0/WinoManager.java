package seng202.team0;

import java.util.function.Consumer;

public class WinoManager {

    /**
     * Launcher for the initial GUI screen.
     */
    private final Consumer<WinoManager> initialGuiLauncher;

    /**
     * Manages the Winos state by creating a GameManager.
     * @param initialGuiLauncher is the launcher for the initial Gui Screen
     */
    public WinoManager(Consumer<WinoManager> initialGuiLauncher) {
        this.initialGuiLauncher = initialGuiLauncher;
        launchInitialGuiScreen();
    }

    /**
     * Launches the initial gui screen.
     */
    public void launchInitialGuiScreen(){
        initialGuiLauncher.accept(this);
    }
}
