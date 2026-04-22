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

  /*
   * Selects a class based on the course number.
   * The class selected is the only section in the most recent term for the given course number.
   * If there are multiple sections in the most recent term, no class is selected and an error message is printed. 
   * @param courseNumber The course number (e.g., "CS410").
   */
  public void selectClass(String courseNumber) {
    String query = "SELECT ID, CourseNumber, Term, SectionNumber " +
                   "FROM Class " +
                   "WHERE CourseNumber = ? " +
                   "AND Term = " + 
                   "(SELECT MAX(Term) FROM Class WHERE CourseNumber = ?)";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setString(1, courseNumber);
      stmt.setString(2, courseNumber);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      // Check if there are any classes at all for the given course number
      if (!rs.next()) {
        System.out.println("No class found for course number:" + courseNumber);
      } else {
        // Store info for first class which will be used if there is only one class for the given course number and term. 
        int tempClassID = rs.getInt("ID");
        String tempCourseNumber = rs.getString("CourseNumber");
        String tempTerm = rs.getString("Term");
        int tempSectionNumber = rs.getInt("SectionNumber");
        // Check if there are more than one class for the given course number in the most recent term
        if (rs.next()) {
          System.out.println("Multiple sections found for course number: " + courseNumber + " in the most recent term. No class selected.");
          System.out.println("Please select a class with the term and section number.");
        } else {
          // If this code is reached, there is exactly one class for the given course number in the most recent term, so we select that class
          this.currentClassID = tempClassID;
          System.out.println(tempCourseNumber + " " + tempTerm + " " + tempSectionNumber + " has been selected.");
        }
      }
    } catch (Exception e) {
      System.out.println("Error selecting class: " + e.getMessage());
    }
  }

  /*
   * Selects a class based on the course number and term.
   * The class selected is the only section in the given term for the given course number.
   * If there are multiple sections in the given term, no class is selected and an error message is printed.
   * @param courseNumber The course number (e.g., "CS410").
   * @param term The term (e.g., "Sp20").
   */
  public void selectClassWithTerm(String courseNumber, String term) {
    String query = "SELECT ID, CourseNumber, Term, SectionNumber " +
                   "FROM Class " +
                   "WHERE CourseNumber = ? " +
                   "AND Term = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setString(1, courseNumber);
      stmt.setString(2, term);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      // Check if there are any classes at all for the given course number and term
      if (!rs.next()) {
        System.out.println("No class found for course number:" + courseNumber + " and term: " + term);
      } else {
        // Store info for first class which will be used if there is only one class for the given course number and term. 
        int tempClassID = rs.getInt("ID");
        String tempCourseNumber = rs.getString("CourseNumber");
        String tempTerm = rs.getString("Term");
        int tempSectionNumber = rs.getInt("SectionNumber");
        // Check if there are more than one class for the given course number and term
        if (rs.next()) {
          System.out.println("Multiple sections found for course number: " + courseNumber + " and term: " + term + ". No class selected.");
          System.out.println("Please select a class with the section number as well.");
        } else {
          // If this code is reached, there is exactly one class for the given course number and term, so we select that class
          this.currentClassID = tempClassID;
          System.out.println("The following class has been selected:");
          System.out.println(tempCourseNumber + " " + tempTerm + " " + tempSectionNumber + " has been selected.");
        }
      }
    } catch (Exception e) {
      System.out.println("Error selecting class: " + e.getMessage());
    }
  }

  /*
   * Selects a class based on the course number, term, and section number.
   * @param courseNumber The course number (e.g., "CS410").
   * @param term The term (e.g., "Sp20").
   * @param sectionNumber The section number.
   */
  public void selectClassWithSection(String courseNumber, String term, int sectionNumber) {
    String query = "SELECT ID, CourseNumber, Term, SectionNumber " +
                   "FROM Class " +
                   "WHERE CourseNumber = ? " +
                   "AND Term = ? " +
                   "AND SectionNumber = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setString(1, courseNumber);
      stmt.setString(2, term);
      stmt.setInt(3, sectionNumber);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      // Check if there are any classes at all for the given course number, term, and section number
      if (!rs.next()) {
        System.out.println("No class found for course number:" + courseNumber + ", term: " + term + ", and section number: " + sectionNumber);
      } else {
        // If this code is reached, there is in fact a class for the given course number, term, and section number, so we select that class
        this.currentClassID = rs.getInt("ID");
        System.out.println("The following class has been selected:");
        System.out.println(rs.getString("CourseNumber") + " " + rs.getString("Term") + " " + rs.getInt("SectionNumber"));
      }
    } catch (Exception e) {
      System.out.println("Error selecting class: " + e.getMessage());
    }
  }


  public void showClass() {
    String query = "SELECT CourseNumber, Term, SectionNumber, Description " +
                   "FROM Class " +
                   "WHERE ID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setInt(1, this.currentClassID);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      if (rs.next()) {
        System.out.println("Current Class:");
        System.out.println("Class ID: " + this.currentClassID);
        System.out.println("Course Number: " + rs.getString("CourseNumber"));
        System.out.println("Term: " + rs.getString("Term"));
        System.out.println("Section Number: " + rs.getInt("SectionNumber"));
        System.out.println("Description: " + rs.getString("Description"));
      } else {
        System.out.println("No class currently selected.");
      }
    } catch (Exception e) {
      System.out.println("Error showing class: " + e.getMessage());
    }
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
