package seng202.team6;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.gui.wrapper.FXWindow;

/**
 * Default entry point class
 *
 * @author seng202 teaching team
 */
public class App {

  private static final Logger log = LogManager.getLogger(App.class);

  /**
   * Entry point which runs the javaFX application Also shows off some different logging levels
   *
   * @param args program arguments from command line
   */
  public static void main(String[] args) {
    FXWindow.launchWrapper(args);
  }
}
