import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is responsible for establishing a connection to the database.
 * It contains a single static method, makeConnection, which returns a
 * Connection
 * object that can be used to interact with the database.
 * The method handles any SQLExceptions that may occur during the connection
 * process
 * and prints relevant error information to the console.
 */

public class DatabaseConnection {
  /**
   * Establishes a connection to the database.
   * 
   * @return A Connection object representing the connection to the database.
   */
  public static Connection makeConnection() {
    try {
      Connection conn = null;
      conn = DriverManager.getConnection(
          "jdbc:mysql://localhost:50212/grades?verifyServerCertificate=false&useSSL=true",
          "msandbox",
          "NOSaints04");
      System.out.println("Connection to database established successfully.");
      return conn;
    } catch (SQLException e) {
      System.err.println("SQLException: " + e.getMessage());
      System.err.println("SQLState: " + e.getSQLState());
      System.err.println("VendorError: " + e.getErrorCode());
    }
    return null;
  }
}
