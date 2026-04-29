# CS410-Final-Project
* Author: Caden Anderson & Vivian Lauer
* Class: CS410
* Semester: Spring 2026

# Overview
- Implementation of a Java application for managing grades in a class or multiple classes using a command line interface.
- It interacts with a MySQL database to track classes, their categories and assignments, students enrollments and student grades.

## Functionality
- The user can create classes, categories for assignments, assignments, students, enroll students in classes and assign grades for a student's assignment.
- The user can then retrieve all info that has been created whether by them or anyone else using the application.

## Details
- DatabaseConnection handles and configures the connection to the desired database. The connection is established as part of the
  contstructor within GradeManager and the connection is later closed within GradeManagerDriver.
- GradeManager handles all functionality of the database management.
- GradeManagerDriver handles all command line inputs and interacts with GradeManager to output the specified functionality.

# Compiling and Using
In order to use the application users must meet the following requirements:

- Java Development Kit (JDK) 17+
- MySQL Connector/J 9.4.0 (installed on Onyx at /opt/mysql/)
- MySQL Sandbox environment configured on the Onyx server.

## Steps to use the Application
1. Initialize the database: Assuming the MySQL sandbox is running and the user is within the sandbox directory load the dummy data using the following:
  ```./use grades < dump.sql```
2. On the Onyx server, set your classpath and compile the Java files by executing the following:
  ```export CLASSPATH=/opt/mysql/mysql-connector-j-9.4.0.jar:.:$CLASSPATH```
  ```javac *.java```
3. In order to run the Driver class execute the following:
  ```java GradeManagerDriver```

# Reflection
## Usage of AI
When the project was first assigned, as a team, we developed an overall implementation plan and schedule that ended up having little to no signifcant changes
from start to finish. AI assisted in executed our plan in the following ways:
- Inline comments from Copilot sped up the process for many redundant lines of code.
  Example: Once it recognized the pattern in which we implemented showCategories it suggested similar code within a method like showAssignment which
  allowed for a quicker implementation process.
- Once the methods we were required to implement fell a bit outside the scope of the few examples we were provivded, Gemini was consulted to further understand the
  best functions and syntax in order to implement our plan.
  Example: Consulted on further explanation of ResultSets and their next functions. The SQL syntax "ON DUPLICATE KEY UPDATE" to handle duplicates smoothly.
## Lessons Learned
- We learned that you can’t empty a table that hasn't been created yet. We had to fix the SQL dump file to ensure CREATE TABLE happened before TRUNCATE,
  otherwise, the entire setup script would crash.
- We discovered that a successful "Database connection established" message in Java doesn't mean the data is there. Understanding the
  distinction between the database system and the schema/data was a key conceptual shift. Discovering that the execution of "./use grades < dump.sql"
  within the MySQL sandbox took some time and research but once that workflow of navigating between the sandbox and java files was clear it all came together quickly.

