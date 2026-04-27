CREATE DATABASE grades;
USE grades;

CREATE TABLE Class (
    ID int primary key auto_increment,
    CourseNumber varchar(50) NOT NULL,
    Term varchar(4),
    SectionNumber int,
    Description text,
    -- Ensures no duplicate classes are created.
    UNIQUE (CourseNumber, Term, SectionNumber)
);

CREATE TABLE Category (
    ID int primary key auto_increment,
    Name varchar(50) NOT NULL,
    Weight double,
    ClassID int,
    foreign key (ClassID) references Class(ID) on delete cascade,
    UNIQUE (Name, ClassID)
);

CREATE TABLE Assignment (
    ID int primary key auto_increment,
    Name varchar(50) NOT NULL,
    Description text,
    PointValue int,
    CategoryID int,
    foreign key (CategoryID) references Category(ID) on delete cascade,
    UNIQUE (Name, CategoryID)
);

CREATE TABLE Student (
    StudentID int primary key UNIQUE NOT NULL,
    Username varchar(50) NOT NULL UNIQUE,
    LastName varchar(200) NOT NULL,
    FirstName varchar(200) NOT NULL
);

CREATE TABLE Enrolled (
    StudentID int,
    ClassID int,
    primary key (StudentID, ClassID),  
    foreign key (StudentID) references Student(StudentID) on delete cascade,
    foreign key (ClassID) references Class(ID) on delete cascade
);

CREATE TABLE Graded (
    StudentID int,
    AssignmentID int,
    Score int,
    primary key (StudentID, AssignmentID),
    foreign key (StudentID) references Student(StudentID) on delete cascade,
    foreign key (AssignmentID) references Assignment(ID) on delete cascade
);
