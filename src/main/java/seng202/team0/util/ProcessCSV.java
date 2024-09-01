package seng202.team0.util;

import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.App;

/**
 * Utility class for interfacing with CSV files
 */
public class ProcessCSV {

  public static boolean processFile(File file) { //TODO bind attributes to wine class
    try (CSVReader fileReader = new CSVReader(new FileReader(file))) {
      String[] nextLine;
      String[] columnNames;
      ArrayList<String[]> data = new ArrayList<>();

      if ((nextLine = fileReader.readNext()) != null) {
        columnNames = nextLine;
      }
      else {
        return false;
      }

      System.out.println("Column Names: "+Arrays.toString(columnNames));

      while ((nextLine = fileReader.readNext()) != null) {
        System.out.println(Arrays.toString(nextLine));
        data.add(nextLine);
      }
    } catch (IOException | CsvValidationException e) {
      LogManager.getLogger(ProcessCSV.class).error("Failed to open csv file: " + file.getAbsolutePath(), e);
    }

    return true;

  }

  /**
   * Gets the column names of a csv file
   * <p>
   *   Strips empty column names
   * </p>
   * @param file file to read
   * @return names of columns if success
   */
  public static String[] getColumnNames(File file) throws IOException, CsvValidationException{
    try (CSVReader fileReader = new CSVReader(new FileReader(file))) {
      String[] columnNames = fileReader.readNext();
      return Arrays.stream(columnNames).filter(string -> !string.isEmpty()).toArray(String[]::new);

    } catch (IOException | CsvValidationException e) {
      LogManager.getLogger(ProcessCSV.class).error("Failed to open csv file: " + file.getAbsolutePath(), e);
      throw e;
    }
  }
  public static void storeProcessed(String[] columns, ArrayList<String[]> data) {
    System.out.println("Inserting");

  }
}
