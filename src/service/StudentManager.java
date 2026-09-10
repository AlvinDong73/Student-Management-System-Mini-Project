package service;

import exception.DuplicateStudentException;
import exception.StudentNotFoundException;
import interfacee.Manageable;
import model.Student;

import java.io.*;
import java.util.*;

public class StudentManager implements Manageable {

    private final ArrayList<Student> students = new ArrayList<>();

    // Student ID -> Student
    private final HashMap<String, Student> studentMap = new HashMap<>();

    // Stores unique courses
    private final HashSet<String> courses = new HashSet<>();

    private final Scanner sc;

    private final String FILE_NAME = "data/students.txt";

    public StudentManager(Scanner sc) {
        this.sc = sc;
        loadFromFile();
    }

    // ================= ADD STUDENT =================

    @Override
    public void addStudent() {

        System.out.println("\n========== ADD STUDENT ==========");

        String id = readNonEmptyString("Enter Student ID: ");

        try {
            if (studentMap.containsKey(id)) {
                throw new DuplicateStudentException(
                        "Student ID already exists!"
                );
            }

            String name = readNonEmptyString("Enter Name: ");

            String course = readNonEmptyString("Enter Course: ");

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
                    course,
                    age,
                    marks
            );

            students.add(student);
            studentMap.put(id, student);
            courses.add(course);

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
                        "Student with ID " + id + " not found."
                );
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

        System.out.println("\n========== SEARCH BY COURSE ==========");

        String course = readNonEmptyString("Enter Course: ");

        boolean found = false;

        for (Student student : students) {

            if (student.getCourse().equalsIgnoreCase(course)) {

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

        String course = readNonEmptyString("Enter New Course: ");

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
        student.setCourse(course);
        student.setAge(age);
        student.setMarks(marks);

        courses.add(course);

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
                        .reversed()
        );

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
                            + student.getGrade()
            );
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
                                .reversed()
                );
                break;

            case 2:
                students.sort(
                        Comparator.comparingDouble(Student::getMarks)
                );
                break;

            case 3:
                students.sort(
                        Comparator.comparing(
                                Student::getName,
                                String.CASE_INSENSITIVE_ORDER
                        )
                );
                break;

            case 4:
                students.sort(
                        Comparator.comparing(Student::getStudentId)
                );
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

        HashMap<String, Integer> courseCount = new HashMap<>();

        for (Student student : students) {

            String course = student.getCourse();

            courseCount.put(
                    course,
                    courseCount.getOrDefault(course, 0) + 1
            );
        }

        for (Map.Entry<String, Integer> entry :
                courseCount.entrySet()) {

            System.out.println(
                    entry.getKey()
                            + " : "
                            + entry.getValue()
                            + " students"
            );
        }
    }

    // ================= SAVE FILE =================

    public synchronized void saveToFile() {

        try {

            File directory = new File("data");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter(FILE_NAME)
                    );

            for (Student student : students) {
                writer.write(student.toString());
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {

            System.out.println(
                    "Error while saving data: "
                            + e.getMessage()
            );
        }
    }

    // ================= LOAD FILE =================

    private void loadFromFile() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(file)
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|");

                if (data.length == 5) {

                    String id = data[0];
                    String name = data[1];
                    String course = data[2];
                    int age = Integer.parseInt(data[3]);
                    double marks = Double.parseDouble(data[4]);

                    Student student =
                            new Student(
                                    id,
                                    name,
                                    course,
                                    age,
                                    marks
                            );

                    students.add(student);
                    studentMap.put(id, student);
                    courses.add(course);
                }
            }

            reader.close();

            System.out.println(
                    students.size()
                            + " student records loaded."
            );

        } catch (IOException | NumberFormatException e) {

            System.out.println(
                    "Error while loading data."
            );
        }
    }

    // ================= REBUILD COURSES =================

    private void rebuildCourses() {

        courses.clear();

        for (Student student : students) {
            courses.add(student.getCourse());
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
                    "Input cannot be empty. Try again."
            );
        }
    }

    private int readInt(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        sc.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    private double readDouble(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Double.parseDouble(
                        sc.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }
}