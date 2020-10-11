import java.io.*;
import java.sql.*;

public class Insertion {

    Connection pos_conn;
    Statement pos_stmt;
    int test_no;

    // Class that handles the insertion configurations
    public Insertion (Connection pos_conn, Statement pos_stmt, int test_no) {
      if (test_no < 0 || test_no > 2) {
        System.out.println("Wrong test number passed - no insertion made");
      } else {
        this.pos_conn = pos_conn;
        this.pos_stmt = pos_stmt;
        this.test_no = test_no;
      }
    }


  // Redirects to the correct configuration based on the test running
  public String insertTuples() {

    // Configuration 1: one tuple
    if (test_no == 0)   return insertOneTuple();

    // Configuration 2: multiple tuples at a time
    if (test_no == 1)   return insertMultipleTuples();

    // Configuration 3: insertion while queries are executed
    if (test_no == 2)   return insertWithMixedWorkload();

    return "Impossible";
  }


  public String insertOneTuple() {

    



    return "\"One\" tuple at a time is inserted";
  }

  public String insertMultipleTuples() {
    return "\"Multiple\" tuples are inserted";
  }

  public String insertWithMixedWorkload() {
    return "A \"mixed\" workload is done";
  }

}
