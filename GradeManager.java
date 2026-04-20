import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeManager {
  private int currentClassID;
  private Connection connection;

  public GradeManager() throws SQLException, IOException {
    this.currentClassID = -1;
    this.connection = DatabaseConnection.makeConnection();
  }

  public void newClass(String courseNumber, String term, int sectionNumber, String description) {
    String query = "INSERT INTO class (course_number, term, section_number, description) VALUES (?, ?, ?, ?)";
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
   * Returns the fully-formatted string that can be printed to the console.
   * Probably should include the term and section number but not description?
   * There could be an option to include description
   *
   * Another option would be to return a result set and format the string in the
   * driver class
   */
  public String listClasses() {
    throw new UnsupportedOperationException("Not Implemented Yet");
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
