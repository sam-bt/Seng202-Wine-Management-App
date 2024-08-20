package seng202.team0.util;

import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ProcessCSV {

  public static boolean processFile(File file) {
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
      e.printStackTrace();
    }

    return true;

  }

  public static void storeProcessed(String[] columns, ArrayList<String[]> data) {
    System.out.println("Inserting");

  }
}
