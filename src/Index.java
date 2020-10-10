import java.io.*;
import java.sql.*;

public class Index {

  Connection pos_conn;
  Statement pos_stmt;
  int test_no;

  public Index (Connection pos_conn, Statement pos_stmt, int test_no) {
    this.pos_conn = pos_conn;
    this.pos_stmt = pos_stmt;
    this.test_no = test_no;
  }

  public void applyIndex() {
    if (test_no == 0) {
      System.out.println("\"No\" indexes has to apply");
    }

    if (test_no == 1) {
      System.out.println("Index on the \"timestamp\" is applied");
    }

    if (test_no == 2) {
      System.out.println("Indexes on the \"timestamp and value\" is applied");
    }

    if (test_no < 0 || test_no > 2) {
      System.out.println("Problems with the number passed");
    }
  }

}
