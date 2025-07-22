package LibrarySystem;

import java.sql.*;
import java.util.Scanner;

public class Library {
    private final Connection connection;

    private final Scanner scanner ;

    public Library(Connection connection , Scanner scanner){
        this.connection = connection ;
        this.scanner = scanner ;
    }

    // issue_book(user_id ,book_id) , return_book(user_id ,book_id) ,search_book

    public void issue_book(int user_id ) throws SQLException {
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
                        if(available){
                            int transaction_no = generate_transaction_id();
                            String issue_transaction_table = "INSERT INTO Transaction (transaction_id,user_id,book_id) VALUES (?,?,?)" ;
                            PreparedStatement preparedStatement2 = connection.prepareStatement(issue_transaction_table);
                            preparedStatement2.setInt(1,transaction_no);
                            preparedStatement2.setInt(2,user_id);
                            preparedStatement2.setInt(3,book_id);
                            int rows_inserted = preparedStatement2.executeUpdate();
                            if(rows_inserted > 0){
                                PreparedStatement pre = connection.prepareStatement("SELECT * FROM Book WHERE book_id = ?");
                                pre.setInt(1,book_id);
                                ResultSet get_book = pre.executeQuery();
                                PreparedStatement preparedStatement3 = connection.prepareStatement("UPDATE Book SET is_available = false WHERE book_id =? ");
                                preparedStatement3.setInt(1,book_id);
                                int rowsAffected = preparedStatement3.executeUpdate();
                                if(rowsAffected >0){
                                    if(get_book.next()){
                                        String book_name = get_book.getString("title");
                                        System.out.println("The book with the Title: "+book_name);
                                        System.out.println("Is Issued to user with user ID: "+user_id);
                                        connection.setAutoCommit(true);
                                    }
                                    System.out.println("Book issued Successfully...");
                                    System.out.println("Thank you .");
                                }else{
                                    System.out.println("Unable to update Book table..");
                                    return;
                                }
                            }else{
                                System.out.println("Book is not issued due to some Reasons   Sorry ...");
                            }
                        }else{
                            System.out.println("Book is currently not available..");
                        }
                    }else{
                        System.out.println("Book ID is not valid..");
                    }
                }else{
                    System.out.println("Invalid user ID ..");
                }
            }else{
                System.out.println("User for this user id not found.");
                System.out.println("Recheck and Retry .");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void return_book(int user_id){
        System.out.println("Enter the Book ID you want to Return: ");
        int book_id = scanner.nextInt();
        scanner.nextLine();

        String check_query = "SELECT * FROM  Transaction WHERE user_id = ? AND book_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(check_query);
            preparedStatement.setInt(1,user_id);
            preparedStatement.setInt(2,book_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Date issue_date = resultSet.getDate("issue_date");
                System.out.println("Your issued book with the book id: "+book_id);
                System.out.println("You issued this book on "+issue_date);
                System.out.print("Do you want to return this Book? [y/n]");
                String choice = scanner.nextLine().trim();
                int transaction_id  = resultSet.getInt("transaction_id");
                if(choice.equalsIgnoreCase("y")){
                    java.sql.Date return_date = new java.sql.Date(System.currentTimeMillis());

                    String transaction_query = " UPDATE TABLE Transaction SET return_date = ? WHERE transaction_id = ? ";
                    String book_query = "UPDATE TABLE Book SET is_available = ? WHERE book_id = ? ";
                    PreparedStatement transaction = connection.prepareStatement(transaction_query);
                    PreparedStatement book = connection.prepareStatement(book_query);

                    //update transaction table
                    transaction.setDate(1,return_date);
                    transaction.setInt(2,transaction_id);
                    transaction.executeUpdate();

                    //update book table
                    book.setBoolean(1,true);
                    book.setInt(2,book_id);
                    book.executeUpdate();

                    System.out.println("Book returned successfully!!");


                }else if(choice.equalsIgnoreCase("n")){
                    System.out.println("Okay, keeping the book for now.");
                    return;
                }else{
                    System.out.println("Enter the valid Choice [y/n]");
                }
            }else{
                System.out.println("No book is issued on your User ID or Book ID is incorrect");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void search_book(){
        String search_query = "SELECT * FROM Book WHERE book_id = ?";
        System.out.println("Please enter the book ID present on the Book: ");
        int id = scanner.nextInt();
        try{
            PreparedStatement search = connection.prepareStatement(search_query);
            search.setInt(1,id);
            ResultSet result = search.executeQuery();
            if(result.next()){

            }else{
                System.out.println("Book with this ID is not issued for you OR recheck the BOOK ID.");
                System.out.println("Do you want to search again ?[y/n]");
                String ch = scanner.nextLine();
                if(ch.equalsIgnoreCase("y")){
                    search_book();
                }else{
                    System.out.println("Ok , Moving to main ..");
                    return;
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }


    public int generate_transaction_id(){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT transaction_id FROM Transaction ORDER BY transaction_id DESC LIMIT 1");
            if(resultSet.next()){
                int new_transaction_id = resultSet.getInt("transaction_id");
                return new_transaction_id + 1;
            }else{
                return 1000;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 1000;
    }
}
