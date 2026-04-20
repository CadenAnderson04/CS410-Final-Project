import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection makeConnection () {
        try {
            Connection conn = null;
            conn = DriverManager.getConnection(
                "jdbc:mysql://onyx.boisestate.edu:50212/grades?verifyServerCertificate=false&useSSL=true",
                "msandbox",  
                "NOSaints04"
            );
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
