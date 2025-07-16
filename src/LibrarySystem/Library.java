package LibrarySystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Library {
    private final Connection connection;

    private final Scanner scanner ;

    public Library(Connection connection , Scanner scanner){
        this.connection = connection ;
        this.scanner = scanner ;
    }

    // issue_book(user_id ,book_id) , return_book(user_id ,book_id) , getIssued_book(user_id) ,search_book

    public void issue_book(int user_id ){
        System.out.println("Enter the book ID: ");
        int book_id = scanner.nextInt();
        try{
            connection.setAutoCommit(false);
            if(user_id != 0 && book_id != 0){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM User WHERE user_id = ?");
                preparedStatement.setInt(1,user_id);
                ResultSet rows = preparedStatement.executeQuery();
                if(rows.next()){
                    String book_query = "SELECT * FROM Book WHERE book_id = ?" ;

                    PreparedStatement preparedStatement1 = connection.prepareStatement(book_query);
                    preparedStatement1.setInt(1,book_id);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()){
                        boolean available =resultSet.getBoolean("is_available");

                    }else{
                        System.out.println("Book ID is not valid..");
                    }
                }else{
                    System.out.println("Invalid user ID ..");
                    connection.setAutoCommit(true);
                    return ;
                }
            }else{
                System.out.println("User for this user id not found.");
                System.out.println("Recheck and Retry .");
                connection.setAutoCommit(true);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
