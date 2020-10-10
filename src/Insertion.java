import java.io.*;
import java.sql.*;

public class Insertion {

    Connection pos_conn;
    Statement pos_stmt;
    int test_no;

    public Insertion (Connection pos_conn, Statement pos_stmt, int test_no) {
      this.pos_conn = pos_conn;
      this.pos_stmt = pos_stmt;
      this.test_no = test_no;
    }

  public void insertTuples() {
    if (test_no == 0) {
      System.out.println("\"One\" tuple at a time is inserted");
    }

    if (test_no == 1) {
      System.out.println("\"Multiple\" tuples are inserted");
    }

    if (test_no == 2) {
      System.out.println("A \"mixed\" workload is done");
    }

    if (test_no < 0 || test_no > 2) {
      System.out.println("Problems with the number passed");
    }
  }

}
