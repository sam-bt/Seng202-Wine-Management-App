package seng202.team6.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for interfacing with CSV files
 */
public class ProcessCSV {
  private static Logger log = LogManager.getLogger(ProcessCSV.class);

  /**
   * Gets a CSV file as a list of rows
   *
   * @param file file to read
   * @return rows if successful
   */
  public static List<String[]> getCSVRows(File file) {
    List<String[]> rows = new ArrayList<>();
    try (FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, UTF_8)
    ) {
      readCSVRows(inputStreamReader, rows);
    } catch (IOException | CsvValidationException error) {
      log.error("Failed to read rows from CSV file: {}", file.getAbsoluteFile(), error);
    }
    return rows;
  }

  /**
   * Gets a CSV input stream as a list of rows
   *
   * @param input input stream to read
   * @return rows if successful
   */
  public static List<String[]> getCSVRows(InputStream input) {
    List<String[]> rows = new ArrayList<>();
    try (InputStreamReader inputStreamReader = new InputStreamReader(input, UTF_8)) {
      readCSVRows(inputStreamReader, rows);
    } catch (IOException | CsvValidationException error) {
      log.error("Failed to read rows from resource", error);
    }
    return rows;
  }

  private static void readCSVRows(InputStreamReader inputStreamReader, List<String[]> rows) throws IOException, CsvValidationException {
    CSVReader csvReader = new CSVReader(inputStreamReader);
    String[] nextLine;
    while ((nextLine = csvReader.readNext()) != null) {
      rows.add(nextLine);
    }
  }
}
