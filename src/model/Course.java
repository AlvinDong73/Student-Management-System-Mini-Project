package model;

import java.util.ArrayList;

public class Course {
    private String subject;
    private String name;
    private int crn;
    private int courseNumber;
    private String professor;
    private ArrayList<Student> students;

    // constructs from columns in save data
    public Course(String[] data_cols) {
        subject = data_cols[0];
        name = data_cols[1];
        crn = Integer.parseInt(data_cols[2]);
        courseNumber = Integer.parseInt(data_cols[3]);
        professor = data_cols[4];
        students = new ArrayList<>();
    }

    // constructs from user input
    public Course(String subject, String name, int crn, int courseNumber, String professor) {
        this.subject = subject;
        this.name = name;
        this.crn = crn;
        this.courseNumber = courseNumber;
        this.professor = professor;
        students = new ArrayList<>();
    }

    public String getBriefDetails() {
        return String.format("%s %d (CRN: %d)", subject, courseNumber, crn);
    }

    public String getFullCourseNumber() {
        return subject + " " + courseNumber;
    }

    public void displayFullDetails() {
        System.out.println("-----------------------------------------------");
        System.out.println("Name       : " + name);
        System.out.println("Course #   : " + subject + " " + courseNumber);
        System.out.println("CRN        : " + crn);
        System.out.println("Professor  : " + professor);
        System.out.println("Students   :");
        if (students.isEmpty()) {
            System.out.println(" (No students in class)");
        } else {
            for (Student student : students) {
                System.out.println(" * " + student.getName());
            }
        }
        System.out.println("-----------------------------------------------");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public int getCrn() {
        return crn;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public String getSubject() {
        return subject;
    }

    public void addStudent(Student s) {
        students.add(s);
    }

    public void removeStudent(Student s) {
        students.remove(s);
    }

    // Courses are equal if their crns are identical
    public boolean equals(Object other) {
        return other instanceof Course && ((Course) other).getCrn() == crn;
    }

    @Override
    public String toString() {
        String finalString = subject;
        finalString += "|" + name;
        finalString += "|" + crn;
        finalString += "|" + courseNumber;
        finalString += "|" + professor + "|";
        for (Student student : students) {
            finalString += student.getStudentId() + ",";
        }
        return finalString.substring(0, finalString.length() - 1);
    }
}
