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

    public void  insert_book(){
        scanner.nextLine();
        String insert_query = "INSERT INTO Book(title,author,is_available) VALUES (?,?,TRUE)";
        System.out.println("Enter Title of the Book: ");
        String title = scanner.nextLine();
        System.out.println("Enter the name of the Author: ");
        String author = scanner.nextLine();
        if(!book_present_title(title)){
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(insert_query);
                preparedStatement.setString(1,title);
                preparedStatement.setString(2,author);
                int rows_affected = preparedStatement.executeUpdate();
                if(rows_affected >0){
                    System.out.println("Book inserted successfully..");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else {
            System.out.println("Book already exists with this Title");
        }
    }

    public boolean book_present_title(String title){
        String search_query = "SELECT * FROM Book WHERE title = ? ";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(search_query);
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean book_present(int book_id){
        String search_query = "SELECT * FROM Book WHERE book_id = ? ";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(search_query);
            preparedStatement.setInt(1, book_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void updateBookDetails(int book_id){
        scanner.nextLine();
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
                    System.out.println("Book details updated successfully!");
                }else{
                    System.out.println("Update Failed!!");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            System.out.println("Book is not present for this Book ID ..");
        }
    }


    public void list_all_books() {
        String list_query = "SELECT * FROM Book";
        try (PreparedStatement preparedStatement = connection.prepareStatement(list_query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("===== BOOKS PRESENT RIGHT NOW =====");
            boolean booksFound = false;
            while (resultSet.next()) {
                booksFound = true;
                int book_id = resultSet.getInt("book_id");
                String book_name = resultSet.getString("title");
                String author = resultSet.getString("author");
                boolean is_available = resultSet.getBoolean("is_available");

                System.out.println("Book ID: " + book_id);
                System.out.println("Title: " + book_name);
                System.out.println("Author: " + author);
                System.out.println("Availability: " + (is_available ? "Available" : "Not Available"));
                System.out.println("-----------------------------------");
            }
            if (!booksFound) {
                System.out.println("No books found in the library.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete_book() {
        System.out.print("Enter the Book_ID of the book to be Deleted: ");
        int delete_id = scanner.nextInt();
        scanner.nextLine();

        String delete_query = "DELETE FROM Book WHERE book_id = ?";
        if (book_present(delete_id)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(delete_query)) {
                preparedStatement.setInt(1, delete_id);
                int delete_rows = preparedStatement.executeUpdate();
                if (delete_rows > 0) {
                    System.out.println("Book deleted successfully.");
                } else {
                    System.out.println("Book deletion failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No book found for the provided Book ID.");
        }
    }
}
