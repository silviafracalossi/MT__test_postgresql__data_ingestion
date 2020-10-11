import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.*;
import java.io.*;

public class Main {

  // Defining which database to connect to
  static final boolean useServerPostgresDB = false;

  // LOCAL Configurations
  static final String local_DB_URL = "jdbc:postgresql://localhost/";
  static final String local_DB_NAME = "thesis_data_ingestion";
  static final String local_DB_USER = "postgres";
  static final String local_DB_PASS = "silvia";

  // Configurations to server PostgreSQL database
  static final String DB_URL = "jdbc:postgresql://ironmaiden.inf.unibz.it/";
  static final String DB_NAME = "sfracalossi";
  static String DB_USER;
  static String DB_PASS;

  // Defining the connection and statement variables for PostgreSQL
  static Connection pos_conn = null;
  static Statement pos_stmt = null;

  // Tests configurations
  static String[] location_types = {"ironmaiden", "ironlady", "pc"};
  static String[] insertion_types = {"one", "multiple", "mixed"};
  static String[] index_types = {"no", "timestamp", "timestamp_and_value"};
  static int location_no=0, insertion_no=0, index_no=0;

  // Logger names date formatter
  static String logs_path = "logs/";
  static SimpleDateFormat simpleDateFormat =
    new SimpleDateFormat("YYYY-MM-dd__HH.mm.ss");


  // TODOS:
  // - Fill empty methods of configurations
  // - Find more data
  // - Fix connection to server database
  // - Answer to Anton's email


	public static void main(String[] args) throws IOException {

    try {

      // Instantiate general logger
      Logger general_logger = instantiateLogger("general");

      // Understanding where the script is executed
      if (args.length == 1) {
        location_no = returnStringIndex(location_types, args[0]);
        if (location_no == 0) {
          general_logger.severe("Unusual arg specified.");
          return;
        }
      } else {
        general_logger.severe("No args specified.");
        return;
      }

      // Loading the credentials to the new postgresql database
      general_logger.info("Reading credentials");
      try {
        File myObj = new File("resources/server_postgresql_credentials.txt");
        Scanner myReader = new Scanner(myObj);
        DB_USER = myReader.nextLine();
        DB_PASS = myReader.nextLine();
        myReader.close();
      } catch (FileNotFoundException e) {
        general_logger.severe("Please, remember to create the database"+
          "credentials file (see README)");
        e.printStackTrace();
      }

      // Removing the table in case it was still there from previous tests
      general_logger.info("Preparing database for tests");
      preparingDatabase();

      // Marking start of tests
      general_logger.info("Executing tests from " +location_types[location_no-1]);
      general_logger.info("---Start of all Tests!---");

      // Iterating through the tests to be done
      for (int insertion_no=0; insertion_no<3; insertion_no++) {
        for (int index_no=0; index_no<3; index_no++) {

          // Printing out the test configuration and creating logger
          String test_configuration = ""+(location_no+1)+(insertion_no+1)+(index_no+1);
          Logger test_logger = instantiateLogger("test_" + test_configuration);
          test_logger.info("Test #" + test_configuration
            +" executing from the machine \"" +location_types[location_no]+ "\","
            +" having \"" +insertion_types[insertion_no]+ "\" insertion_nos at a time,"
            +" and \""+index_types[index_no]+"\" index_no set.");

          // Opening a connection to the postgreSQL database
          test_logger.info("Connecting to the PostgreSQL database...");
          createDBConnection();

          // Creating the test table
          boolean created_table = createTestTable();
          if (created_table) {
            test_logger.info("Created table \"Test_Table\"");
          } else {
            test_logger.severe("Table not created!");
            return;
          }

          // Applying the specified index
          test_logger.info("Setting index...");
          Index index = new Index(pos_conn, pos_stmt, index_no);
          index.applyIndex();

          // ==START OF TEST==
          test_logger.info("--Start of test #"+test_configuration+"--");
          general_logger.info("--Start of test #"+test_configuration+"--");

          // Inserting the tuples based on the specified methodology
          test_logger.info("Inserting tuples...");
          Insertion insertion = new Insertion(pos_conn, pos_stmt, insertion_no);
          insertion.insertTuples();

          // ==END OF TEST==
          general_logger.info("--End of test #"+test_configuration+"--");
          test_logger.info("--End of test #"+test_configuration+"--");

          // Clean database and close connections
          endOfTest();
        }
      }

       // Printing the end of the tests
       general_logger.info("--- End of all Tests! ---");

    } catch(Exception e) {
       e.printStackTrace();
    } finally {
       try{
          if(pos_stmt!=null) pos_stmt.close();
       } catch(SQLException se2) {
           se2.printStackTrace();
       }
       try {
          if(pos_conn!=null) pos_conn.close();
       } catch(SQLException se){
          se.printStackTrace();
       }
    }
  }

  //-----------------------UTILITY----------------------------------------------

  // Instantiating the logger for the general information or errors
  public static Logger instantiateLogger (String file_name) throws IOException {

    // Retrieving and formatting current timestamp
    Date date = new Date();
    Timestamp now = new Timestamp(date.getTime());
    String dateAsString = simpleDateFormat.format(now);

    // Setting the name of the folder
    if (file_name.compareTo("general") == 0) {
      logs_path += dateAsString+ "/";
      File file = new File(logs_path);
      boolean bool = file.mkdirs();
    }

    // Instantiating general logger
    String log_complete_path = logs_path + dateAsString + "__" + file_name
        + "__data_ingestion.xml";
    Logger logger = Logger.getLogger("DataIngestionGeneralLog_"+file_name);
    logger.setLevel(Level.ALL);

    // Loading properties of log file
    Properties preferences = new Properties();
    try {
        FileInputStream configFile = new FileInputStream("resources/logging.properties");
        preferences.load(configFile);
        LogManager.getLogManager().readConfiguration(configFile);
    } catch (IOException ex) {
        System.out.println("[WARN] Could not load configuration file");
    }

    // Instantiating file handler
    FileHandler gl_fh = new FileHandler(log_complete_path);
    logger.addHandler(gl_fh);

    // Returning the logger
    return logger;
  }


  // Returns the index_no of the specified string in the string array
  public static int returnStringIndex(String[] list, String keyword) {
    for (int i=0; i<list.length; i++) {
      if (list[i].compareTo(keyword) == 0) {
        return i;
      }
    }
    return -1;
  }


  // Cleans the database and closes all the connections to it
  public static void endOfTest() {
    removeTestTable();
    closeDBConnection();
  }

  //----------------------DATABASE----------------------------------------------

  // Connecting to the PostgreSQL database
  public static void createDBConnection() {
    try {

      // Creating the connection URL
      String pos_complete_url;
      if (useServerPostgresDB) {
         pos_complete_url = DB_URL + DB_NAME +
         "?user=" + DB_USER +"&password=" + DB_PASS;
      } else {
         pos_complete_url = local_DB_URL + local_DB_NAME +
         "?user=" + local_DB_USER +"&password=" + local_DB_PASS;
      }

      // Connecting and creating a statement
      pos_conn = DriverManager.getConnection(pos_complete_url);
      pos_stmt = pos_conn.createStatement();
    } catch (SQLException e) {
      System.out.println("Problems with creating the database connection");
      e.printStackTrace();
    }
  }


  // Removing the table in case it was still there from previous tests
  public static void preparingDatabase() {
    createDBConnection();
    removeTestTable();
    closeDBConnection();
  }


  // Creating the table "test_table" in the database
  public static boolean createTestTable () {
    try {
      String test_table_creation = "CREATE TABLE test_table (" +
              "    time timestamp NOT NULL," +
              "    value int NOT NULL" +
              ")";
      return (pos_stmt.executeUpdate(test_table_creation) == 0);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }


  // Dropping the table "test_table" from the database
  public static boolean removeTestTable() {
    try {
      String test_table_drop = "DROP TABLE IF EXISTS test_table;";
      return (pos_stmt.executeUpdate(test_table_drop) == 0);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }


  // Closing the connections to the database
  public static void closeDBConnection() {
    try{
       if(pos_stmt!=null) pos_stmt.close();
    } catch(SQLException se2) {
        se2.printStackTrace();
    }
    try {
       if(pos_conn!=null) pos_conn.close();
    } catch(SQLException se){
       se.printStackTrace();
    }

    // Nulling the database variables
    pos_conn = null;
    pos_stmt = null;
  }
}
