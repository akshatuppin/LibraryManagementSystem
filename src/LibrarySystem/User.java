package LibrarySystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private Connection connection ;

    private Scanner scanner ;

    public User(Connection connection , Scanner scanner){
        this.connection = connection ;
        this.scanner = scanner ;
    }

    void register(){
        scanner.nextLine();
        System.out.println("Enter Full Name: ");
        String name = scanner.nextLine();
        System.out.println("Enter Email: ");
        String email = scanner.nextLine();
        System.out.println("Enter Phone Number: ");
        String number =scanner.nextLine();
        System.out.println("Enter Password: ");
        String password = scanner.nextLine();

        if(user_exist(email)){
            System.out.println("User Already exist for this Email..");
            return;
        }else{
            String register_query = "INSERT INTO User (full_name,email,phone,password) VALUES(?,?,?,?)";

            try{
                PreparedStatement preparedStatement = connection.prepareStatement(register_query);
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,email);
                preparedStatement.setString(3,number);
                preparedStatement.setString(4,password);
                int affected_rows = preparedStatement.executeUpdate();

                if(affected_rows>0){
                    System.out.println("Register Successful !!");
                }else{
                    System.out.println("Registration Failed !!");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public boolean user_exist(String email) {
        String query = "SELECT * FROM User WHERE email = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String Login(){
        String login_query = "SELECT * FROM User WHERE email = ? AND password = ?";
        System.out.println("Enter Email: ");
        String email  = scanner.nextLine();
        System.out.println("Enter Password: ");
        String pass = scanner.nextLine();

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,pass);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return email ;
            }else{
                return  null;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
