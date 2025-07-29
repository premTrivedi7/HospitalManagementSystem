package HospitalManagementSystem;

import javax.print.Doc;
import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital_management_sys_db";
    private static final String userName = "root";
    private static final String password = "1234";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
Scanner scanner = new Scanner(System.in);
        try{
            /Connection is a Java interface from the java.sql package.
            //It represents a connection to a specific database (like MySQL, PostgreSQL, Oracle)
            //✅ 2. DriverManager.getConnection(...)
            //DriverManager is a built-in Java class used to manage a list of database drivers.
            //The getConnection() method tries to connect to the database
            Connection connection = DriverManager.getConnection(url, userName, password);
            Patient patient = new Patient(connection, scanner);
            Doctors doctors = new Doctors(connection);
            Appointments appointments = new Appointments(connection, scanner);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. View Appointments");
                System.out.println("6. Cancel Appointments");
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1://add patient
                        patient.addPatient();
                        System.out.println();
                        continue;
                    case 2: // view patient
                        patient.viewPatients();
                        System.out.println();
                        continue;
                    case 3 : // view doctors
                        doctors.viewDoctors();
                        System.out.println();
                        continue;
                    case 4: // book appointments
                        bookAppointment(patient, doctors, connection, scanner);
                        System.out.println();
                        continue;
                    case 5: //view appointments
                        appointments.viewAppointments();
                        break;
                    case 6: // Cancel appointment
                        appointments.cancelAppointments();
                        break;
                    case 7: //exit
                        System.out.println("Thank you for visiting...");
                        return;
                    default:
                        System.out.println("Enter a valid choice!!!");
                        continue;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void bookAppointment(Patient patient, Doctors doctors, Connection connection, Scanner scanner){
        System.out.println("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        String patientNameQuery = "select name from patients where id = ?";
        String patientName = "";
        String doctorNameQuery = "select name from doctors where id = ?";
        String doctorName = "";
        try {
            //PreparedStatement: It's an interface from java.sql used to execute parameterized SQL queries.
            //It's faster and safer than Statement because:It avoids SQL injection.It can be reused with different values.
            //✅ preparedStatement (the variable)
            //This is the variable you're declaring to hold the prepared SQL statement object.
            //✅ connection.prepareStatement(...)
            //You're calling the prepareStatement() method on a Connection object.It takes a SQL query string
            PreparedStatement preparedStatement = connection.prepareStatement(patientNameQuery);
            preparedStatement.setInt(1, patientId);
            //Execute the SQL query 
            // Returns a ResultSet object when executing a GET method, which represents the result table of data returned by the query.
            //ResultSet resultSet: when executing a GET method
            //You’re creating a variable to store the returned rows.
            //This ResultSet acts like a cursor that starts before the first row
            //✅ resultSet.next();
            //Moves the cursor to the next row in the result set
            //Returns:
            //true → if there's another row.
            //false → if there are no more rows.
            //Must be called before accessing any data (e.g., resultSet.getString("column")).
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            patientName = resultSet.getString("name");
            PreparedStatement preparedStatement1 = connection.prepareStatement(doctorNameQuery);
            preparedStatement1.setInt(1, doctorId);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            resultSet1.next();
            doctorName = resultSet1.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (patient.getPatientById(patientId) && doctors.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date, patient_name, doctor_name) VALUES (?, ?, ?, ?, ?)";

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    preparedStatement.setString(4, patientName);
                    preparedStatement.setString(5, doctorName);
                    int rowsAffected = preparedStatement.executeUpdate(); // POST Query returns num of rows affected and not the ResultSet
                    if(rowsAffected>0) System.out.println("Appointment booked.");
                    else System.out.println("Appointment booking failed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }else{
                System.out.println("Doctor unavailable on "+appointmentDate);
            }
        }else{
            System.out.println("Doctor/Patient not found");
        }
    }

    private static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "select count(*) from appointments where doctor_id = ? and appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0) return true;
                else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
