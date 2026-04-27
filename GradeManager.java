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
    // Test connection    
    if (this.connection != null && !this.connection.isClosed()) {
      System.out.println("Database connection established successfully.");
    } else {
      System.out.println("Failed to establish database connection.");
    }
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
      int rowsAffected = stmt.executeUpdate();

      // Action w/query results
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
    }  
    String query = "INSERT INTO Category" +
                   " (Name, Weight, ClassID)" + 
                   " VALUES (?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query Execution
      stmt.setString(1, name);
      stmt.setDouble(2, weight);
      stmt.setInt(3, this.currentClassID);
      int rowsAffected = stmt.executeUpdate();
      // Action w/query results
      if (rowsAffected > 0) {
        System.out.println("The following category has been successfully created:");
        System.out.println(name + " " + weight);
      } else {
        System.out.println("Failed to add category.");
      }
    } catch (SQLException e) {
			System.err.println("Error creating category: " + e.getMessage());
    }
  }

  /**
   * Lists the assignments for the currently active class, if any.
   * Grouped by category and includes assignment name and points.
   */
  public void showAssignment() {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }
    String query = "SELECT A.Name as AssignmentName, A.PointValue, C.Name as CategoryName" +
                   " FROM Assignment A JOIN Category C" +
                   " ON A.CategoryID = C.ID" +
                   " WHERE C.ClassID = ?" +
                   " ORDER BY CategoryName, AssignmentName";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setInt(1, this.currentClassID);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      boolean hasResults = false;
      System.out.println("Assignments for the active class:");
      // Only enters if there are assignments for the active class.
      while (rs.next()) {
        hasResults = true;
        System.out.println("Category: " + rs.getString("CategoryName") + " Assignment: " + rs.getString("AssignmentName") + " Points: " + rs.getInt("PointValue"));
      }
      if (!hasResults) {
        System.out.println("No assignments found.");
      }
    } catch (SQLException e) {
      System.err.println("Error showing assignments: " + e.getMessage());
    }
  }  

  /**
   * Creates a new assignment with the given parameters for the currently active class.
   * @param name The name of the assignment (e.g., "Homework 1").
   * @param description The assignment description (e.g., "Chapter 1 and 2 problems").
   * @param points The number of points the assignment is worth (e.g., 100).
   * @param categoryName The name of the category this assignment belongs to (e.g., "Homework").
   */
  public void addAssignment(String name, String categoryName, String description, int pointValue) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }  
    String categorySubquery = "SELECT ID" +
                              " FROM Category" +
                              " WHERE Name = ?" +
                              " AND ClassID = ?";
    String query = "INSERT INTO Assignment" +
                   " (Name, Description, PointValue, CategoryID)" + 
                   " VALUES (?, ?, ?, (" + categorySubquery + "))";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query Execution
      stmt.setString(1, name);
      stmt.setString(2, categoryName);
      stmt.setString(3, description);
      stmt.setInt(4, pointValue);
      stmt.setInt(5, this.currentClassID);
      int rowsAffected = stmt.executeUpdate();
      // Action w/query results
      if (rowsAffected > 0) {
        System.out.println("The following assignment has been successfully created within " + categoryName + ":");
        System.out.println(name + " " + description + " " + pointValue);
      } else {
        System.out.println("Failed to add assignment.");
      }
    } catch (SQLException e) {
      System.err.println("Error creating assignment: " + e.getMessage());
      System.out.println("Failed to add assignment. Please ensure the category exists and is spelled correctly.");
    }
  }

  /**
   * Adds a student and enrolls them in the active class.
   * If the student already exists, they are enrolled in the active class.
   * If the name provided differs from the stored name, the stored name is updated and a warning message is printed.
   * 
   * @param username The student's username (e.g., "jsmith").
   * @param StudentID The student's ID number (e.g., 12345).
   * @param lastName The student's last name (e.g., "Smith").
   * @param firstName  The student's first name (e.g., "John").
   */
  public void addStudent(String username, int studentID, String lastName, String firstName) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }  
    // Check if student already exists and whether any of the provided name info conflicts with the stored info for that student.
    String studentExistenceCheck = "SELECT StudentID, LastName, FirstName, Username" +
                                   " FROM Student" +
                                   " WHERE StudentID = ?";
    boolean existStudentNameMismatch = false;
    try (PreparedStatement stmt = connection.prepareStatement(studentExistenceCheck)) {
      stmt.setInt(1, studentID);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          if (!rs.getString("LastName").equals(lastName) || !rs.getString("FirstName").equals(firstName) || !rs.getString("Username").equals(username)) {
            existStudentNameMismatch = true;
          }
        }
      }
    } catch (SQLException e) {
      System.err.println("Error checking student existence: " + e.getMessage());
    }
    // Handle results of student existence and name conflict checks
    // Following query handles both adding a new student and updating name info if necessary.
    String studentQuery = "INSERT INTO Student" +
                  " (Username, StudentID, LastName, FirstName)" +
                  " VALUES (?, ?, ?, ?)" +
                  " ON DUPLICATE KEY UPDATE" +
                  " Username = VALUES(Username)," +
                  " LastName = VALUES(LastName)," +
                  " FirstName = VALUES(FirstName)";
    try (PreparedStatement stmt = connection.prepareStatement(studentQuery)) {
      stmt.setString(1, username);
      stmt.setInt(2, studentID);
      stmt.setString(3, lastName);
      stmt.setString(4, firstName);
      stmt.executeUpdate();
      if (existStudentNameMismatch) {
        System.out.println("Warning: The provided name information conflicts with the stored information for student ID: " + studentID + ". The stored information has been updated to match the provided information.");
      }
    } catch (SQLException e) {
      System.err.println("Error adding student: " + e.getMessage());
    }
    // Enroll student in active class, IGNORE if they are already enrolled.
    String enrollQuery = "INSERT IGNORE INTO Enrolled" +
                         " (StudentID, ClassID)" +
                         " VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(enrollQuery)) {
      stmt.setInt(1, studentID);
      stmt.setInt(2, this.currentClassID);
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Student with ID: " + studentID + " has been enrolled in the active class.");
      } else {
        System.out.println("Student with ID: " + studentID + " is already enrolled in the active class.");
      }
    } catch (SQLException e) {
      System.err.println("Error enrolling student: " + e.getMessage());
    }
  }

  /**
   * Enrolls an existing student in the active class based on their username.
   * If the student does not exist, an error message is printed.
   * If the student is already enrolled in the active class, a warning message is printed.
   * @param username The username of the student to enroll.
   */
  public void addStudent (String username) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }
    // Check if student exists & get ID if they do exist
    String studentCheckQuery = "SELECT StudentID FROM Student WHERE Username = ?";
    int studentID = -1;
    try (PreparedStatement stmt = connection.prepareStatement(studentCheckQuery)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          studentID = rs.getInt("StudentID");
        }
      }
    } catch (SQLException e) {
      System.err.println("Error checking student existence: " + e.getMessage());
    }

    if (studentID == -1) {
      System.out.println("Student with username: " + username + " does not exist.");
      return;
    }
    // Check if student is already enrolled in active class
    String enrollmentCheckQuery = "SELECT * FROM Enrolled WHERE StudentID = ? AND ClassID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(enrollmentCheckQuery)) {
      stmt.setInt(1, studentID);
      stmt.setInt(2, this.currentClassID);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          System.out.println("Student with username: " + username + " is already enrolled in the active class.");
          return;
        }
      } catch (SQLException e) {
        System.err.println("Error checking student enrollment: " + e.getMessage());
      }
    } catch (SQLException e) {
      System.err.println("Error preparing enrollment check: " + e.getMessage());
    }
    // Enroll student in active class
    String enrollQuery = "INSERT INTO Enrolled" +
                         " (StudentID, ClassID)" +
                         " VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(enrollQuery)) {
      stmt.setInt(1, studentID);
      stmt.setInt(2, this.currentClassID);
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Student with username: " + username + " has been enrolled in the active class.");
      } else {
        System.out.println("Failed to enroll student with username: " + username + " in the active class.");
      }
    } catch (SQLException e) {
      System.err.println("Error enrolling student: " + e.getMessage());
    }
  }

  /**
   * Lists the students enrolled in the active class, if any, with their username, student ID, and name.
   */
  public void showStudents() {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }
    String query = "SELECT S.Username, S.StudentID, S.FirstName, S.LastName" +
                   " FROM Student S JOIN Enrolled E" +
                   " ON S.StudentID = E.StudentID" +
                   " WHERE E.ClassID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setInt(1, this.currentClassID);
      ResultSet rs = stmt.executeQuery();
      // Action w/query results
      boolean hasResults = false;
      System.out.println("Students enrolled in the active class:");
      // Only enters if there are students enrolled in the active class.
      while (rs.next()) {
        hasResults = true;
        System.out.println(rs.getString("Username") + " " + rs.getInt("StudentID") + " " + rs.getString("FirstName") + " " + rs.getString("LastName"));
      }
      if (!hasResults) {
        System.out.println("No students found.");
      }
    } catch (SQLException e) {
      System.err.println("Error showing students: " + e.getMessage());
    }
  }

  /**
   * Lists the students enrolled in the active class that have the searchTerm contained in their username, first name, or last name.
   * @param searchTerm The term to search for in the students' username, first name, or last name. 
   */
  public void showStudentsWithSearch(String searchTerm) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }  
    String query = "SELECT S.Username, S.StudentID, S.FirstName, S.LastName" +
                   " FROM Student S JOIN Enrolled E" +
                   " ON S.StudentID = E.StudentID" +
                   " WHERE E.ClassID = ?" +
                   " AND (S.Username LIKE ? OR S.FirstName LIKE ? OR S.LastName LIKE ?)";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Query execution
      stmt.setInt(1, this.currentClassID);
      stmt.setString(2, "%" + searchTerm + "%");
      stmt.setString(3, "%" + searchTerm + "%");
      stmt.setString(4, "%" + searchTerm + "%");
      ResultSet rs = stmt.executeQuery();
      // Action with query results
      boolean hasResults = false;
      System.out.println("Students enrolled in the active class:");
      // Only enters if there are students enrolled in the active class.
      while (rs.next()) {
        hasResults = true;
        System.out.println(rs.getString("Username") + " " + rs.getInt("StudentID") + " " + rs.getString("FirstName") + " " + rs.getString("LastName"));
      }
      if (!hasResults) {
        System.out.println("No students found.");
      }
    } catch (SQLException e) {
      System.err.println("Error showing students: " + e.getMessage());
    }
  }

  /**
   * Assigns a grade according to the given parameters.
   * If the student already has a grade for the given assignment, the grade is updated to the new value.
   * If the number of points exceeds the number of points the assignment is worth, print a warning message but still update the value.
   * If the student or assignment does not exist, an error message is printed.
   * @param assignmentName The name of the assignment (e.g., "Homework 1").
   * @param username The username of the student receiving the grade (e.g., "jsmith").
   * @param grade The grade being assigned (e.g., 85).
   */
  public void grade(String assignmentName, String username, int grade) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }
  }

  public void studentGrades(String username) {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }

    String unfinishedQuery = """
      SELECT Category.Name AS CategoryName, Assignment.Name AS AssignmentName, SUM(Score) AS Score, SUM(PointValue) AS PointValue,  
	      ROUND(SUM(Score * Weight) / SUM(PointValue * Weight * (NOT ISNULL(Score))) * 100, 2) AS AttemptedPercent, 
        ROUND(SUM(Score * Weight) / SUM(PointValue * Weight) * 100, 2) AS TotalPercent
      FROM Category JOIN Assignment ON Assignment.CategoryID = Category.ID
        LEFT JOIN Graded ON Graded.AssignmentID = Assignment.ID
        LEFT JOIN Student ON Graded.StudentID = Student.StudentID
      WHERE (username = "jdoe" OR username IS NULL) AND Category.ClassID = 1
      GROUP BY Category.Name, Assignment.Name WITH ROLLUP;
    """;
    
    throw new UnsupportedOperationException("Not Implemented Yet");
  }

  public void gradebook() {
    if (this.currentClassID == -1) {
        System.out.println("No class currently selected. Please use select-class first.");
        return;
    }
    throw new UnsupportedOperationException("Not Implemented Yet");
  }


}
