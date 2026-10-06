package com.dashboard.models;

public class StudentExtended {
    private String studentId;
    private String firstName;
    private String lastName;
    private String classYear;
    private String phone;
    private String averageScore;
    private String finalGrade;

    public StudentExtended(String studentId, String firstName, String lastName, String classYear, String phone, String averageScore, String finalGrade) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.classYear = classYear;
        this.phone = phone;
        this.averageScore = averageScore;
        this.finalGrade = finalGrade;
    }

    public String getStudentId() { return studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getClassYear() { return classYear; }
    public String getPhone() { return phone; }
    public String getAverageScore() { return averageScore; }
    public String getFinalGrade() { return finalGrade; }
}
