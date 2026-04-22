import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeManager {
  private int currentClassID;
  private Connection connection;

  /**
   * Initializes the GradeManager with a database connection.
   */
  public GradeManager() throws SQLException, IOException {
    this.currentClassID = -1;
    this.connection = DatabaseConnection.makeConnection();
  }

  /**
   * Closes the database connection.
   */
  public void closeConnection() {
    try {
      if (this.connection != null && !this.connection.isClosed()) {
        this.connection.close();
        System.out.println("Database connection closed successfully.");
      }
    } catch (SQLException e) {
      System.err.println("Error closing database connection: " + e.getMessage());
    }
  }

  /**
   * Creates a new class with the given parameters.
    * @param courseNumber The course number (e.g., "CS410").
    * @param term The term (e.g., "Sp20").
    * @param sectionNumber The section number. (e.g., 1).
    * @param description The class description/Title. (e.g., "Databases")
   */
  public void newClass(String courseNumber, String term, int sectionNumber, String description) {
    String query = "INSERT INTO Class" +
                   " (CourseNumber, Term, SectionNumber, Description)" + 
                   " VALUES (?, ?, ?, ?)";
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
			System.err.println("Error creating class: " + e.getMessage());
		}
  }

  /**
   * Lists all classes in the database.
   * Includes class ID, course number, term, section number, and number of students.
   * This method includes classes with zero students enrolled due to the LEFT JOIN with the Enrolled table.
   */
  public void listClasses() {
    String query = "SELECT ID, CourseNumber, Term, SectionNumber, count(StudentID) AS numStudents" +
                   " FROM Class LEFT JOIN Enrolled" +
                   " ON Class.ID = Enrolled.ClassID" +
                   " GROUP BY Class.ID";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      ResultSet rs = stmt.executeQuery();

      // Action w/query results
      boolean hasResults = false;
      System.out.println("ID | Course | Term | Section | Students");
      // Only enters if there are classes in the database.
      while (rs.next()) {
        hasResults = true;
        System.out.println(rs.getInt("ID") + " " + rs.getString("CourseNumber") + " " + rs.getString("Term") + " " + rs.getInt("SectionNumber") + " " + rs.getInt("numStudents"));
      }
      if (!hasResults) {
        System.out.println("No classes found.");
      }
    } catch (SQLException e) {
			System.err.println("Error listing classes: " + e.getMessage());
		}
  }

  /**
   * Selects a class based on the course number.
   * The class selected is the only section in the most recent term for the given course number.
   * If there are multiple sections in the most recent term, no class is selected and an error message is printed. 
   * @param courseNumber The course number (e.g., "CS410").
   */
  public void selectClass(String courseNumber) {
    // Schema/Project specifics require term format to be in Sp20 or Fa20.
    String termSubquery = "SELECT Term" +  
                          " FROM Class" + 
                          " WHERE CourseNumber = ?" +
                          // Isolates and orders by the year first.
                          " ORDER BY SUBSTR(Term, 3, 2) DESC," +
                          // Isolates semester and orders by Fall, then Summer (if applicable), then Spring for each year.
                          " CASE SUBSTR(Term, 1, 2)" + 
                          " WHEN 'Fa' THEN 3" + 
                          " WHEN 'SU' THEN 2" +
                          " WHEN 'Sp' THEN 1" + 
                          " END DESC" +
                          " LIMIT 1";
    String query = "SELECT ID, CourseNumber, Term, SectionNumber" +
                   " FROM Class" +
                   " WHERE CourseNumber = ?" +
                   " AND Term =" + 
                   " (" + termSubquery + ")";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setString(1, courseNumber);
      stmt.setString(2, courseNumber);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      // Check if there are any classes at all for the given course number
      if (!rs.next()) {
        System.out.println("No class found for course number:" + courseNumber);
        return;
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
    } catch (SQLException e) {
			System.err.println("Error selecting class: " + e.getMessage());
		}
  }

  /**
   * Selects a class based on the course number and term.
   * The class selected is the only section in the given term for the given course number.
   * If there are multiple sections in the given term, no class is selected and an error message is printed.
   * @param courseNumber The course number (e.g., "CS410").
   * @param term The term (e.g., "Sp20").
   */
  public void selectClassWithTerm(String courseNumber, String term) {
    String query = "SELECT ID, CourseNumber, Term, SectionNumber" +
                   " FROM Class" +
                   " WHERE CourseNumber = ?" +
                   " AND Term = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setString(1, courseNumber);
      stmt.setString(2, term);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      // Check if there are any classes at all for the given course number and term
      if (!rs.next()) {
        System.out.println("No class found for course number:" + courseNumber + " and term: " + term);
        return;
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
    } catch (SQLException e) {
			System.err.println("Error selecting class: " + e.getMessage());
		}
  }

  /**
   * Selects a class based on the course number, term, and section number.
   * @param courseNumber The course number (e.g., "CS410").
   * @param term The term (e.g., "Sp20").
   * @param sectionNumber The section number.
   */
  public void selectClassWithSection(String courseNumber, String term, int sectionNumber) {
    String query = "SELECT ID, CourseNumber, Term, SectionNumber" +
                   " FROM Class" +
                   " WHERE CourseNumber = ?" +
                   " AND Term = ?" +
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
        return;
      } else {
        // If this code is reached, there is in fact a class for the given course number, term, and section number, so we select that class
        this.currentClassID = rs.getInt("ID");
        System.out.println("The following class has been selected:");
        System.out.println(rs.getString("CourseNumber") + " " + rs.getString("Term") + " " + rs.getInt("SectionNumber"));
      }
    } catch (SQLException e) {
			System.err.println("Error selecting class: " + e.getMessage());
		}
  }

  /**
   * Shows the details of the currently selected class, if any.
   */
  public void showClass() {
    String query = "SELECT CourseNumber, Term, SectionNumber, Description" +
                   " FROM Class" +
                   " WHERE ID = ?";
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
    } catch (SQLException e) {
			System.err.println("Error showing class: " + e.getMessage());
		}
  }

  /**
   * Lists the categories for the currently active class, if any, with their weights.
   */
  public void showCategories() {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }
    String query = "SELECT Name, Weight" +
                   " FROM Category" +
                   " WHERE ClassID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setInt(1,this.currentClassID);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      boolean hasResults = false;
      System.out.println("Categories for the active class:");
      // Only enters if there are categories for the active class.
      while (rs.next()) {
        hasResults = true;
        System.out.println(rs.getString("Name") + " " + rs.getDouble("Weight"));
      }
      if (!hasResults) {
        System.out.println("No categories found.");
      }
    } catch (SQLException e) {
      System.err.println("Error showing categories: " + e.getMessage());
    }
  }

  /**
  ********* Do we want to care about keeping a classes categories weights summing to 1? ********
  * Creates a new category with the given name and weight for the currently active class.
  * @param name The name of the category (e.g., "Homework").
  * @param weight The weight of the category (e.g., 0.4).
  */
  public void addCategory(String name, double weight) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }  }

  /**
   * Lists the assignments for the currently active class, if any.
   * Grouped by category and includes assignment name and points.
   */
  public void showAssignment() {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }  }

  /**
   * Creates a new assignment with the given parameters for the currently active class.
   * @param name The name of the assignment (e.g., "Homework 1").
   * @param categoryName The name of the category this assignment belongs to (e.g., "Homework").
   * @param description The assignment description (e.g., "Chapter 1 and 2 problems").
   * @param points The number of points the assignment is worth (e.g., 100).
   */
  public void addAssignment(String name, String categoryName, String description, int points) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }  }

  public void addStudent(String username) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }  }

  public void showStudents() {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }
  }

  public void showStudentsWithSearch(String searchTerm) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }  }

  public void grade(String assignmentName, String username, int grade) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }
  }

  public String studentGrades(String username) {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public String gradebook() {
    throw new UnsupportedOperationException("Not Implemented Yet");
  }


}
