
package model;

public class Student extends Person {

    private String studentId;
    private String course;
    private double marks;

    public Student(String studentId, String name, String course, int age, double marks) {
        super(name, age);
        this.studentId = studentId;
        this.course = course;
        this.marks = marks;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public String getGrade() {
        if (marks >= 90) {
            return "A+";
        } else if (marks >= 80) {
            return "A";
        } else if (marks >= 70) {
            return "B";
        } else if (marks >= 60) {
            return "C";
        } else if (marks >= 50) {
            return "D";
        } else {
            return "F";
        }
    }

    public String getStatus() {
        return marks >= 40 ? "PASS" : "FAIL";
    }

    @Override
    public void displayDetails() {
        System.out.println("-----------------------------------------------");
        System.out.println("Student ID : " + studentId);
        System.out.println("Name       : " + getName());
        System.out.println("Course     : " + course);
        System.out.println("Age        : " + getAge());
        System.out.println("Marks      : " + marks);
        System.out.println("Grade      : " + getGrade());
        System.out.println("Status     : " + getStatus());
        System.out.println("-----------------------------------------------");
    }

    @Override
    public String toString() {
        return studentId + "|" + getName() + "|" + course + "|" + getAge() + "|" + marks;
    }
}