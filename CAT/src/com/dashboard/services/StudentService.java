package com.dashboard.services;

import com.dashboard.models.Student;
import com.dashboard.models.StudentReportModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.Year;

public class StudentService {
    
    private static final String DB_URL = "jdbc:mysql://10.240.97.36:3306/school";
    private static final String DB_USER = "cat";
    private static final String DB_PASS = "Kigali@1234";

    private static String currentStoredOtp = null;
    private static String currentResetTargetEmail = null;

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                      "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                      "full_name VARCHAR(150) NOT NULL, " + 
                                      "email VARCHAR(255) UNIQUE NOT NULL, " +
                                      "password VARCHAR(255) NOT NULL" +
                                      ");";
            stmt.execute(createUsersTable);

            String checkEmpty = "SELECT COUNT(*) FROM users";
            try (ResultSet rs = stmt.executeQuery(checkEmpty)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO users (full_name, email, password) VALUES ('System Administrator', 'admin@system.com', 'admin123')");
                }
            }

            String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                                         "student_id VARCHAR(20) PRIMARY KEY, " +
                                         "first_name VARCHAR(100) NOT NULL, " +
                                         "last_name VARCHAR(100), " +
                                         "email VARCHAR(255) UNIQUE NOT NULL, " +
                                         "class_year VARCHAR(50) NOT NULL, " +
                                         "address TEXT NOT NULL, " +
                                         "dob VARCHAR(50) NOT NULL, " +
                                         "gender VARCHAR(20) NOT NULL, " +
                                         "phone VARCHAR(30) NOT NULL, " +
                                         "registration_date VARCHAR(50) NOT NULL, " + 
                                         "registered_by VARCHAR(255) NOT NULL, " +
                                         "status INT DEFAULT 1 NOT NULL" + 
                                         ");";
            stmt.execute(createStudentsTable);

            String createMarksTable = "CREATE TABLE IF NOT EXISTS marks (" +
                                      "marks_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                      "student_id VARCHAR(20) NOT NULL, " +
                                      "java_prog DOUBLE NOT NULL, " +
                                      "db_ms DOUBLE NOT NULL, " +
                                      "web_design DOUBLE NOT NULL, " +
                                      "entrepreneurship DOUBLE NOT NULL, " +
                                      "language DOUBLE NOT NULL, " +
                                      "average_score DOUBLE NOT NULL, " +
                                      "final_grade VARCHAR(5) NOT NULL, " +
                                      "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE" +
                                      ");";
            stmt.execute(createMarksTable);
            System.out.println("Database initialization verified: System tables ok.");
            
        } catch (SQLException e) {
            System.err.println("XAMPP MySQL Initialization Error: " + e.getMessage());
        }
    }

    public static boolean checkEmailExists(String email) {
        String sql = "SELECT id FROM users WHERE LOWER(email) = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            return false;
        }
    }

    public static void setTemporaryRecoveryToken(String email, String otp) {
        currentResetTargetEmail = email;
        currentStoredOtp = otp;
    }

    public static boolean verifyOtpAndChangePassword(String email, String otp, String newPassword) {
        if (currentResetTargetEmail == null || currentStoredOtp == null || 
            !currentResetTargetEmail.equalsIgnoreCase(email) || !currentStoredOtp.equals(otp)) {
            return false;
        }
        String sql = "UPDATE users SET password = ? WHERE LOWER(email) = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email.trim().toLowerCase());
            boolean ok = pstmt.executeUpdate() > 0;
            if (ok) {
                currentStoredOtp = null;
                currentResetTargetEmail = null;
            }
            return ok;
        } catch (SQLException ex) {
            return false;
        }
    }

    public static boolean verifyCurrentPassword(String email, String password) {
        String sql = "SELECT id FROM users WHERE LOWER(email) = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.trim().toLowerCase());
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            return false;
        }
    }

    public static int getTotalStudentCount() {
        String sql = "SELECT COUNT(*) FROM students WHERE status = 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {}
        return 0;
    }

    public static double getOverallPerformanceAverage() {
        String sql = "SELECT AVG(average_score) FROM marks";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException ex) {}
        return 0.0;
    }

    public static int getGradeCount(String grade) {
        String sql = "SELECT COUNT(*) FROM marks WHERE final_grade = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, grade);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {}
        return 0;
    }
        public static ObservableList<Student> getAllStudents() {
        String sql = "SELECT * FROM students WHERE status = 1";
        ObservableList<Student> list = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // FIXED ORDER: Matches the internal variable order of your Student model constructor precisely
                list.add(new Student(
                    rs.getString("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("class_year"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("dob"),
                    rs.getString("gender"),
                    rs.getString("registration_date")
                ));
            }
        } catch (SQLException ex) {
            System.err.println("Error pulling student master list: " + ex.getMessage());
        }
        return list;
    }
    public static ObservableList<StudentReportModel> getStudentsWithMarks() {
        ObservableList<StudentReportModel> list = FXCollections.observableArrayList();
        String sql = "SELECT s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS full_name, s.class_year, "
                   + "m.java_prog, m.db_ms, m.web_design, m.entrepreneurship, m.language, m.average_score, m.final_grade "
                   + "FROM students s INNER JOIN marks m ON s.student_id = m.student_id WHERE s.status = 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new StudentReportModel(
                    rs.getString("student_id"),
                    rs.getString("full_name"),
                    rs.getString("class_year"),
                    String.valueOf(rs.getDouble("java_prog")),
                    String.valueOf(rs.getDouble("db_ms")),
                    String.valueOf(rs.getDouble("web_design")),
                    String.valueOf(rs.getDouble("entrepreneurship")),
                    String.valueOf(rs.getDouble("language")),
                    String.valueOf(rs.getDouble("average_score")),
                    rs.getString("final_grade")
                ));
            }
            } catch (SQLException ex) {
    System.err.println("Error pulling report data: " + ex.getMessage());
}
return list;
}

public static ObservableList getStudentsWithMarksCombinedWithUnmarked() {
    ObservableList list = FXCollections.observableArrayList();
    String sql = "SELECT s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS full_name, s.class_year, m.average_score, m.final_grade, "
            + "IFNULL(m.java_prog, 0.0) as j, IFNULL(m.db_ms, 0.0) as d, IFNULL(m.web_design, 0.0) as w, IFNULL(m.entrepreneurship, 0.0) as e, IFNULL(m.language, 0.0) as l "
            + "FROM students s LEFT JOIN marks m ON s.student_id = m.student_id WHERE s.status = 1";
            
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
         
        while (rs.next()) {
            double avgValue = rs.getDouble("average_score");
            String avgStr = rs.wasNull() || avgValue == 0.0 ? "0.0" : String.valueOf(avgValue);
            String gradeStr = rs.getString("final_grade") == null ? "Unmarked" : rs.getString("final_grade");
            
            list.add(new StudentReportModel(
                rs.getString("student_id"),
                rs.getString("full_name"),
                rs.getString("class_year"),
                String.valueOf(rs.getDouble("j")),
                String.valueOf(rs.getDouble("d")),
                String.valueOf(rs.getDouble("w")),
                String.valueOf(rs.getDouble("e")),
                String.valueOf(rs.getDouble("l")),
                avgStr,
                gradeStr
            ));
        }
    } catch (SQLException ex) {
        System.err.println("Combined data processing error: " + ex.getMessage());
    }
    return list;
}

public static String getStudentNameById(String studentId) {
    String sql = "SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM students WHERE student_id = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, studentId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getString("full_name");
        }
    } catch (SQLException ex) {
        // Intentionally blank per original code
    }
    return "Unknown Student";
}

public static boolean registerAccount(String fullName, String email, String password) {
    String sql = "INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, fullName);
        pstmt.setString(2, email);
        pstmt.setString(3, password);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException ex) {
        return false;
    }
}

public static String validateLoginAndGetFullName(String email, String password) {
    String sql = "SELECT full_name FROM users WHERE LOWER(email) = ? AND password = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, email.trim().toLowerCase());
        pstmt.setString(2, password);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getString("full_name");
        }
    } catch (SQLException ex) {
        // Intentionally blank per original code
    }
    return null;
}
public static String generateNextStudentId(int intake) {
    String yearPrefix = String.valueOf(Year.now().getValue()).substring(2);
    String intakePrefix = String.format("%02d", intake);
    String basePrefix = yearPrefix + intakePrefix;
    String sql = "SELECT student_id FROM students WHERE student_id LIKE ? ORDER BY student_id DESC LIMIT 1";
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, basePrefix + "%");
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("student_id");
                int lastSequence = Integer.parseInt(lastId.substring(4));
                int nextSequence = lastSequence + 1;
                return basePrefix + String.format("%03d", nextSequence);
            }
        }
    } catch (SQLException | NumberFormatException e) {
        System.err.println("Error calculating next Student ID sequence: " + e.getMessage());
    }
    return basePrefix + "001";
}

public static boolean registerStudentInDb(String studentId, String fName, String lName, String email,
                                          String classYear, String address, String dob, String gender,
                                          String phone, String regDate, String registeredBy) {
                                          
    String sql = "INSERT INTO students (student_id, first_name, last_name, email, class_year, address, dob, gender, phone, registration_date, registered_by, status) " 
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
            
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, studentId);
        pstmt.setString(2, fName);
        pstmt.setString(3, lName);
        pstmt.setString(4, email);
        pstmt.setString(5, classYear);
        pstmt.setString(6, address);
        pstmt.setString(7, dob);
        pstmt.setString(8, gender);
        pstmt.setString(9, phone);
        pstmt.setString(10, regDate);
        pstmt.setString(11, registeredBy);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("MySQL Student Insert Error: " + e.getMessage());
        return false;
    }
}

public static boolean modifyStudentProfile(String studentId, String fName, String lName, String email, 
                                          String classYear, String address, String dob, String gender, String phone) {
                                          
    String sql = "UPDATE students SET first_name = ?, last_name = ?, email = ?, class_year = ?, address = ?, dob = ?, gender = ?, phone = ? WHERE student_id = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, fName);
        pstmt.setString(2, lName);
        pstmt.setString(3, email);
        pstmt.setString(4, classYear);
        pstmt.setString(5, address);
        pstmt.setString(6, dob);
        pstmt.setString(7, gender);
        pstmt.setString(8, phone);
        pstmt.setString(9, studentId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Student database profile update error: " + e.getMessage());
        return false;
    }
}

public static boolean hasStudentBeenMarked(String studentId) {
    String sql = "SELECT student_id FROM marks WHERE student_id = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, studentId);
        try (ResultSet rs = pstmt.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException ex) {
        return false;
    }
}

public static boolean saveStudentMarks(String studentId, double java, double dbms, double web, double ent, double lang, double avg, String grade) {
    String sql = "INSERT INTO marks (student_id, java_prog, db_ms, web_design, entrepreneurship, language, average_score, final_grade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, studentId);
        pstmt.setDouble(2, java);
        pstmt.setDouble(3, dbms);
        pstmt.setDouble(4, web);
        pstmt.setDouble(5, ent);
        pstmt.setDouble(6, lang);
        pstmt.setDouble(7, avg);
        pstmt.setString(8, grade);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException ex) {
        System.err.println("MySQL Marks Insert Error: " + ex.getMessage());
        return false;
    }
}

public static boolean updateStudentMarks(String studentId, double java, double dbms, double web, double ent, double lang, double avg, String grade) {
    String sql = "UPDATE marks SET java_prog=?, db_ms=?, web_design=?, entrepreneurship=?, language=?, average_score=?, final_grade=? WHERE student_id=?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        p_update_sequence(pstmt, java, dbms, web, ent, lang, avg, grade, studentId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException ex) {
        return false;
    }
}

private static void p_update_sequence(PreparedStatement pstmt, double java, double dbms, double web, double ent, double lang, double avg, String grade, String id) throws SQLException {
    pstmt.setDouble(1, java);
    pstmt.setDouble(2, dbms);
    pstmt.setDouble(3, web);
    pstmt.setDouble(4, ent);
    pstmt.setDouble(5, lang);
    pstmt.setDouble(6, avg);
    pstmt.setString(7, grade);
    pstmt.setString(8, id);
}

public static boolean modifyStudentBaseProfileFromDashboard(String studentId, String firstName, String lastName, String classYear) {
    String sql = "UPDATE students SET first_name = ?, last_name = ?, class_year = ? WHERE student_id = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, firstName);
        pstmt.setString(2, lastName);
        pstmt.setString(3, classYear);
        pstmt.setString(4, studentId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException ex) {
        return false;
    }
}

public static boolean deleteStudentFromDb(String studentId) {
    String sql = "UPDATE students SET status = 0 WHERE student_id = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
        pstmt.setString(1, studentId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException ex) {
        return false;
    }
}
}