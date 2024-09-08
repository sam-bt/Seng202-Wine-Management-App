package seng202.team0.util;

import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;

/**
 * Utility class for interfacing with CSV files
 */
public class ProcessCSV {

  /**
   * Gets a CSV file as a list of rows
   * @param file file to read
   * @return rows if successful
   */
  public static ArrayList<String[]> getCSVRows(File file) throws IOException, CsvValidationException{
    ArrayList<String[]> rows = new ArrayList<>();
    String[] nextLine;
    try (CSVReader fileReader = new CSVReader(new FileReader(file))) {

      while ((nextLine = fileReader.readNext()) != null) {
        rows.add(nextLine);
      }

    } catch (IOException | CsvValidationException e) {
      LogManager.getLogger(ProcessCSV.class).error("Failed to open csv file: " + file.getAbsolutePath(), e);
      throw e;
    }
    return rows;
  }


}
