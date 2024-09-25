package seng202.team6.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
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
   * <p>
   *   This function will attempt to detect the file encoding then try and read the bytes as
   *   the detected encoding. If the encoding detection fails, the file will be read as UTF-8.
   * </p>
   *
   * @param file the CSV file to read
   * @return a list of rows from the CSV input stream, or an empty list if the reading fails
   */
  public static List<String[]> getCSVRows(File file) {
    List<String[]> rows = new ArrayList<>();
    byte[] fileBytes;

    // read the all the bytes from the file. this can be used to detect encoding and read the csv
    // file without having to read the file twice
    try {
      fileBytes = Files.readAllBytes(file.toPath());

      String detectedCharset = detectFileEncoding(fileBytes);
    } catch (IOException e) {
      log.error("Failed to read the file: {}", file.getAbsolutePath(), e);
      return rows;
    }

    // detect encoding using ICU4J then try read the CSV rows using it
    String detectedCharset = detectFileEncoding(fileBytes);
    try (InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(fileBytes), detectedCharset)) {
      readCSVRows(inputStreamReader, rows);
    } catch (IOException | CsvValidationException error) {
      log.error("Failed to read rows from CSV file: {}", file.getAbsolutePath(), error);
    }
    return rows;
  }

  /**
   * Gets a CSV input stream as a list of rows
   *
   * @param input the input stream to read
   * @return a list of rows from the CSV input stream, or an empty list if the reading fails
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

  /**
   * Reads the CSV rows from an input stream reader and adds them to the provided list.
   *
   * @param inputStreamReader the input stream reader to read from
   * @param rows the list to add the read rows to
   * @throws IOException if an I/O error occurs while reading
   * @throws CsvValidationException if a CSV validation error occurs
   */
  private static void readCSVRows(InputStreamReader inputStreamReader, List<String[]> rows) throws IOException, CsvValidationException {
    CSVReader csvReader = new CSVReader(inputStreamReader);
    String[] nextLine;
    while ((nextLine = csvReader.readNext()) != null) {
      rows.add(nextLine);
    }
  }

  /**
   * Detects the file encoding of the provided byte array using ICU4J's character detector
   *
   * @param fileBytes the byte array of the file
   * @return the name of the detected charset, or UTF-8 is detection fails
   */
  private static String detectFileEncoding(byte[] fileBytes) {
    CharsetDetector charsetDetector = new CharsetDetector();
    charsetDetector.setText(fileBytes);
    CharsetMatch match = charsetDetector.detect();
    return match != null ? match.getName() : UTF_8.name();
  }
}
