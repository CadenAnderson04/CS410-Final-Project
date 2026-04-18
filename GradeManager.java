public class GradeManager {
  private int currentClassID;

  public GradeManager() {
    currentClassID = -1;
  }

  public void newClass(String courseNumber, String term, int sectionNumber, String description) {
    throw new UnsupportedOperationException("Not Implemented Yet");
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
