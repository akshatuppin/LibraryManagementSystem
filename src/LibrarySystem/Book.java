package LibrarySystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.PreparedStatement;

public class Book {

    private Connection connection;
    private Scanner scanner ;

    public Book(Connection connection , Scanner scanner){
        this.connection = connection;
        this.scanner = scanner ;
    }

    public String insert_book(){
        String insert_query = "INSERT INTO Book(title,author) VALUES (?,?)";
        System.out.println("Enter Title of the Book: ");
        String title = scanner.nextLine();
        System.out.println("Enter the name of the Author: ");
        String author = scanner.nextLine();
        if(!book_present(title)){
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(insert_query);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Book Already Present for this Title.");
    }

    public boolean book_present(String title){
        String search_query = "SELECT * FROM Book WHERE title = ? ";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(search_query);
            preparedStatement.setString(1,title);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
