# Test - Postgresql - Data Ingestion

Tester of the Postgresql ability of ingesting time series data

## Repository Structure
-   `build/`, containing the generated .class files after compiling the java code;
-   `data/`, containing the printers parsed logs files in the format of CSV files;
-   `logs/`, containing the log information of all the tests done;
-   `resources/`, containing the postgresql driver, the database credentials file and the logger properties;
-   `src/`, containing the java source files.

In the main directory, there is:
-   `compile_and_run.bash`, a bash file containing the commands for compiling the java code and running it.


## Requirements
-   PostgreSQL JDBC Driver (42.2.14)


## Installation
-   Create the folder `build`;
-   Create the folder `data`;
    -   Inside the folder, copy-paste the printers parsed log files;
-   Inside the folder `resources`,
    -   Create a file called `server_postgresql_credentials.txt`, containing the username (first line) and the password (second line) to access the server PostgreSQL database;
    -   Copy-paste the indicated PostgreSQL driver (called `postgresql-42.2.14.jar`);


## Running the project
-   `bash compile_and_run.bash`


## Preparing a standalone version
Since I couldn't manage to find a way with the command line, I used Eclipse:
-   Open the project in Eclipse;
-   Set Java 8 as the default JRE:
    -   `Window > Preferences > Java > Installed JREs`;
    -   Select Java 8;
    -   `Apply and Close`;
-   Set Java 8 as the compiler version:
    -   `Window > Preferences > Java > Compiler`;
    -   Compiler compliance level: `1.8`;
    -   `Apply and Close`;
-   Create the JAR file:
    - Right-click on the project folder > `Export`;
    - `Java > Runnable JAR file > Next`;
    - Launch Configuration: `Main`;
    - Export destination: `test_postgresql_data_ingestion\DataIngestionTest.jar`;
    - `Finish`.
-   Execute the JAR file:
    - Copy-paste the JAR file in an empty folder;
    - In the folder, create the folder `resources` and copy-paste the `resources/logging.properties` and the `resources/server_postgresql_credentials.txt` files;
    - Create the folder `data` and copy-paste the printers parsed log files;
