import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;
import java.text.*;

public class Insertion {

  Connection pos_conn;
  Statement pos_stmt;
  int test_no;

  Logger test_logger;

  static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  // Defines how many tuples are inserted when using "multiple tuples" configuration
  int no_multiple_tuples = 5;

  // Data structure: timestamp and temperature
  String data_file_path;

  // Class that handles the insertion configurations
  public Insertion (Connection pos_conn, Statement pos_stmt, int test_no, Logger test_logger, String data_file_path) {
    if (test_no < 0 || test_no > 2) {
      System.out.println("Wrong test number passed - no insertion made");
    } else {
      this.pos_conn = pos_conn;
      this.pos_stmt = pos_stmt;
      this.test_no = test_no;
      this.test_logger = test_logger;
      this.data_file_path = data_file_path;
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
    String row;
    int rows_inserted = 0;

    // Timestamp variables
    java.util.Date parsedDate;
    Timestamp timestamp;

    try {

      // Preparing file scanner
      Scanner reader = new Scanner(new File(data_file_path));

      // Defining variables for the insertion
      String insertion_query = "INSERT INTO test_table (time, value) VALUES (?, ?)";
      PreparedStatement pst = pos_conn.prepareStatement(insertion_query);

      // Signaling start of test
      test_logger.info("--Start of test--");
      while (reader.hasNextLine()) {

        // Retrieving the data and preparing insertion script
        row = reader.nextLine();
        fields = row.split(",");

        // Casting timestamp
        parsedDate = dateFormat.parse((String)fields[0]);
        timestamp = new Timestamp(parsedDate.getTime());

        // Inserting the variables in the prepared statement
        pst.setTimestamp(1, timestamp);
        pst.setInt(2, Integer.parseInt(fields[1]));

        // Executing the query and checking the result
        if (pst.executeUpdate() != 1) {
            test_logger.severe("Problem executing the query\n");
        } else {
            rows_inserted++;
            test_logger.info("Query successfully executed: ("+fields[0]+","+fields[1]+")\n");
        }
      }

      // Closing the file reader
      reader.close();

    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
      test_logger.severe("Insertion: \""+configuration+"\" - problems with the execution");
    } catch (SQLException e) {
      test_logger.severe("Problem executing the script\n");
      e.printStackTrace();
    } catch (ParseException e) {
      test_logger.severe("Problem with parsing a timestamp\n");
      e.printStackTrace();
    }


    test_logger.info("Total rows inserted: "+rows_inserted);
  }


  // Applying the "multiple tuples at a time" configuration
  public void insertMultipleTuples() {

    // Defining variables useful for method
    String[] fields;
    String row;
    int rows_inserted = 0;

    // Defining variables for the insertion
    String original_insert_query = "INSERT INTO test_table (time, value) VALUES ";
    String insertion_query = original_insert_query;
    String insertions="";

    // Number of tuples inserted in the script but not yet executed
    int no_rows_waiting = 0;

    // Timestamp variables
    java.util.Date parsedDate;
    Timestamp timestamp;

    try {
      Scanner reader = new Scanner(new File(data_file_path));

      // Preparing the insertion query
      for (int i=0; i<no_multiple_tuples; i++) {
        if (i!=0) {
          insertion_query+=", ";
        }
        insertion_query+="(?, ?)";
      }

      PreparedStatement pst = pos_conn.prepareStatement(insertion_query);

      // Signaling start of test
      test_logger.info("--Start of test--");
      while (reader.hasNextLine()) {

        // Retrieving the data and preparing insertion script
        row = reader.nextLine();
        fields = row.split(",");
        insertions += "('" + fields[0] + "'," + fields[1] + ") ";

        // Casting timestamp
        parsedDate = dateFormat.parse((String)fields[0]);
        timestamp = new Timestamp(parsedDate.getTime());

        // Inserting the variables in the prepared statement
        int first_column = (no_rows_waiting*2)+1;
        int second_column = (no_rows_waiting+1)*2;
        pst.setTimestamp(first_column, timestamp);
        pst.setInt(second_column, Integer.parseInt(fields[1]));
        no_rows_waiting++;

        // Executing the query and checking the result, if number of rows is enough
        if (no_rows_waiting == no_multiple_tuples) {
          if (pst.executeUpdate() == 5) {
            rows_inserted+=no_rows_waiting;
            test_logger.info("Query successfully executed: \n"+insertions);
            insertions = "";
          } else {
            no_rows_waiting = 0;
            test_logger.severe("Problem executing the following insertion"+insertions+"\n");
            insertions = "";
          }

          // Reset variables for successive tuples
          no_rows_waiting = 0;
        }
      }

      // Last insertion, if no_multiple_tuples was not reached
      if (no_rows_waiting != 0) {
        original_insert_query+= " "+insertions.replace(") (", "),(");
        if (pos_stmt.executeUpdate(original_insert_query) == no_rows_waiting) {
          rows_inserted+=no_rows_waiting;
          test_logger.info("Query successfully executed: \n"+insertions);
        } else {
          test_logger.severe("Problem executing the following insertion"+insertions+"\n");
        }
      }

      // Closing the file reader
      reader.close();

    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
      test_logger.severe("Insertion: \"Multiple tuples at a time\" - problems with the execution");
    } catch (SQLException e) {
      test_logger.severe("Problem executing the following script\n");
      e.printStackTrace();
    } catch (ParseException e) {
      test_logger.severe("Problem with parsing a timestamp\n");
      e.printStackTrace();
    }

    test_logger.info("Total rows inserted: "+rows_inserted);
  }
}
