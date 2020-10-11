import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

public class Insertion {

    Connection pos_conn;
    Statement pos_stmt;
    int test_no;

    Logger test_logger;

    // Defines how many tuples are inserted when using "multiple tuples" configuration
    int no_multiple_tuples = 5;

    // Data structure: timestamp, printer ID, temperature
    String data_file_path = "data/ISPPE_FPGA_TEMPERATURE.csv";

    // Class that handles the insertion configurations
    public Insertion (Connection pos_conn, Statement pos_stmt, int test_no, Logger test_logger) {
      if (test_no < 0 || test_no > 2) {
        System.out.println("Wrong test number passed - no insertion made");
      } else {
        this.pos_conn = pos_conn;
        this.pos_stmt = pos_stmt;
        this.test_no = test_no;
        this.test_logger = test_logger;
      }
    }


  // Redirects to the correct configuration based on the test running
  public void insertTuples() {

    // Configuration 1: one tuple at a time
    if (test_no == 0)   insertOneTupleAndMixedWorkload("One tuple at a time");

    // Configuration 2: multiple tuples at a time
    if (test_no == 1)   insertMultipleTuples();

    // Configuration 3: insertion while queries are executed
    if (test_no == 2)   insertOneTupleAndMixedWorkload("Mixed Workload");
  }


  // Applying the "one tuple at a time" or the "mixed workload" configuration
  public void insertOneTupleAndMixedWorkload(String configuration) {

    // Defining variables useful for method
    String[] fields;
    String row, query = "";
    String insertion_sql = "INSERT INTO test_table (time, value) VALUES ('";
    int rows_inserted = 0;

    try {
      Scanner reader = new Scanner(new File(data_file_path));

      // Signaling start of test
      test_logger.info("--Start of test--");
      while (reader.hasNextLine()) {

        // Retrieving the data and preparing insertion script
        row = reader.nextLine();
        fields = row.split(",");
        query = insertion_sql + fields[0] + "', " + fields[2] + ");";

        // Executing the query and checking the result
        try {
          if (pos_stmt.executeUpdate(query) != 1) {
              test_logger.severe("Problem executing the following script: \n"+query);
          } else {
              rows_inserted++;
              test_logger.info("Query successfully executed: \n"+query);
          }
        } catch (SQLException e) {
          test_logger.severe("Problem executing the following script: \n"+query);
          e.printStackTrace();
        }
      }

      // Closing the file reader
      reader.close();

    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
      test_logger.severe("Insertion: \""+configuration+"\" - problems with the execution");
    }

    test_logger.info("Total rows inserted: "+rows_inserted);
  }


  // Applying the "multiple tuples at a time" configuration
  public void insertMultipleTuples() {

    // Defining variables useful for method
    String[] fields;
    String row, query = "", tuple;
    String insertion_sql = "INSERT INTO test_table (time, value) VALUES ";
    int rows_inserted = 0;

    // Number of tuples inserted in the script but not yet executed
    int no_rows_waiting = 0;

    try {
      Scanner reader = new Scanner(new File(data_file_path));

      // Signaling start of test
      test_logger.info("--Start of test--");
      while (reader.hasNextLine()) {

        // Retrieving the data and preparing insertion script
        row = reader.nextLine();
        fields = row.split(",");
        tuple = "('" + fields[0] + "', " + fields[2] + ")";

        // Inserting the tuple in the final query
        query = (no_rows_waiting == 0) ? (insertion_sql+tuple) : (query+", "+tuple);
        no_rows_waiting++;

        // Executing the query and checking the result, if number of rows is enough
        if (no_rows_waiting == no_multiple_tuples) {

          try {
            query += ';';
            if (pos_stmt.executeUpdate(query) != no_rows_waiting) {
                test_logger.severe("Problem executing the following script: \n"+query);
            } else {
                rows_inserted+=no_rows_waiting;
                test_logger.info("Query successfully executed: \n"+query);
            }
          } catch (SQLException e) {
            test_logger.severe("Problem executing the following script: \n"+query);
            e.printStackTrace();
          }

          // Reset variables for successive tuples
          no_rows_waiting = 0;
          query = "";
        }
      }

      // Last insertion, if no_multiple_tuples was not reached
      if (no_rows_waiting != 0) {
        try {
          query += ';';
          if (pos_stmt.executeUpdate(query) != no_rows_waiting) {
              test_logger.severe("Problem executing the following script: \n"+query);
          } else {
              rows_inserted+=no_rows_waiting;
              test_logger.info("Query successfully executed: \n"+query);
          }
        } catch (SQLException e) {
          test_logger.severe("Problem executing the following script: \n"+query);
          e.printStackTrace();
        }
      }

      // Closing the file reader
      reader.close();

    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
      test_logger.severe("Insertion: \"Multiple tuples at a time\" - problems with the execution");
    }

    test_logger.info("Total rows inserted: "+rows_inserted);
  }
}
