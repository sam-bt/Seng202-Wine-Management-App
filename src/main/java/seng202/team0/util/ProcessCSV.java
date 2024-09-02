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
   * Gets a row from a csv file
   * @param file file to read
   * @return given row if successful
   */
  public static String[] getCSVRow(File file, int row) throws IOException, CsvValidationException{
    try (CSVReader fileReader = new CSVReader(new FileReader(file))) {
      for(int i=0; i < row; i++){
        fileReader.readNext();
      }
      return fileReader.readNext();

    } catch (IOException | CsvValidationException e) {
      LogManager.getLogger(ProcessCSV.class).error("Failed to open csv file: " + file.getAbsolutePath(), e);
      throw e;
    }
  }
  public static void storeProcessed(String[] columns, ArrayList<String[]> data) {
    System.out.println("Inserting");

  }

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
        System.out.println(Arrays.toString(nextLine));
        rows.add(nextLine);
      }

    } catch (IOException | CsvValidationException e) {
      LogManager.getLogger(ProcessCSV.class).error("Failed to open csv file: " + file.getAbsolutePath(), e);
      throw e;
    }
    return rows;
  }


}
