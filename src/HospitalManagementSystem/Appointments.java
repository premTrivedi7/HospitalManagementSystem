package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Appointments {
    private Connection connection;
    private Scanner scanner;
    public Appointments(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }
    public void viewAppointments() {
        String viewAppointmentQuery = "select * from appointments";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(viewAppointmentQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Appointments: ");
            System.out.println("+-----------------+------------+--------------+-----------+-------------+-------------+");
            System.out.println("| Appointment Id  | Patient Id | Patient Name | Doctor Id | Doctor Name | Date        |");
            System.out.println("+-----------------+------------+--------------+-----------+-------------+-------------+");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String patient_id = resultSet.getString("patient_id");
                String doctor_id= resultSet.getString("doctor_id");
                String appointment_date= resultSet.getString("appointment_date");
                String patient_name= resultSet.getString("patient_name");
                String doctor_name= resultSet.getString("doctor_name");
                System.out.printf("|  %-15s|  %-10s|  %-12s|  %-9S|  %-11s|  %-11s|\n", id, patient_id,patient_name, doctor_id,doctor_name, appointment_date);
                System.out.println("+-----------------+------------+--------------+-----------+-------------+-------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void cancelAppointments(){
        System.out.println("Patient Name: ");
        String patient_name = scanner.next();
        System.out.println("Appointment Date: ");
        String appointment_date = scanner.next();
        if(checkAppointmentExist(patient_name, appointment_date)){
        String cancelQuery =  "delete from appointments where patient_name = ? and appointment_date =?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(cancelQuery);
            preparedStatement.setString(1, patient_name);
            preparedStatement.setString(2, appointment_date);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows==1) System.out.println("Appointment Cancelled.");
            else{
                System.out.println("Some error occured");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }}else{
            return;
        }
    }

    private boolean checkAppointmentExist(String patient_Name, String appointment_Date) {
        String checkQuery = "select * from appointments where  patient_name = ? and appointment_date =?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkQuery);
            preparedStatement.setString(1, patient_Name);
            preparedStatement.setString(2, appointment_Date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) return true;
            else{
                System.out.println("Patient doesn't have appointment on "+ appointment_Date);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
