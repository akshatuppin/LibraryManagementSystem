package LibrarySystem;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/library_system";
    private static final String root = "root";
    private static final String password = "Akshat@1026" ;

    public static void main(String args []){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        try{
            Connection connection = DriverManager.getConnection(url,root,password);
            Scanner scanner = new Scanner(System.in);
            User user = new User(connection,scanner);
            Book book = new Book(connection,scanner);


        }catch (SQLException e){
            e.printStackTrace();
        }

    }
}
