package utilities;

import com.mysql.jdbc.Connection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import models.User;

/**
 *
 * @author Shawn
 */
public class DBConnection {
    
    // These items are for the JDBC URL
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    // IP address + database name
    private static final String ipAddress = "//3.227.166.251/U06eXL";
    //Complete JDBC URL
    private static final String jdbcURL = protocol + vendorName + ipAddress;
    //reference to JDBC driver interface that we imported
    private static final String MYSQLJDBCDriver = "com.mysql.jdbc.Driver";
    //reference to connection interface
    private static Connection conn = null;
    //database username
    private static final String username = "U06eXL";
    //database password
    private static final String password = "53688745504";
    //These items are for login validation
    public static User loggedInUser;
    
    private static final String FILENAME = "log.txt";
    
    
    /* we are going to need a reference to a connection object. we are not
       going to get the connection object until we use the driver manager 
       to access the connection*/
    //This connects to the DB
    public static Connection startConnection(){
        //this will look through project directory and get driver
        try {
            Class.forName(MYSQLJDBCDriver);
            conn = (Connection)DriverManager.getConnection(jdbcURL, username, password); //returns our database conncetion
            System.out.println("Connection Successfull");
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        return conn;
    }
    
    //This method returns the DB connection
    public static Connection getConnection(){
        return conn;
    }
    
    //This closes the DB connection
    public static void closeConnection(){
        try {
            conn.close();
            System.out.println("Connection closed");
        } catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    //return user that is currently logged in
    public User getLoggedInUser(){
        return loggedInUser;
    }
    
    //Validate login attempted
    public static Boolean validateLogin(String username, String password){
        QueryManager.makeQuery("SELECT * FROM user WHERE userName='" + username + "' AND password='" + password + "'");
        ResultSet userSet = QueryManager.getResult();
        try {
            //call next() because internal cursor initially doesn't point to anything
            if(userSet.next()){
                loggedInUser = new User();
                loggedInUser.setUsername(userSet.getString("userName"));
                loggedInUser.setuserID(userSet.getInt("userId"));
                //Log user successful log-in activity and append to .txt file if one exists
                log(username, true);
                return true;
            } else {
                //log failed log in attempt and append to .txt file if one exists
                log(username, false);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    public static void log(String userName, boolean success){
        try (FileWriter fw = new FileWriter(FILENAME, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw)) {
            pw.println(ZonedDateTime.now() + " User: " + userName + " Login_Attempt: " + (success ? " Success" : " Failure"));
        } catch (IOException e) {
            System.out.println("Logger Error: " + e.getMessage());
        }
    }
}
