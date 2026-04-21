import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeManager {
  private int currentClassID;
  private Connection connection;

  /*
   * Initializes the GradeManager with a database connection.
   */
  public GradeManager() throws SQLException, IOException {
    this.currentClassID = -1;
    this.connection = DatabaseConnection.makeConnection();
  }

  /*
   * Creates a new class with the given parameters.
    * @param courseNumber The course number (e.g., "CS410").
    * @param term The term (e.g., "Sp20").
    * @param sectionNumber The section number. (e.g., 1).
    * @param description The class description/Title. (e.g., "Databases")
   */
  public void newClass(String courseNumber, String term, int sectionNumber, String description) {
    String query = "INSERT INTO Class " +
                   "(CourseNumber, Term, SectionNumber, Description)" + 
                   "VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setString(1, courseNumber);
      stmt.setString(2, term);
      stmt.setInt(3, sectionNumber);
      stmt.setString(4, description);

      // Action w/query results
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("The following class has been successfully created:");
        System.out.println(courseNumber + " " + term + " " + sectionNumber + " " + description);
      } else {
        System.out.println("Failed to add class.");
      }
    } catch (SQLException e) {
			System.err.println("SQLException: " + e.getMessage());
			System.err.println("SQLState: " + e.getSQLState());
			System.err.println("VendorError: " + e.getErrorCode());
		}
  }

  /*
   * Lists all classes in the database.
   * Includes class ID, course number, term, section number, and number of students.
   * This method includes classes with zero students enrolled due to the LEFT JOIN with the Enrolled table.
   */
  public void listClasses() {
    String query = "SELECT ID, CourseNumber, Term, SectionNumber, count(StudentID) AS numStudents" +
                   "FROM Class LEFT JOIN Enrolled" +
                   "ON Class.ID = Enrolled.ClassID " +
                   "GROUP BY Class.ID";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      ResultSet rs = stmt.executeQuery();

      // Action w/query results
      System.out.println("ID | Course | Term | Section | Students");
      while (rs.next()) {
        System.out.println(rs.getInt("ID") + " " + rs.getString("CourseNumber") + " " + rs.getString("Term") + " " + rs.getInt("SectionNumber") + " " + rs.getInt("numStudents"));
      }
    } catch (Exception e) {
			System.err.println("Error listing classes: " + e.getMessage());
    }
  }

  // Sets currentClassID
  public void selectClassWithSection(String courseNumber, String term, int sectionNumber) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public void selectClassWithTerm(String courseNumber, String term) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public void selectClass(String courseNumber) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public String showClass() {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public String showCategories() {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public void addCategory(String name, double weight) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public String showAssignment() {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public void addAssignment(String name, String categoryName, String description, int points) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public void addStudent(String username) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public String showStudents() {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public String showStudentsWithSearch(String searchTerm) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public void grade(String assignmentName, String username, int grade) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public String studentGrades(String username) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public String gradebook() {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }
}
