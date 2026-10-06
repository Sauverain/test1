package com.dashboard.models;

public class Student {
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String classYear;
    private String phone;
    private String address;
    private String dob;
    private String gender;
    private String registrationDate;

    public Student(String studentId, String firstName, String lastName, String email, 
                   String classYear, String phone, String address, String dob, 
                   String gender, String registrationDate) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.classYear = classYear;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.registrationDate = registrationDate;
    }

    public String getStudentId() { return studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getClassYear() { return classYear; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getRegistrationDate() { return registrationDate; }
}