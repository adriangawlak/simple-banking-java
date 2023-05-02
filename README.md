# Simple Banking System

This is a Java application for a simple banking system that allows users to create new bank accounts, 
log into existing accounts, check their balance, deposit money, transfer money, and close their accounts. 
All operations are stored in a SQLite database. You can simply start using the program and it will create an empty database (or use an existing one).
This application uses Java with manually configured JDBC requests, SQL statements and an embedded SQLite database.

The program utilises Luhn algorithm to generate checksum and valid card numbers. Algorithm is widely used in the banking sector.  
Card numbers have 16 digits:  
* 6 digits - Bank Identification Number
* 9 digits - account number
* 1 digit - checksum that approves validity  

You can find more details about it here:  [Luhn algorithm in Wikipedia](https://en.wikipedia.org/wiki/Luhn_algorithm)

### Default Database

The program automatically creates a new SQLite database named "accountsDatabase" when you run the program for the first time.  
The database is stored in the "./src/database/" directory.

### Getting Started

To run this application, you will need to have Java, an IDE and SQLite database installed on your system.

## Running the Application

#### In an IDE

You can run the application in an IDE such as IntelliJ IDEA or Eclipse by running the Main class located in the banking package.  
Once the application is running, you will be presented with a menu of options that allow you to interact with the application.

#### On the Command Line

To run the application from the command line, you can build the application yourself using a tool like Maven,  
or compile the application using javac and then run the compiled Java class.
