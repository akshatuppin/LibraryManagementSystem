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
        if(!book_present_title(title)){
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(insert_query);
                preparedStatement.setString(1,title);
                preparedStatement.setString(2,author);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else {
            throw new RuntimeException("Book Already Present for this Title.");
        }
        return "Failed to insert book.";
    }

    public boolean book_present_title(String title){
        String search_query = "SELECT * FROM Book WHERE title = ? ";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(search_query);
            preparedStatement.setString(1, title);
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

    public int get_book_id(int book_id){
        String search_query = "SELECT * FROM Book WHERE book_id = ?";
        try{
            PreparedStatement preaparedStatement = connection.prepareStatement(search_query);
            preaparedStatement.setInt(1,book_id);
            ResultSet resultSet = preaparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("book_id");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Book for this ID is not present .");
    }


    public boolean book_present(int book_id){
        String search_query = "SELECT * FROM Book WHERE book_id = ? ";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(search_query);
            preparedStatement.setInt(1, book_id);
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

    public void updateBookDetails(int book_id){
        System.out.println("Enter the New Title of the Book: ");
        String title = scanner.nextLine();
        System.out.println("Enter the Updated Author Name: ");
        String author = scanner.nextLine();
        String update_query = "UPDATE Book SET title = ? , author = ? WHERE book_id = ?";
        if(book_present(book_id)){
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(update_query);
                preparedStatement.setString(1,title);
                preparedStatement.setString(2,author);
                preparedStatement.setInt(3,book_id);
                int rows_affected = preparedStatement.executeUpdate();
                if(rows_affected >0){
                    System.out.println("The Book Title and Author are Updated now..");
                    System.out.println("Remember the Book ID: "+book_id);
                }else{
                    System.out.println("Updation Failed!!");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            System.out.println("Book is not present for this Book ID ..");
        }
    }


    public void list_all_books(){
        String list_query = "SELECT * FROM Book";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(list_query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("=====   BOOK PRESENT RIGHT NOW  =====");
            if(resultSet.next()){
                int book_id = resultSet.getInt("book_id");
                String book_name = resultSet.getString("title");
                String author = resultSet.getString("author");
                boolean is_available = resultSet.getBoolean("is_available");
                System.out.println("Book id: "+book_id);
                System.out.println("Book Title: "+book_name);
                System.out.println("Author of this Book is "+author);
                if(is_available){
                    System.out.println("Currently this Book is Available.");
                }else{
                    System.out.println("Currently this Book is Not Available.");
                }
            }else{
                System.out.println("There is no Book present in the library right now !!!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void delete_book(int book_id){
        String delete_query = "DELETE FROM Book WHERE book_id = ?";
        System.out.println("Enter the Book_ID of the book to be Deleted: ");
        int delete_id = scanner.nextInt();
        if(book_present(delete_id)){
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(delete_query);
                preparedStatement.setInt(1,delete_id);
                int delete_rows = preparedStatement.executeUpdate();
                if(delete_rows > 0){
                    System.out.println("Book Delete Successfully..");
                }else {
                    System.out.println("Book deletion Failed..");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            System.out.println("Book is not present for the ID provided ..");
        }
    }
}
