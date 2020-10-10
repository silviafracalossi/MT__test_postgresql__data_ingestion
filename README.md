# Test - Postgresql - Data Ingestion

Tester of the Postgresql ability of ingesting time series data


## Requirements
- PostgreSQL JDBC Driver (42.2.14)


## Installation
- Create the folder "data";
  - Inside the folder, copy-paste the printers' parsed log files;
- Create the folder "resources";
  - Inside the folder, create a file called "server_postgresql_credentials.txt", containing the username (first line) and the password (second line) to access the server PostgreSQL database;
  - Inside the folder, copy-paste the PostgreSQL driver (called "postgresql-42.2.14.jar");


## Running the project
- 'bash compile_and_run.bash'


## Repository Structure
- 'build/', containing the generated .class files after compiling the java code;
- 'data/', containing the printers' parsed logs files in the format of CSV files;
- 'logs/', containing the log information of all the tests done;
- 'resources/', containing the postgresql driver and the database credentials file;
- 'src/', containing the java source files.

In the main folder, there is:
- 'compile_and_run.bash', a bash file containing the commands for compiling the java code and running it.
