package service;

import exception.DuplicateStudentException;
import exception.StudentNotFoundException;
import interfacee.Manageable;
import model.Student;
import model.Course;

import java.io.*;
import java.util.*;

public class StudentManager implements Manageable {

    private final ArrayList<Student> students = new ArrayList<>();

    // Student ID -> Student
    private final HashMap<String, Student> studentMap = new HashMap<>();

    // Stores unique courses
    private final HashMap<Integer, Course> courses = new HashMap<>();

    private final Scanner sc;

    private final String STUDENTS_FILE_NAME = "data/students.txt";
    private final String COURSES_FILE_NAME = "data/courses.txt";

    public StudentManager(Scanner sc) {
        this.sc = sc;
        loadFromFile();
    }

    // ================= ADD STUDENT =================

    @Override
    public void addStudent() {

        if (courses.isEmpty()) {
            System.out.println("No available courses to assign a new student to.");
            return;
        }

        System.out.println("\n========== ADD STUDENT ==========");

        String id = readNonEmptyString("Enter Student ID: ");

        try {

            if (studentMap.containsKey(id)) {
                throw new DuplicateStudentException(
                        "Student ID already exists!");
            }

            String name = readNonEmptyString("Enter Name: ");

            int course = -1;
            System.out.println("\nCurrent Courses:");
            while (true) {
                for (Course c : courses.values()) {
                    System.out.println(c.getBriefDetails());
                }
                course = readInt("Enter Course CRN: ");
                if (courses.containsKey(course)) {
                    break;
                }
            }

            int age = readInt("Enter Age: ");

            if (age < 5 || age > 100) {
                System.out.println("Age must be between 5 and 100.");
                return;
            }

            double marks = readDouble("Enter Marks: ");

            if (marks < 0 || marks > 100) {
                System.out.println("Marks must be between 0 and 100.");
                return;
            }

            Student student = new Student(
                    id,
                    name,
                    courses.get(course),
                    age,
                    marks);

            students.add(student);
            studentMap.put(id, student);

            courses.get(course).addStudent(student);

            System.out.println("\nStudent added successfully!");
            System.out.println("Grade  : " + student.getGrade());
            System.out.println("Status : " + student.getStatus());

            saveToFile();

        } catch (DuplicateStudentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ================= VIEW STUDENTS =================

    @Override
    public void viewStudents() {

        System.out.println("\n========== ALL STUDENTS ==========");

        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        for (Student student : students) {
            student.displayDetails();
        }

        System.out.println("Total Students: " + students.size());
    }

    // ================= SEARCH BY ID =================

    public void searchById() {

        System.out.println("\n========== SEARCH BY ID ==========");

        String id = readNonEmptyString("Enter Student ID: ");

        try {
            Student student = studentMap.get(id);

            if (student == null) {
                throw new StudentNotFoundException(
                        "Student with ID " + id + " not found.");
            }

            student.displayDetails();

        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ================= SEARCH BY NAME =================

    public void searchByName() {

        System.out.println("\n========== SEARCH BY NAME ==========");

        String name = readNonEmptyString("Enter Name: ");

        boolean found = false;

        for (Student student : students) {

            if (student.getName().toLowerCase()
                    .contains(name.toLowerCase())) {

                student.displayDetails();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No student found.");
        }
    }

    // ================= SEARCH BY COURSE =================

    public void searchByCourse() {

        if (courses.isEmpty()) {
            System.out.println("No available courses to search by.");
            return;
        }

        System.out.println("\n========== SEARCH BY COURSE ==========");

        int course = -1;
        while (true) {
            for (Course c : courses.values()) {
                System.out.println(c.getBriefDetails());
            }
            course = readInt("Enter Course CRN to search by: ");
            if (courses.containsKey(course)) {
                break;
            }
        }

        boolean found = false;

        for (Student student : students) {

            if (student.getCourse().getCrn() == course) {

                student.displayDetails();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No student found for this course.");
        }
    }

    // ================= UPDATE =================

    @Override
    public void updateStudent() {

        if (courses.isEmpty()) {
            System.out.println("No available courses to assign a student to.");
            return;
        }

        System.out.println("\n========== UPDATE STUDENT ==========");

        String id = readNonEmptyString("Enter Student ID: ");

        Student student = studentMap.get(id);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("\nCurrent Details:");
        student.displayDetails();

        String name = readNonEmptyString("Enter New Name: ");

        int course = -1;
        while (true) {
            for (Course c : courses.values()) {
                System.out.println(c.getBriefDetails());
            }
            course = readInt("Enter Course CRN to assign the student to: ");
            if (courses.containsKey(course)) {
                break;
            }
        }

        int age = readInt("Enter New Age: ");

        if (age < 5 || age > 100) {
            System.out.println("Invalid age.");
            return;
        }

        double marks = readDouble("Enter New Marks: ");

        if (marks < 0 || marks > 100) {
            System.out.println("Invalid marks.");
            return;
        }

        student.setName(name);
        Course oldCourse = student.getCourse();
        if (oldCourse != null) {
            oldCourse.removeStudent(student);
        }
        courses.get(course).addStudent(student);
        student.setCourse(courses.get(course));
        student.setAge(age);
        student.setMarks(marks);

        System.out.println("\nStudent updated successfully!");

        saveToFile();
    }

    // ================= DELETE =================

    @Override
    public void deleteStudent() {

        System.out.println("\n========== DELETE STUDENT ==========");

        String id = readNonEmptyString("Enter Student ID: ");

        Student student = studentMap.get(id);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        student.displayDetails();

        System.out.print("Are you sure you want to delete? (Y/N): ");

        String choice = sc.nextLine();

        if (choice.equalsIgnoreCase("Y")) {

            students.remove(student);
            studentMap.remove(id);

            rebuildCourses();

            System.out.println("Student deleted successfully!");

            saveToFile();

        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    // ================= STATISTICS =================

    public void displayStatistics() {

        System.out.println("\n========== STUDENT STATISTICS ==========");

        if (students.isEmpty()) {
            System.out.println("No student records available.");
            return;
        }

        double total = 0;
        double highest = students.get(0).getMarks();
        double lowest = students.get(0).getMarks();

        int passed = 0;
        int failed = 0;

        for (Student student : students) {

            double marks = student.getMarks();

            total += marks;

            if (marks > highest) {
                highest = marks;
            }

            if (marks < lowest) {
                lowest = marks;
            }

            if (marks >= 40) {
                passed++;
            } else {
                failed++;
            }
        }

        double average = total / students.size();

        System.out.println("Total Students : " + students.size());
        System.out.printf("Average Marks  : %.2f%n", average);
        System.out.printf("Highest Marks  : %.2f%n", highest);
        System.out.printf("Lowest Marks   : %.2f%n", lowest);
        System.out.println("Passed Students: " + passed);
        System.out.println("Failed Students: " + failed);
        System.out.println("Unique Courses : " + courses.size());
    }

    // ================= TOP STUDENTS =================

    public void displayTopStudents() {

        System.out.println("\n========== TOP PERFORMERS ==========");

        if (students.isEmpty()) {
            System.out.println("No records available.");
            return;
        }

        ArrayList<Student> sorted = new ArrayList<>(students);

        sorted.sort(
                Comparator.comparingDouble(Student::getMarks)
                        .reversed());

        int count = Math.min(5, sorted.size());

        for (int i = 0; i < count; i++) {

            Student student = sorted.get(i);

            System.out.println(
                    (i + 1) + ". "
                            + student.getName()
                            + " | "
                            + student.getStudentId()
                            + " | Marks: "
                            + student.getMarks()
                            + " | Grade: "
                            + student.getGrade());
        }
    }

    // ================= SORT =================

    public void sortStudents() {

        System.out.println("\n========== SORT STUDENTS ==========");

        if (students.isEmpty()) {
            System.out.println("No records available.");
            return;
        }

        System.out.println("1. Marks - Highest to Lowest");
        System.out.println("2. Marks - Lowest to Highest");
        System.out.println("3. Name - A to Z");
        System.out.println("4. Student ID");

        int choice = readInt("Enter choice: ");

        switch (choice) {

            case 1:
                students.sort(
                        Comparator.comparingDouble(Student::getMarks)
                                .reversed());
                break;

            case 2:
                students.sort(
                        Comparator.comparingDouble(Student::getMarks));
                break;

            case 3:
                students.sort(
                        Comparator.comparing(
                                Student::getName,
                                String.CASE_INSENSITIVE_ORDER));
                break;

            case 4:
                students.sort(
                        Comparator.comparing(Student::getStudentId));
                break;

            default:
                System.out.println("Invalid choice.");
                return;
        }

        System.out.println("Students sorted successfully.");

        viewStudents();
    }

    // ================= COURSE STATISTICS =================

    public void courseStatistics() {

        System.out.println("\n========== COURSE STATISTICS ==========");

        if (students.isEmpty()) {
            System.out.println("No records available.");
            return;
        }

        HashMap<Course, Integer> courseCount = new HashMap<>();

        for (Student student : students) {

            Course course = student.getCourse();

            courseCount.put(
                    course,
                    courseCount.getOrDefault(course, 0) + 1);
        }

        for (Map.Entry<Course, Integer> entry : courseCount.entrySet()) {

            System.out.println(
                    entry.getKey().getBriefDetails()
                            + " : "
                            + entry.getValue()
                            + " students");
        }
    }

    // ================= SAVE FILE =================

    public synchronized void saveToFile() {

        try {

            File directory = new File("data");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            BufferedWriter studentsWriter = new BufferedWriter(
                    new FileWriter(STUDENTS_FILE_NAME));

            BufferedWriter coursesWriter = new BufferedWriter(
                    new FileWriter(COURSES_FILE_NAME));

            for (Student student : students) {
                studentsWriter.write(student.toString());
                studentsWriter.newLine();
            }

            for (Course course : courses.values()) {
                coursesWriter.write(course.toString());
                coursesWriter.newLine();
            }

            studentsWriter.close();
            coursesWriter.close();

        } catch (IOException e) {

            System.out.println(
                    "Error while saving data: "
                            + e.getMessage());
        }
    }

    // ================= LOAD FILE =================

    private void loadFromFile() {

        File studentsFile = new File(STUDENTS_FILE_NAME);
        File coursesFile = new File(COURSES_FILE_NAME);

        if (!studentsFile.exists() || !coursesFile.exists()) {
            return;
        }

        try {

            BufferedReader studentsReader = new BufferedReader(
                    new FileReader(studentsFile));

            BufferedReader coursesReader = new BufferedReader(
                    new FileReader(coursesFile));

            String line;

            while ((line = coursesReader.readLine()) != null) {

                String[] data = line.split("\\|");

                System.out.println(data.length);

                if (data.length == 5) {
                    Course course = new Course(data);

                    courses.put(course.getCrn(), course);
                }

                System.out.println("Course added");
            }

            while ((line = studentsReader.readLine()) != null) {

                String[] data = line.split("\\|");

                if (data.length == 5) {

                    String id = data[0];
                    String name = data[1];
                    int course = Integer.parseInt(data[2]);
                    int age = Integer.parseInt(data[3]);
                    double marks = Double.parseDouble(data[4]);

                    Student student = new Student(
                            id,
                            name,
                            // If the student was saved with no course
                            (course != -1 ? courses.get(course) : null),
                            age,
                            marks);

                    students.add(student);
                    studentMap.put(id, student);

                    Course currentCourse = student.getCourse();
                    if (currentCourse != null) {
                        currentCourse.addStudent(student);
                    }
                }
            }

            studentsReader.close();
            coursesReader.close();

            System.out.println(
                    students.size()
                            + " student records loaded.");

        } catch (IOException | NumberFormatException e) {

            System.out.println(
                    "Error while loading data.");
        }
    }

    // ================= REBUILD COURSES =================

    private void rebuildCourses() {

        courses.clear();

        for (Student student : students) {
            Course course = student.getCourse();
            if (course != null) {
                courses.put(course.getCrn(), course);
            }
        }
    }

    // ================= INPUT METHODS =================

    private String readNonEmptyString(String message) {

        while (true) {

            System.out.print(message);

            String input = sc.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println(
                    "Input cannot be empty. Try again.");
        }
    }

    private int readInt(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        sc.nextLine().trim());

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number.");
            }
        }
    }

    private double readDouble(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Double.parseDouble(
                        sc.nextLine().trim());

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number.");
            }
        }
    }

    public void addCourse() {
        System.out.println("\n========== ADD COURSE ==========");

        int crn = readInt("Enter CRN: ");

        if (courses.containsKey(crn)) {
            System.out.println("A course with this CRN already exists.");
            return;
        }

        if (crn <= 0) {
            System.out.println("A course's CRN must be positive.");
            return;
        }

        String subject = readNonEmptyString("Enter Course Subject: ");

        String name = readNonEmptyString("Enter Course Name: ");

        int num = readInt("Enter Course Number: ");

        if (num <= 0) {
            System.out.println("A course's number must be positive.");
            return;
        }

        String professor = readNonEmptyString("Enter Professor Name: ");

        Course course = new Course(subject, name, crn, num, professor);

        courses.put(crn, course);

        System.out.println("\nCourse added successfully!");

        saveToFile();
    }

    public void viewCourses() {

        System.out.println("\n========== ALL COURSES ==========");

        if (courses.isEmpty()) {
            System.out.println("No courses exist yet.");
            return;
        }

        for (Course course : courses.values()) {
            course.displayFullDetails();
        }
    }

    public void deleteCourse() {
        System.out.println("\n========== DELETE COURSE ==========");

        int crn = readInt("Enter Course CRN: ");

        Course course = courses.get(crn);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        course.displayFullDetails();

        System.out.print("Are you sure you want to delete? (Y/N): ");

        String choice = sc.nextLine();

        if (choice.equalsIgnoreCase("Y")) {

            for (Student student : students) {
                if (student.getCourse().equals(course)) {
                    student.setCourse(null);
                }
            }

            courses.remove(crn);

            System.out.println("Course deleted successfully!");

            saveToFile();

        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    public void updateCourse() {
        System.out.println("\n========== UPDATE STUDENT ==========");

        int crn = readInt("Enter Course CRN: ");

        Course course = courses.get(crn);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        System.out.println("\nCurrent Details:");
        course.displayFullDetails();

        String prof = readNonEmptyString("Enter New Professor: ");
        String name = readNonEmptyString("Enter New Course Name:");

        course.setProfessor(prof);
        course.setName(name);

        System.out.println("\nCourse updated successfully!");

        saveToFile();
    }
}