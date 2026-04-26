import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class GradeManagerDriver {
  public static void main(String[] args) {
    try {
      GradeManager gradeManager = new GradeManager();
      Scanner shell = new Scanner(System.in);

      boolean shouldExit = false;
      while (!shouldExit) {
        System.out.println();
        System.out.print(">> ");
        String input = shell.nextLine();
        String[] inputArgs = input.split(" ");

        switch (inputArgs[0]) {
          case "new-class":
            if (inputArgs.length == 5) {
              gradeManager.newClass(inputArgs[1], inputArgs[2], Integer.parseInt(inputArgs[3]), inputArgs[4]);
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: new-class <courseNumber> <term> <section> <description>");
            }
            break;
          case "list-classes":
            if (inputArgs.length == 1) {
              gradeManager.listClasses();
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: list-classes");
            }
            break;
          case "select-class":
            if (inputArgs.length == 2) {
              gradeManager.selectClass(inputArgs[1]);
            } else if (inputArgs.length == 3) {
              gradeManager.selectClassWithTerm(inputArgs[1], inputArgs[2]);
            } else if (inputArgs.length == 4) {
              gradeManager.selectClassWithSection(inputArgs[1], inputArgs[2], Integer.parseInt(inputArgs[3]));
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: select-class <courseNumber> [term] [section]");
            }
            break;
          case "show-class":
            if (inputArgs.length == 1) {
              gradeManager.showClass();
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: show-class");
            }
            break;
          case "show-categories":
            if (inputArgs.length == 1) {
              gradeManager.showCategories();
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: show-categories");
            }
            break;
          case "add-category":
            if (inputArgs.length == 3) {
              gradeManager.addCategory(inputArgs[1], Double.parseDouble(inputArgs[2]));
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: add-category <name> <weight>");
            }
            break;
          case "show-assignment":
            if (inputArgs.length == 1) {
              gradeManager.showAssignment();
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: show-assignment");
            }
            break;
          case "add-assignment":
            if (inputArgs.length == 5) {
              gradeManager.addAssignment(inputArgs[1], inputArgs[2], inputArgs[3], Integer.parseInt(inputArgs[4]));
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: add-assignment <name> <category> <description> <points>");
            }
            break;
          case "add-student":
            if (inputArgs.length == 5) {
              gradeManager.addStudent(inputArgs[1], Integer.parseInt(inputArgs[2]), inputArgs[3], inputArgs[4]);
            } else if (inputArgs.length == 2) {
              gradeManager.addStudent(inputArgs[1]);
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: add-student <username> <studentID> <lastName> <firstName>");
            }
            break;
          case "show-students":
            if (inputArgs.length == 1) {
              gradeManager.showStudents();
            } else if (inputArgs.length == 2) {
              gradeManager.showStudentsWithSearch(inputArgs[1]);
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: show-students");
            }
            break;
          case "grade":
            if (inputArgs.length == 4) {
              gradeManager.grade(inputArgs[1], inputArgs[2], Integer.parseInt(inputArgs[3]));
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: grade <assignmentName> <username> <grade>");
            }
            break;
          case "student-grades":
            if (inputArgs.length == 2) {
              gradeManager.studentGrades(inputArgs[1]);
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: student-grades <username>");
            }
            break;
          case "gradebook":
            if (inputArgs.length == 1) {
              gradeManager.gradebook();
            } else {
              printWrongNumArgs(inputArgs[0], inputArgs.length - 1);
              System.out.println("Usage: gradebook");
            }
            break;
          case "exit":
            shouldExit = true;
            break;
          case "help":
            System.out.println("""
                available commands:
                  new-class <courseNumber> <term> <section> <description>
                  list-classes
                  select-class <courseNumber> [term] [section]
                  show-class

                  show-categories
                  add-category <name> <weight>
                  show-assignment
                  add-assignment <name> <category> <description> <points>

                  add-student <username> [studentID] [lastName] [firstName]
                  show-students [searchTerm]
                  grade <assignmentName> <username> <grade>

                  student-grades <username>
                  gradebook

                  help
                  exit
                """);
            break;
          default:
            System.out.println("command " + inputArgs[0] + " not found. Try again or type help for list of commands");
            break;
        }
      }
    } catch (SQLException | IOException e) {
      System.out.println(e.getMessage());
    }

    // We'll prolly want a few arguement checks but one that I thought of while
    // implementing selectClass() is to ensure that the term is properly inputted to
    // the specifications given in the project documentation.
    // For example, if the user inputs "Fall2024" instead of "Fa24", the method
    // won't
    // output the most recent term properly.
  }

  public static void printWrongNumArgs(String commandName, int numArgs) {
    System.out.println("wrong number of arguments for " + commandName + ", got " + numArgs);
  }
}
