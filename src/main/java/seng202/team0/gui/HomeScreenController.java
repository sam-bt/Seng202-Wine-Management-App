package seng202.team0.gui;


import javafx.fxml.FXML;
import seng202.team0.WinoManager;

public class HomeScreenController {

    /**
     * The WINO manager instance.
     */
    private final WinoManager winoManager;

    /**
     * Constructs the HomeScreenController.
     * @param manager The WINO manager instance.
     */
    HomeScreenController(WinoManager manager) {
        winoManager = manager;


    }
@FXML
    private void openHomeScreen(){
        System.out.println("openinghomescnrees");
    }

}
