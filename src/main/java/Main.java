import org.postgresql.util.PSQLException;

import java.sql.*;

/**
 * @author Rebecca Elliott 101199034
 */
public class Main {

    public static final String url = "jdbc:postgresql://localhost:5432/university";
    public static final String user = "postgres";
    public static final String password = "admin";
    private Connection connection;

    public Main(String url, String user, String password) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("Connected to the database");
            }
            else throw new RuntimeException("Failed to connect to the database");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints all the students in the students table.
     */
    public void getAllStudents() {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM students");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.print(resultSet.getString("student_id") + "\t");
                System.out.print(resultSet.getString("first_name") + "\t");
                System.out.print(resultSet.getString("last_name") + "\t");
                System.out.print(resultSet.getString("email") + "\t");
                System.out.println(resultSet.getString("enrollment_date"));
            }
            System.out.println("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a new student to the students table.
     * @param first_name of the student
     * @param last_name of the student
     * @param email of the student
     * @param enrollment_date of the student
     * @return student_id if added, 0 otherwise
     */
    public int addStudent(String first_name, String last_name, String email, String enrollment_date) {
        Statement statement = null;
        try {
            String newStudent = "('" + first_name + "', '" + last_name + "', '" + email + "', '" + enrollment_date + "')";
            statement = connection.createStatement();
            statement.executeQuery("INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES\n" + newStudent + "RETURNING *");
            ResultSet resultSet = statement.getResultSet();
            System.out.println("Successfully inserted new student");
            if (resultSet.next()) {
                return resultSet.getInt("student_id");
            }
        } catch (PSQLException e) {
            if (e.getSQLState().equals("23505")) {
                // Handle duplicate key exception
                System.out.println("Duplicate key error: " + e.getMessage());
            } else {
                // Handle other SQL exceptions
                System.out.println("SQL error: " + e.getMessage());
            }
        }catch (SQLException e2) {
            throw new RuntimeException(e2);
        }
        return 0;
    }

    /**
     * Updates a student's email in the students table.
     * @param student_id id of student
     * @param new_email email to be changed to
     */
    public void updateStudentEmail(int student_id, String new_email) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery("UPDATE students SET email = '" + new_email + "' WHERE student_id = '" + student_id +"'");
        } catch (PSQLException e) {
            if (e.getSQLState().equals("02000")) {
                // successful update will still throw an error as nothing is returned in the return set
                System.out.println("Successfully updated student");
            } else {
                // Handle other SQL exceptions
                System.out.println("SQL error: " + e.getMessage());
            }
        }catch (SQLException e2) {
            throw new RuntimeException(e2);
        }
    }

    /**
     * Deletes a student from the students table.
     * @param student_id id of student
     */
    public void deleteStudent(int student_id) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery("DELETE FROM students WHERE student_id = '" + student_id +"'");
        } catch (PSQLException e) {
            if (e.getSQLState().equals("02000")) {
                // successful delete will still throw an error as nothing is returned in the return set
                System.out.println("Successfully deleted student");
            } else {
                // Handle other SQL exceptions
                System.out.println("SQL error: " + e.getMessage());
            }
        } catch (SQLException e2) {
            throw new RuntimeException(e2);
        }
    }
    public static void main(String[] args) {
        Main main = new Main(url, user, password);

        main.getAllStudents();
        int num = main.addStudent("June", "Fry", "june.fong@example.com", "2023-09-03");
        main.getAllStudents();
        main.updateStudentEmail(num, "june.fry@example.com" );
        main.getAllStudents();
        main.deleteStudent(num);
        main.getAllStudents();
    }
}
