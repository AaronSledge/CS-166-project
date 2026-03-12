/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Swing java for gui extra credit!!!!!
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class EmbeddedSQL {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of EmbeddedSQL
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public EmbeddedSQL (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end EmbeddedSQL

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            EmbeddedSQL.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      EmbeddedSQL esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the EmbeddedSQL object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new EmbeddedSQL (dbname, dbport, user, "");


         boolean quit = false;

         //welcome page
         System.out.println("Welcome to **** mechanics:");
         System.out.println("---------");
         System.out.println("Do you need to be added to the system: Type Yes/No");
         String answer = readStringChoice();
         int employeeID = 0;
         if(answer.equalsIgnoreCase("yes")) {
            employeeID = newMechanic(esql);
         }

         boolean isEmployee = false;

         while(!isEmployee) {
             System.out.println("Enter your work ID to log in: ");
             employeeID = readIntChoice();
             int rowCount = MechanicExists(esql, employeeID);
             if(rowCount <= 0) {
               System.out.println("Can't find ID. Please re type ID");
             }
             else {
               isEmployee = true;
             }
         }

         String vin = "";

         int rowCount = IsAlreadyWorking(esql, employeeID);
         if(rowCount > 0) {
            System.out.println("Enter the VIN number as seen on screen");
            vin = readStringChoice();
         }
         while(!quit) {
            // These are sample SQL statements
            System.out.println("Are you here for ");
            System.out.println("0. Type 0 if you wish to add a customer");
            System.out.println("1. Type 1 if you wish to open add a car");
            System.out.println("2. Type 2 if you wish to open a service request");
            System.out.println("3. Type 3 if you wish to work on a specfic car");
            System.out.println("4. Type 4 if you wish to close a service request");
            System.out.println("5. Type 5 if you wish to list customers that paid less than 100 dollars");
            System.out.println("6. Type 6 if you wish to list customers with more than 20 cars");
            System.out.println("7. Type 7 if you wish to list cars built before 1995 that has less than 50000 miles"); 
            System.out.println("8. Type 8 if you wish to list the first k cars with the highest number of service requests");
            System.out.println("9. Type 9 if you wish to list all the customers and total bill in descending order");
            System.out.println("10. < EXIT");

            switch (readIntChoice()){
               case 0: newCustomer(esql); break;
               case 1: newVehicle(esql); break;
               case 2: ServiceRequest(esql, employeeID); break;
               case 3: vin = SelectCar(esql, employeeID, vin); break;
               case 4: vin = CloseRequest(esql, employeeID, vin); break;
               case 5: Query2(esql); break;
               case 6: Query3(esql); break;
               case 7: Query4(esql); break;
               case 8: Query5(esql); break;
               case 9: Query6(esql); break;
               case 10: quit = true; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main


   public static String newCustomer(EmbeddedSQL esql) {
     boolean correctPhoneNum = false;
     String phoneNum = "";
     while(!correctPhoneNum) {
      System.out.println("Enter customer's phone number: ");
      phoneNum = readStringChoice();
      int rowCount = PhoneNumExists(esql, phoneNum);
      if(rowCount > 0) {
         System.out.println("Phone number already exists: Please re enter: ");
      }
      else {
         correctPhoneNum = true;
      }
     }
     
      System.out.println("Enter customer's first name: ");
      String firstName = readStringChoice();
      System.out.println("Enter customer's last name: ");
      String lastName = readStringChoice();
      System.out.println("Enter customer's address: ");
      String address = readStringChoice();
      AddCustomer(esql, firstName, lastName, phoneNum, address);
      return lastName;
   }

   public static String newVehicle(EmbeddedSQL esql) {
      boolean correctVin = false;
      String vin = "";
      while(!correctVin) {
         System.out.println("Please type in vehicle's VIN: ");
         vin = readStringChoice();
         int rowCount = VinExists(esql, vin);
         if(rowCount > 0) {
            System.out.println("VIN already taken: Please enter correct VIN");
         }
         else {
            correctVin = true;
         }
      }
      System.out.println("Please type in vehicle's year: ");
      int year = readIntChoice();
      System.out.println("Please type in vehicle's make: ");
      String make = readStringChoice();
      System.out.println("Please type in vehicle's model: ");
      String model = readStringChoice();


      AddVehicle(esql, vin, year, make, model);

      boolean correctPhoneNum = false;
      String phoneNum = "";
      while(!correctPhoneNum) {
         System.out.println("Please type in your phone number: ");
         phoneNum = readStringChoice();
         int rowCount = PhoneNumExists(esql, phoneNum);
         if(rowCount <= 0) {
            System.out.println("Phone number incorrect: Do you want to be added as customer. If not retry entering phone number again(Yes/No): ");
            String answer = readStringChoice();
            if(answer.equalsIgnoreCase("Yes")) {
               String lastName = newCustomer(esql);
               break;
            }
         }
         else {
            correctPhoneNum = true;
         }
      }
      CustomerOwns(esql, phoneNum, vin);
      return vin;
   }

   public static int newMechanic(EmbeddedSQL esql) {
      boolean isTaken = false;
      int employeeID = 0;
      while(!isTaken) {
         System.out.println("Enter your work ID: ");
         employeeID = readIntChoice();
         int rowCount = AlreadyWorker(esql, employeeID);
         if(rowCount > 0) {
            System.out.println("ID already taken, please re enter ID: ");
         }
         else {
            isTaken = true;
         }
      }
      System.out.println("Enter your first name: ");
      String firstName = readStringChoice();
      System.out.println("Enter your last name: ");
      String lastName = readStringChoice();
      System.out.println("Enter how many years of experience you have: ");
      int experience = readIntChoice();
      AddMechanic(esql, employeeID, firstName, lastName, experience);
      return employeeID;
   }
   public static void ServiceRequest(EmbeddedSQL esql, int employeeID) {
      System.out.println("Enter your customer's last name: ");
      String lastName = readStringChoice();
      int rowCount = CustomerExists(esql, lastName);
      if(rowCount <= 0) {
         System.out.println("Customer does not exist, please add to database");
         lastName = newCustomer(esql);
      }

      DisplayCustomers(esql, lastName);

      boolean correctPhoneNum = false;
      String phoneNum = "";
      while(!correctPhoneNum) {
         System.out.println("Please type in phone number to select customer: ");
         phoneNum = readStringChoice();
         rowCount = PhoneNumMatch(esql, phoneNum, lastName);
         if (rowCount <= 0) {
            System.out.println("Cannot find phone number in our records. Please re enter");
         }
         else {
            correctPhoneNum = true;
         }
      }

      rowCount = VehicleExists(esql, phoneNum);
      String vin = "";
      if(rowCount <= 0) {
         System.out.println("It seems you have no cars added, please add car to database");
         vin = newVehicle(esql);
      }
      else {
         boolean correctVin = false;
         while(!correctVin) {
            System.out.println("Pick a car for service");
            DisplayVehicles(esql, phoneNum);
            vin = readStringChoice();
            rowCount = VinMatch(esql, phoneNum, vin);
            if(rowCount <= 0) {
               System.out.println("Cannot find VIN in our records. Please re enter");
            }
            else {
               correctVin = true;
            }
         }
      }


      int requests = NumRequests(esql);
      System.out.println("Create service request.");
      LocalDate today = LocalDate.now();
      String status = "Open";
      System.out.println("Please enter odometer reading: ");
      int odometer = readIntChoice();
      System.out.println("Please type a short description of the service needed: ");
      String description = readStringChoice();

      AddService(esql, requests + 101, today, status, odometer, description);
      CarsNeedsService(esql, vin, requests + 101);
   }

   public static String SelectCar(EmbeddedSQL esql, int employeeID, String vin) {
       int rowCount = IsAlreadyWorking(esql, employeeID);

      if(rowCount >= 1) {
         System.out.println("It seems you are already working on another car. Please let another employee handle this car.");
         return vin;
      }
      rowCount = DisplayALLVehicles(esql);
      if(rowCount <= 0) {
         System.out.println("No cars require service as of right now.");
         return vin;
      }

      boolean correctVin = false;
      vin = "";
      while(!correctVin) {
         System.out.println("Please type in vehicle's VIN: ");
         vin = readStringChoice();
         rowCount = VinExists(esql, vin);
         if(rowCount <= 0) {
            System.out.println("VIN doesn't exist: Re enter the correct vin number");
         }
         else {
            correctVin = true;
         }
      }
      WorksOn(esql, employeeID, vin);
      return vin;
   }

   public static String CloseRequest(EmbeddedSQL esql, int employeeID, String vin) {
      int rowCount = IsAlreadyWorking(esql, employeeID);
      if(rowCount <= 0) {
         System.out.println("It seems you are not working on a car at the moment. Please open a service request first.");
         return vin;
      }

      boolean correctCar = false;
      boolean validServiceID = false;
      int serviceID = 0;
      while(!validServiceID) {

         rowCount = NumOfService(esql, vin);
         if (rowCount <= 0) {
            System.out.println("You are not working on any serivce requests at the moment. Please open a request");
            return vin;
         }
         System.out.println("Please select the service number you wish to close: ");
         DisplayServices(esql, vin);

         serviceID = readIntChoice();
         rowCount = ServiceExists(esql, serviceID);
         if(rowCount <= 0) {
            System.out.println("Service ID does not exist in dataset: Please re enter ID: ");
         }
         else {
            validServiceID = true;
         }
      }

      System.out.println("Closing Service request, please add in some last comments: ");
      String comments = readStringChoice();
      System.out.println("Enter the final bill for service: ");
      int bill = readIntChoice();
      LocalDate today = LocalDate.now();

      Handles(esql, employeeID, serviceID, bill, today, comments);
      UpdateService(esql, serviceID);
      rowCount = AviliableServices(esql, vin);
      if(rowCount <= 0) {
         System.out.println("Car has completed all service requests needed");
         DeleteCar(esql, employeeID, vin);
         vin = "";
      }

      return vin;
   }

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static String readStringChoice() {
      String input;
      Scanner scanner = new Scanner(System.in);
      // returns only if a correct value is given.
      do {
         try { // read the integer, parse it and break.
            input = scanner.nextLine();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   public static int readIntChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }

   public static void DisplayCustomers(EmbeddedSQL esql, String lastName) {
      try {
         String sql = "SELECT Phone_Num, First_Name, Last_Name, Address " +
                      "FROM Customer " +
                      "WHERE Last_Name = '" + lastName + "';";
         esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void DisplayVehicles(EmbeddedSQL esql, String phoneNum) {
      try {
         String sql = "SELECT C.VIN, C.Year, C.Make, C.Model " +
                      "FROM Car C, Owns O " +
                      "WHERE C.VIN = O.VIN AND O.Phone_Num = '" + phoneNum + "';";
         esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static int DisplayALLVehicles(EmbeddedSQL esql) {
      try {
         String sql = "SELECT DISTINCT N.VIN " +
                      "FROM Needs N, Service S " +
                      "WHERE N.SERVICE_ID = S.ID AND S.STATUS = 'Open';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }

      return 0;
   }

   public static void DisplayServices(EmbeddedSQL esql, String vin) {
      try {
         String sql = "SELECT S.ID, S.Description " +
                      "FROM Service S, Needs N " +
                      "WHERE S.ID = N.Service_ID " +
                      "AND N.VIN = '" + vin + "' " +
                      "AND S.Status = 'Open';";
         esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static int NumOfService(EmbeddedSQL esql, String vin) {
      try {
         String sql = "SELECT S.ID, S.Description " +
                      "FROM Service S, Needs N " +
                      "WHERE S.ID = N.Service_ID " +
                      "AND N.VIN = '" + vin + "' " +
                      "AND S.Status = 'Open';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int NumRequests(EmbeddedSQL esql) {
      try {
         String sql = "SELECT * FROM Service;";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int CustomerExists(EmbeddedSQL esql, String lastName){
      try {
         String sql = "SELECT * FROM Customer WHERE Last_Name = '" + lastName + "';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int MechanicExists(EmbeddedSQL esql, int employeeID) {
      try {
         String sql = "SELECT * FROM Mechanic WHERE ID = " + employeeID + ";";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int PhoneNumExists(EmbeddedSQL esql, String phoneNum) {
      try {
         String sql = "SELECT * FROM Customer WHERE Phone_Num = '" + phoneNum + "';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int PhoneNumMatch(EmbeddedSQL esql, String phoneNum, String lastName) {
      try {
         String sql = "SELECT * FROM Customer WHERE Phone_Num = '" + phoneNum + "' AND Last_Name = '" + lastName + "';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int VehicleExists(EmbeddedSQL esql, String phoneNum) {
      try {
         String sql = "SELECT * FROM Owns WHERE Phone_Num = '" + phoneNum + "';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int VinExists(EmbeddedSQL esql, String vin) {
      try {
         String sql = "SELECT * FROM Car WHERE VIN = '" + vin + "';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int VinMatch(EmbeddedSQL esql, String phoneNum, String vin) {
      try {
         String sql = "SELECT * FROM Owns WHERE Phone_Num = '" + phoneNum + "' AND VIN = '" + vin + "';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int ServiceExists(EmbeddedSQL esql, int serviceID) {
      try {
         String sql = "SELECT * FROM Service WHERE ID = " + serviceID + ";";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int AviliableServices(EmbeddedSQL esql, String vin) {
      try {
         String sql = "SELECT * " +
                      "FROM Service S, Needs N " +
                      "WHERE S.ID = N.Service_ID " +
                      "AND N.VIN = '" + vin + "' " +
                      "AND S.Status = 'Open';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int IsAlreadyWorking(EmbeddedSQL esql, int ID) {
      try {
         String sql;
         sql = "SELECT * FROM Works_On WHERE Mechanic_ID = " + ID + ";";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static int AlreadyWorker(EmbeddedSQL esql, int ID) {
      try {
         String sql;
         sql = "SELECT * FROM Mechanic WHERE ID = " + ID + ";";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }

   public static void CustomerOwns(EmbeddedSQL esql, String phoneNum, String vin) {
      try {
         String sql = "INSERT INTO Owns (Phone_Num, VIN) " +
                      "VALUES ('" + phoneNum + "', '" + vin + "');";
         esql.executeUpdate(sql);
         System.out.println("Ownership added.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void CarsNeedsService(EmbeddedSQL esql, String vin, int ID) {
      try {
         String sql = "INSERT INTO Needs (VIN, Service_ID) " +
                      "VALUES ('" + vin + "', " + ID + ");";
         esql.executeUpdate(sql);
         System.out.println("Service linked to car.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void WorksOn(EmbeddedSQL esql, int ID, String vin) {
      try {
         String sql = "INSERT INTO Works_On (Mechanic_ID, VIN) " +
                      "VALUES (" + ID + ", '" + vin + "');";
         esql.executeUpdate(sql);
         System.out.println("Mechanic assigned to car.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void Handles(EmbeddedSQL esql, int employeeID, int serviceID, int bill, LocalDate today, String comments) {
      try {
         String sql = "INSERT INTO Handles (Mechanic_ID, Service_ID, Bill, Closed_Date, Comments) " +
                      "VALUES (" + employeeID + ", " + serviceID + ", " + bill + ", '" + today.toString() + "', '" + comments + "');";
         esql.executeUpdate(sql);
         System.out.println("Service request closed record inserted.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static int CorrectWorker(EmbeddedSQL esql, int employeeID, String vin) {
      try {
         String sql;
         sql = "SELECT * FROM Works_On WHERE Mechanic_ID = " + employeeID + " AND VIN = '" + vin + "';";
         return esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return 0;
   }
   
   public static void AddCustomer(EmbeddedSQL esql, String firstName, String lastName, String phoneNum, String address){
      try {
         String sql = "INSERT INTO Customer (Phone_Num, First_Name, Last_Name, Address) " +
                      "VALUES ('" + phoneNum + "', '" + firstName + "', '" + lastName + "', '" + address + "');";
         esql.executeUpdate(sql);
         System.out.println("Customer added.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
         
      }
   }

   public static void AddMechanic(EmbeddedSQL esql, int ID, String firstName, String lastName, int experience){
      try {
         String sql = "INSERT INTO Mechanic (ID, First_Name, Last_Name, Experience) " +
                      "VALUES (" + ID + ", '" + firstName + "', '" + lastName + "', " + experience + ");";
         esql.executeUpdate(sql);
         System.out.println("Mechanic added.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void AddVehicle(EmbeddedSQL esql, String vin, int year, String make, String model){
      try {
         String sql = "INSERT INTO Car (VIN, Year, Make, Model) " +
                      "VALUES ('" + vin + "', " + year + ", '" + make + "', '" + model + "');";
         esql.executeUpdate(sql);
         System.out.println("Vehicle added.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void AddService(EmbeddedSQL esql, int ID, LocalDate today, String status, int odometer, String text) {
      try {
         String sql = "INSERT INTO Service (ID, Open_Date, Status, Odometer, Description) " +
                      "VALUES (" + ID + ", '" + today.toString() + "', '" + status + "', " + odometer + ", '" + text + "');";
         esql.executeUpdate(sql);
         System.out.println("Service request added.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void UpdateService(EmbeddedSQL esql, int serviceID) {
      try {
         String sql = "UPDATE Service " +
                   "SET Status = 'Closed' " +
                   "WHERE ID = " + serviceID + ";";
         esql.executeUpdate(sql);
         System.out.println("Service updated to Closed.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void DeleteCar(EmbeddedSQL esql, int employeeID, String vin) {
      try {
         String sql = "DELETE FROM Works_On " +
                   "WHERE Mechanic_ID = " + employeeID + " AND VIN = '" + vin + "';";
         esql.executeUpdate(sql);
         System.out.println("Mechanic removed from active car assignment.");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void Query2(EmbeddedSQL esql){
      try {
         String sql = "SELECT Closed_Date, Comments, Bill " +
                   "FROM Handles " +
                   "WHERE Bill < 100 " +
                   "ORDER BY Closed_Date;";
         esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void Query3(EmbeddedSQL esql){
      try {
         String sql = "SELECT C.First_Name, C.Last_Name " +
                   "FROM Customer C, Owns O " +
                   "WHERE C.Phone_Num = O.Phone_Num " +
                   "GROUP BY C.Phone_Num, C.First_Name, C.Last_Name " +
                   "HAVING COUNT(DISTINCT O.VIN) > 20;";
         esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void Query4(EmbeddedSQL esql){
      try {
         String sql = "SELECT DISTINCT C.Make, C.Model, C.Year " +
                   "FROM Car C, Needs N, Service S " +
                   "WHERE C.VIN = N.VIN " +
                   "AND N.Service_ID = S.ID " +
                   "AND C.Year < 1995 " +
                   "AND S.Odometer < 50000;";
         esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void Query5(EmbeddedSQL esql){
      try {
         System.out.print("Enter k: ");
         int k = readIntChoice();

         if (k <= 0) {
            System.out.println("k must be greater than 0.");
            return;
         }

         String sql = "SELECT C.Make, C.Model, COUNT(*) AS Num_Service_Requests " +
                   "FROM Car C, Needs N, Service S " +
                   "WHERE C.VIN = N.VIN " +
                   "AND N.Service_ID = S.ID " +
                   "AND S.Status = 'Open' " +
                   "GROUP BY C.VIN, C.Make, C.Model " +
                   "ORDER BY Num_Service_Requests DESC " +
                   "LIMIT " + k + ";";
         esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void Query6(EmbeddedSQL esql){
      try {
         String sql = "SELECT C.First_Name, C.Last_Name, SUM(H.Bill) AS Total_Bill " +
                   "FROM Customer C, Owns O, Needs N, Handles H " +
                   "WHERE C.Phone_Num = O.Phone_Num " +
                   "AND O.VIN = N.VIN " +
                   "AND N.Service_ID = H.Service_ID " +
                   "GROUP BY C.Phone_Num, C.First_Name, C.Last_Name " +
                   "ORDER BY Total_Bill DESC;";
         esql.executeQuery(sql);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

}//end EmbeddedSQL
