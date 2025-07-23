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

        try (Connection connection = DriverManager.getConnection(url, root, password);
             Scanner scanner = new Scanner(System.in)) {

            User user = new User(connection, scanner);
            Book book = new Book(connection, scanner);
            Library library = new Library(connection, scanner);
            Admin admin = new Admin(connection, scanner); // Admin class handles login

            while (true) {
                System.out.println("\n===== WELCOME TO LIBRARY MANAGEMENT SYSTEM =====");
                System.out.println("1. Register");
                System.out.println("2. Login as User");
                System.out.println("3. Login as Admin");
                System.out.println("4. Exit");
                System.out.print("Enter your Choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        user.register();
                        break;

                    case 2:
                        String email = user.Login();
                        if (email != null) {
                            System.out.println("\nUser Logged in Successfully..");
                            userMenu(scanner, library, book, email);
                        }
                        break;

                    case 3:
                        if (admin.login()) {
                            adminMenu(scanner, library, book);
                        }
                        break;

                    case 4:
                        System.out.println("Exiting system...");
                        return;

                    default:
                        System.out.println("Invalid Choice! Please try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void userMenu(Scanner scanner, Library library, Book book, String email) {
        while (true) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Issue a Book");
            System.out.println("2. Return a Book");
            System.out.println("3. Update Book Details");
            System.out.println("4. See All Books");
            System.out.println("5. View My Transactions");
            System.out.println("6. Delete a Book");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");
            int userChoice = scanner.nextInt();
            scanner.nextLine();

            switch (userChoice) {
                case 1:
                    library.issue_book(email);
                    break;
                case 2:
                    library.return_book(email);
                    break;
                case 3:
                    System.out.print("Enter Book ID to update: ");
                    int bookId = scanner.nextInt();
                    scanner.nextLine();
                    book.updateBookDetails(bookId);
                    break;
                case 4:
                    book.list_all_books();
                    break;
                case 5:
                    library.show_user_transactions(email);
                    break;
                case 6:
                    book.delete_book();
                    break;
                case 7:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid Choice!!");
            }
        }
    }

    private static void adminMenu(Scanner scanner, Library library, Book book) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Insert a Book");
            System.out.println("2. Delete a Book");
            System.out.println("3. View All Books");
            System.out.println("4. View Issued Books");
            System.out.println("5. View All Transactions");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int adminChoice = scanner.nextInt();
            scanner.nextLine();

            switch (adminChoice) {
                case 1:
                    book.insert_book();
                    break;
                case 2:
                    book.delete_book();
                    break;
                case 3:
                    book.list_all_books();
                    break;
                case 4:
                    library.show_issued_books();
                    break;
                case 5:
                    library.show_all_transactions();
                    break;
                case 6:
                    System.out.println("Returning to Main Menu...");
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
