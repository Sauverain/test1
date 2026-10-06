package com.dashboard.models;

public class StudentReportModel {
    private String studentId;
    private String fullName;
    private String classYear;
    private String java;
    private String dbms;
    private String web;
    private String ent;
    private String lang;
    private String average;
    private String grade;

    public StudentReportModel(String studentId, String fullName, String classYear, String java, 
                              String dbms, String web, String ent, String lang, String average, String grade) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.classYear = classYear;
        this.java = java;
        this.dbms = dbms;
        this.web = web;
        this.ent = ent;
        this.lang = lang;
        this.average = average;
        this.grade = grade;
    }

    // Explicit Getters for all variables needed by PropertyValueFactory
    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getClassYear() { return classYear; }
    public String getJava() { return java; }
    public String getDbms() { return dbms; }
    public String getWeb() { return web; }
    public String getEnt() { return ent; }
    public String getLang() { return lang; }
    public String getAverage() { return average; }
    public String getGrade() { return grade; }
}
