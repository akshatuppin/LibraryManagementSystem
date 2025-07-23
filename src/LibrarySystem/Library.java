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

    public void issue_book(String userEmail) {
        System.out.print("Enter Book ID to Issue: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        int userId = getUserId(userEmail);
        if (userId == -1) {
            System.out.println("Invalid user email!");
            return;
        }

        String checkQuery = "SELECT is_available FROM Book WHERE book_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                boolean isAvailable = rs.getBoolean("is_available");
                if (isAvailable) {
                    String updateQuery = "UPDATE Book SET is_available = FALSE WHERE book_id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, bookId);
                        updateStmt.executeUpdate();
                    }

                    String insertTransaction = "INSERT INTO `Transaction` (user_id, book_id) VALUES (?, ?)";
                    try (PreparedStatement transStmt = connection.prepareStatement(insertTransaction)) {
                        transStmt.setInt(1, userId);
                        transStmt.setInt(2, bookId);
                        transStmt.executeUpdate();
                        System.out.println("Book issued successfully!");
                    }
                } else {
                    System.out.println("Book is already issued (Not Available).");
                }
            } else {
                System.out.println("No book found with this Book ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void return_book(String userEmail) {
        System.out.print("Enter Book ID to Return: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        int userId = getUserId(userEmail);
        if (userId == -1) {
            System.out.println("Invalid user email!");
            return;
        }

        String checkTransaction = "SELECT transaction_id FROM `Transaction` WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkTransaction)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int transactionId = rs.getInt("transaction_id");

                String updateBook = "UPDATE Book SET is_available = TRUE WHERE book_id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateBook)) {
                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();
                }

                String updateTransaction = "UPDATE `Transaction` SET return_date = CURRENT_TIMESTAMP WHERE transaction_id = ?";
                try (PreparedStatement transStmt = connection.prepareStatement(updateTransaction)) {
                    transStmt.setInt(1, transactionId);
                    transStmt.executeUpdate();
                    System.out.println("Book returned successfully!");
                }
            } else {
                System.out.println("No active transaction found for this book and user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void show_issued_books() {
        String query = "SELECT * FROM Book WHERE is_available = FALSE";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("===== ISSUED BOOKS =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Book ID: " + rs.getInt("book_id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("---------------------------");
            }
            if (!found) {
                System.out.println("No books are currently issued.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Show all transactions for a user
    public void show_user_transactions(String email) {
        int userId = getUserId(email);
        if (userId == -1) {
            System.out.println("Invalid user email!");
            return;
        }

        String query = "SELECT t.transaction_id, b.title, t.issue_date, t.return_date " +
                "FROM `Transaction` t JOIN Book b ON t.book_id = b.book_id " +
                "WHERE t.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Your Transactions:");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Transaction ID: " + rs.getInt("transaction_id"));
                System.out.println("Book Title: " + rs.getString("title"));
                System.out.println("Issued On: " + rs.getTimestamp("issue_date"));
                System.out.println("Returned On: " + rs.getTimestamp("return_date"));
                System.out.println("---------------------------------");
            }
            if (!found) {
                System.out.println("No transactions found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getUserId(String email) {
        String query = "SELECT user_id FROM User WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void show_all_transactions() {
        String query = "SELECT t.transaction_id, u.name AS user_name, b.title AS book_title, " +
                "t.issue_date, t.return_date " +
                "FROM `Transaction` t " +
                "JOIN User u ON t.user_id = u.user_id " +
                "JOIN Book b ON t.book_id = b.book_id " +
                "ORDER BY t.issue_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("===== ALL TRANSACTIONS =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Transaction ID: " + rs.getInt("transaction_id"));
                System.out.println("User: " + rs.getString("user_name"));
                System.out.println("Book: " + rs.getString("book_title"));
                System.out.println("Issued On: " + rs.getTimestamp("issue_date"));
                System.out.println("Returned On: " + rs.getTimestamp("return_date"));
                System.out.println("---------------------------------");
            }
            if (!found) {
                System.out.println("No transactions found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
