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
        System.out.println(">> ");
        String input = shell.nextLine();
        String[] inputArgs = input.split(" ");

        switch (inputArgs[0]) {
          case "new-class":
            break;
          case "list-classes":
            break;
          case "select-class":
            break;
          case "show-class":
            break;
          case "show-categories":
            break;
          case "add-category":
            break;
          case "show-assignment":
            break;
          case "add-assignment":
            break;
          case "add-student":
            break;
          case "show-students":
            break;
          case "grade":
            break;
          case "student-grades":
            break;
          case "gradebook":
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
}
