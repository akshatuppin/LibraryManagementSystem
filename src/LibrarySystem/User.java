package LibrarySystem;

import java.sql.Connection;
import java.util.Scanner;

public class User {
    private Connection connection ;

    private Scanner scanner ;

    public User(Connection connection , Scanner scanner){
        this.connection = connection ;
        this.scanner = scanner ;
    }



}
