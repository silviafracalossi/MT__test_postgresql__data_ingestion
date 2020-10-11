import java.io.*;
import java.sql.*;

public class Index {

  Connection pos_conn;
  Statement pos_stmt;
  int test_no;

  // Class that handles the indexes configurations
  public Index (Connection pos_conn, Statement pos_stmt, int test_no) {
    if (test_no < 0 || test_no > 2) {
      System.out.println("Wrong test number passed - no index applied");
    } else {
      this.pos_conn = pos_conn;
      this.pos_stmt = pos_stmt;
      this.test_no = test_no;
    }
  }


  // Redirects to the correct configuration based on the test running
  public String applyIndex() {

    // Configuration 1: no index
    if (test_no == 0)   return noIndex();

    // Configuration 2: index on the timestamp field
    if (test_no == 1)   return timestampIndex();

    // Configuration 3: indexes on the timestamp and on the value fields
    if (test_no == 2)   return timestampAndValueIndexes();

    return "Impossible";
  }


  // Applying the "no index" configuration
  public String noIndex() {
    return "Index: \"No Index\" applied";
  }

  // Applying the "index on timestamp" configuration
  public String timestampIndex() {
    try {
      String timestamp_index_creation =
              "CREATE INDEX time_index " +
              "    ON test_table (time);";
      if (pos_stmt.executeUpdate(timestamp_index_creation) == 0) {
        return "Index: \"Index on Timestamp\" applied";
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Index: \"Index on Timestamp\" - problems with the execution";
  }

  // Applying the "index on timestamp and value" configuration
  public String timestampAndValueIndexes() {
    try {
      String timestamp_index_creation =
              "CREATE INDEX time_index " +
              "    ON test_table (time, value);";
      if (pos_stmt.executeUpdate(timestamp_index_creation) == 0) {
        return "Index: \"Index on Timestamp and Value\" applied";
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Index: \"Index on Timestamp and Value\" - problems with the execution";
  }

}
