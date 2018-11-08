package CECS_323.JDB;

import java.sql.*;
import java.util.Scanner;
import java.util.*;

/**
 *
 * @author Jacen Tan 
 * @StudentID: 012393782
 * Description: Sloppy but came together.
 */
public class CECS323JavaTermProject {
    //  Database credentials
    static String USER;
    static String PASS;
    static String DBNAME;
    static final String Books = "Books";
    static final String BookTitle = "BookTitle";
    static final String Publishers = "Publishers";
    static final String PublisherName = "PublisherName";
    static final String WritingGroups = "WritingGroups";
    static final String GroupName = "GroupName";
    //This is the specification for the printout that I'm doing:
    //each % denotes the start of a new field.
    //The - denotes left justification.
    //The number indicates how wide to make the field.
    //The "s" denotes that it's a string.  All of our output in this test are 
    //strings, but that won't always be the case.
    static final String displayFormat="%-20s%-20s%-20s%-20s\n";
    static final String displayFormatAlt = "%-25s%-20s%-20s%-20s%-20s%-20s%-20s%-20s\n";
    
// JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    static final String DB_URL_HEADER = "jdbc:derby://localhost:1527/";
    static String DB_URL = DB_URL_HEADER;
//            + "testdb;user=";
/**
 * Takes the input string and outputs "N/A" if the string is empty or null.
 * @param input The string to be mapped.
 * @return  Either the input string or "N/A" as appropriate.
 */
    public static String dispNull (String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0)
            return "N/A";
        else
            return input;
    }
    
    public static void listAll(Connection conn, String tableName, String data){
        String selectAll = "select " + data + " from " + tableName;
        try{
        PreparedStatement stmt = conn.prepareStatement(selectAll);
        ResultSet rset = stmt.executeQuery();
        
        System.out.println("Here is/are all the " + data +"s listed in " + tableName);
        while(rset.next()){
            String gName = rset.getString(data);
            System.out.println('\t' + gName);
        }
        rset.close();
        stmt.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void listData(Connection conn, String tableName, String pkey){
        String userInput = "";
        Scanner input = new Scanner(System.in);
        boolean validInput = false;
        while(!validInput){
            System.out.print("Please enter the value of " + pkey +" you wish to search for in " + tableName +": ");
            userInput = input.nextLine();
            if (!dispNull(userInput).equals(userInput)){
                System.out.println("You seem to have not entered anything. Please try again!");
            }
            else{
                validInput = true;
            }
        }
        
        String query = "";
        if (tableName.equals(WritingGroups)){
            query = query + "Select GroupName, HeadWriter, YearFormed, Subject from WritingGroups where GroupName = ?";
        }
        else if(tableName.equals(Publishers)){
            query = query + "Select PublisherName, PublisherAddress, PublisherPhone, PublisherEmail from Publishers where PublisherName = ?";
        }
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userInput);
            
            ResultSet rset = stmt.executeQuery();
            if (tableName.equals(WritingGroups)){
                printWritingGroup(rset);
            }
            else if (tableName.equals(Publishers)){
                printPublisher(rset);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void printWritingGroup(ResultSet rset){
        try{
            if(rset.next()){
                String gName = rset.getString(GroupName);
                String hWriter = rset.getString("HeadWriter");
                String yFormed = rset.getString("YearFormed");
                String subject = rset.getString("Subject");
                
                System.out.println("A result has been found!");
                System.out.printf(displayFormat, GroupName, "HeadWriter", "YearFormed", "Subject");
                System.out.printf(displayFormat, gName, hWriter, yFormed, subject);
            }
            else{
                System.out.println("Sorry! No results found under your specification!");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void printPublisher(ResultSet rset){
        try{
            if(rset.next()){
                String pName = rset.getString(PublisherName);
                String pAddress = rset.getString("PublisherAddress");
                String pPhone = rset.getString("PublisherPhone");
                String pEmail = rset.getString("PublisherEmail");
                
                System.out.println("A result has been found!");
                System.out.printf(displayFormat, PublisherName, "PublisherAddress", "PublisherPhone", "PublisherEmail");
                System.out.printf(displayFormat, pName, pAddress, pPhone, pEmail);
            }
            else{
                System.out.println("Sorry! No results found under your specification!");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void listBooks_WritingGroups(Connection conn){
        String userPKEY = "";
        String userFKEY = "";
        Scanner input = new Scanner(System.in);
        boolean validInput = false;
        while(!validInput){
            System.out.print("Please enter the value of BookTitle you wish to join using: ");
            userPKEY = input.nextLine();
            if (!dispNull(userPKEY).equals(userPKEY)){
                System.out.println("You seem to have not entered anything. Please try again!");
            }
            else{
                validInput = true;
            }
        }
        
        validInput = false;
        while(!validInput){
            System.out.print("Please enter the value of GroupName you wish to join using: ");
            userFKEY = input.nextLine();
            if (!dispNull(userFKEY).equals(userFKEY)){
                System.out.println("You seem to have not entered anything. Please try again!");
            }
            else{
                validInput = true;
            }
        }
        
        String query = "select BookTitle, PublisherName, YearPublished, NumberPages, GroupName, HeadWriter, YearFormed, Subject"
                + " from WritingGroups inner join Books using (GroupName) where BookTitle = ? AND GroupName = ?"; 
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userPKEY);
            stmt.setString(2, userFKEY);
            
            ResultSet rset = stmt.executeQuery();
            if(rset.next()){
                String bTitle = rset.getString(BookTitle);
                String gName = rset.getString(GroupName);
                String pName= rset.getString(PublisherName);
                String yPublished = rset.getString("YearPublished");
                String numPages = rset.getString("NumberPages");
                 String hWriter = rset.getString("HeadWriter");
                String yFormed = rset.getString("YearFormed");
                String subject = rset.getString("Subject");
                
                System.out.println("A result has been found!");
                System.out.printf(displayFormatAlt, BookTitle, PublisherName, "YearPublished", "NumberPages", 
                                    GroupName, "HeadWriter", "YearFormed", "Subject");
                System.out.printf(displayFormatAlt, bTitle, pName, yPublished, numPages, gName, hWriter, yFormed, subject);
            }
            else{
                System.out.println("Sorry! No results found under your specification!");
            }
            rset.close();
            stmt.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        
    }
    
    public static void listBooks_Publishers(Connection conn){
        String userPKEY = "";
        String userFKEY = "";
        Scanner input = new Scanner(System.in);
        boolean validInput = false;
        while(!validInput){
            System.out.print("Please enter the value of BookTitle you wish to join using: ");
            userPKEY = input.nextLine();
            if (!dispNull(userPKEY).equals(userPKEY)){
                System.out.println("You seem to have not entered anything. Please try again!");
            }
            else{
                validInput = true;
            }
        }
        
        validInput = false;
        while(!validInput){
            System.out.print("Please enter the value of PublisherName you wish to join using: ");
            userFKEY = input.nextLine();
            if (!dispNull(userFKEY).equals(userFKEY)){
                System.out.println("You seem to have not entered anything. Please try again!");
            }
            else{
                validInput = true;
            }
        }
        
        String query = "select BookTitle, GroupName, YearPublished, NumberPages, "
                + "PublisherName, PublisherAddress, PublisherPhone, PublisherEmail"
                + " from Publishers inner join Books using (PublisherName) where BookTitle = ? AND PublisherName = ?"; 
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userPKEY);
            stmt.setString(2, userFKEY);
            
            ResultSet rset = stmt.executeQuery();
            if(rset.next()){
                String bTitle = rset.getString(BookTitle);
                String gName = rset.getString(GroupName);
                String pName= rset.getString(PublisherName);
                String yPublished = rset.getString("YearPublished");
                String numPages = rset.getString("NumberPages");
                String pAddress = rset.getString("PublisherAddress");
                String pPhone = rset.getString("PublisherPhone");
                String pEmail = rset.getString("PublisherEmail");
                
                System.out.println("A result has been found!");
                System.out.printf(displayFormatAlt, BookTitle, GroupName, "YearPublished", "NumberPages", 
                                    PublisherName, "PublisherAddress", "PublisherPhone", "PublisherEmail");
                System.out.printf(displayFormatAlt, bTitle, gName, yPublished, numPages,pName, pAddress, pPhone, pEmail);
            }
            else{
                System.out.println("Sorry! No results found under your specification!");
            }
            rset.close();
            stmt.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        
    }

    public static int selectKeyForBook(Connection conn){
        Scanner input = new Scanner(System.in);
        boolean selection = false;      //boolean that tells whether the user has made a decision on which menu function to use or not.
        int decision = 1;              // a default decision.
        //Start of the decision making process.
        while(!selection){
        System.out.println("Please select from the following options which action you'd like to execute.");
        System.out.println("1. Provide BookTitle and GroupName");
        System.out.println("2. Provide BookTitle and PublisherName");
        System.out.print("Selection (Integer): ");
            try {
                decision = input.nextInt();
                if (decision != 1 && decision != 2){
                    throw new Exception("Your decision " + decision + " is not 1 or 2. Please try again.");
                }
                else{
                    selection = true;
                }
            }
            catch(InputMismatchException e){
                System.out.println("You did not enter an integer. Please try again.");
                input.next();
                System.out.println();
            }
            catch(Exception exc){
                System.out.println(exc);
                System.out.println();
            }       
        }
        
        return decision;
    }
    
    public static void insertBook(Connection conn, Scanner input){
       String bTitle = getStringInput("Book",BookTitle, input);
       String gName = getStringInput("Book", GroupName, input);
       String pName =  getStringInput("Book", PublisherName, input);
       String yPublished =  getIntInput("Book","YearPublished", input);
       String nPages = getIntInput("Book", "NumberPages", input);
       
       String query = "INSERT INTO Books (BookTitle, GroupName, PublisherName, "
               + "yearPublished, NumberPages) values (?, ?, ?, ?, ?)";  
       try{
           PreparedStatement stmt = conn.prepareStatement(query);
           stmt.setString(1, bTitle);
           stmt.setString(2, gName);
           stmt.setString(3, pName);
           stmt.setString(4, yPublished);
           stmt.setString(5, nPages);
           stmt.executeUpdate();
           
       }
       catch(SQLException e){
           System.out.println(e.getMessage());
       }
    }
    public static String getStringInput(String tableName, String columnName, Scanner input){
        String userInput = "";
        boolean validInput = false;
        while(!validInput){
            System.out.print("Please enter the value of " + columnName + " of the " +tableName+": ");
            userInput = input.nextLine();
            if (!dispNull(userInput).equals(userInput)){
                System.out.println("You seem to have not entered anything. Please try again!");
            }
            else{
                validInput = true;
            }
        }
        
        return userInput;
    }
    
    public static String getIntInput(String tableName, String columnName, Scanner input){
        String userInput = "";
        int userInt = 0;
        boolean validInput = false;
        while(!validInput){
            System.out.print("Please enter the value of "+ columnName + " of the " + tableName+": ");
            try {
                    
                    userInt = input.nextInt();
                    
                    if (userInt < 0){
                        throw new Exception("Your value " + userInt + " is not valid. Please try again.");
                    }
                    else{
                        validInput = true;
                    }
                }
                catch(InputMismatchException e){
                    System.out.println("You did not enter an integer. Please try again.");
                    input.next();
                    System.out.println();
                }
                catch(Exception exc){
                    System.out.println(exc);
                    System.out.println();
                }   
        }
        return userInput = "" +userInt;
    }
    
    public static void removeBook(Connection conn, Scanner in){
        String bTitle = "";
        String gName = "";
        String pName = "";
        String query = "Delete from Books where BookTitle = ? AND ";
        try{
            int choice = selectKeyForBook(conn);
            bTitle = getStringInput("Book", BookTitle, in);
            if (choice == 1){
                gName = getStringInput("Book", GroupName, in);
                query = query + "GroupName = ?";
            }
            else if (choice == 2){
                pName = getStringInput("Book", PublisherName, in);
                query = query + "PublisherName = ?";
            }
            else{
                System.out.println("I messed up.");
            }
            //System.out.println(query); was used in testing
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1,bTitle);
            if (choice==1){
                stmt.setString(2, gName);
            }
            else{
                stmt.setString(2, pName);
            }
            if (stmt.executeUpdate() == 0){
                System.out.println("The book did not exist.");
            }
            else{
                System.out.println("The book has been removed.");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        listAll(conn, Books, BookTitle); //Automatically Executing Option 1
        
    }
   
    public static void option8(Connection conn, Scanner in){
        String pName = getStringInput("Publisher", "new PublisherName", in);
        String pAddress = getStringInput("Publisher", "new PublisherAddress", in);
        String pPhone = getStringInput("Publisher", "new PublisherPhone", in);
        String pEmail = getStringInput("Publisher", "new PublisherEmail", in);
        
        try{
            String query = "INSERT INTO Publishers (PublisherName, PublisherAddress, PublisherPhone, PublisherEmail) "
                    + "values (?, ?, ?, ?) ";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, pName);
            stmt.setString(2, pAddress);
            stmt.setString(3, pPhone);
            stmt.setString(4, pEmail);
            stmt.executeUpdate();
            
            String oldPublisher = getStringInput("Publisher", " Old PublisherName", in);
            String queryAlt = "UPDATE Books SET PublisherName = ? where PublisherName = ?";
            PreparedStatement stmtAlt = conn.prepareStatement(queryAlt);
            stmtAlt.setString(1, pName);
            stmtAlt.setString(2, oldPublisher);
            if(stmtAlt.executeUpdate() == 1){
            
                System.out.println("\nChecking old books published by old publisher!");
                String checkQuery = "Select BookTitle, GroupName, PublisherName, YearPublished, NumberPages "
                        + " from Books where PublisherName = ?";

                PreparedStatement checkSTMT = conn.prepareStatement(checkQuery);
                checkSTMT.setString(1, pName);
                ResultSet rSet = checkSTMT.executeQuery();
            
                while(rSet.next()){
                    String bTitle = rSet.getString(BookTitle);
                    String publisherN = rSet.getString(PublisherName);
                    String gName = rSet.getString(GroupName);
                    String yearPublished = rSet.getString("YearPublished");
                    String numberPages = rSet.getString("NumberPages");
                    System.out.printf("%-20s%-20s%-20s%-20s%-20s\n", BookTitle, PublisherName, GroupName, "YearPublished", "NumberPages");
                    System.out.printf("%-20s%-20s%-20s%-20s%-20s\n", bTitle, publisherN, gName, yearPublished, numberPages);
                }
            }
            else if (stmtAlt.executeUpdate() == 0){
                System.out.println("The old publisher could not be found and thus executeUpdate returned 0");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static String getStringForPublisher(String columnName, Scanner input){
        String userInput = "";
        boolean validInput = false;
        while(!validInput){
            System.out.print("Please enter the value of " + columnName + " of the book: ");
            userInput = input.nextLine();
            if (!dispNull(userInput).equals(userInput)){
                System.out.println("You seem to have not entered anything. Please try again!");
            }
            else{
                validInput = true;
            }
        }
        
        return userInput;
    }
    public static void main (String [] args){
        
        Scanner in = new Scanner(System.in);
        //Step 1 Obtaining connection
        boolean hasConnection = false;
        Connection conn = null; //initialize the connection
        while(!hasConnection){
           try{ System.out.print("Name of the database ( not the user account): ");
            DBNAME = in.nextLine();

            //Constructing Database URL connection String
            DB_URL = DB_URL_HEADER + DBNAME;
            conn = DriverManager.getConnection(DB_URL);
            hasConnection = true;
           }
           catch(SQLException se){
                System.out.println();
                System.out.println(se);
                System.out.println();
           }
        }
        //Step 1 Obtaining connection END
        
        
        boolean finished = false;
       
        
        //Start of the Menu construction.
        while (!finished){
            boolean selection = false;      //boolean that tells whether the user has made a decision on which menu function to use or not.
            int decision = 10;              // a default decision.
            //Start of the decision making process.
            while(!selection){
            System.out.println("Please select from the following options which action you'd like to execute.");
            System.out.println("1. List all Writing Groups");
            System.out.println("2. List all data for a writing group specified by the user");
            System.out.println("3. List all publishers");
            System.out.println("4. List all data for a publisher specified by the user");
            System.out.println("5. List all book titles");
            System.out.println("6. List all data for a book specified by the user");
            System.out.println("7. Insert a new book");
            System.out.println("8. Insert a new publisher and update books to be published by this new publisher");
            System.out.println("9. Remove a book specified by the user");
            System.out.println("10. Exit the program");
            System.out.print("Selection (Integer): ");
                try {
                    decision = in.nextInt();
                    if (decision < 1 || decision > 10){
                        throw new Exception("Your decision " + decision + " is not between 1 and 10. Please try again.");
                    }
                    else{
                        selection = true;
                    }
                }
                catch(InputMismatchException e){
                    System.out.println("You did not enter an integer. Please try again.");
                    in.next();
                    System.out.println();
                }
                catch(Exception exc){
                    System.out.println(exc);
                    System.out.println();
                }
                in.nextLine(); //Gets rid of what's left behind from the integer input.
            }
            //End of the decision making process.
            
            //Start of the Menu Execution
            switch (decision){
                case 1:
                    System.out.println("You have selected option 1.");
                    listAll(conn, WritingGroups, GroupName);
                    break;
                case 2:
                    System.out.println("You have selected option 2.");
                    listData(conn, WritingGroups, GroupName);
                    break;
                case 3:
                    System.out.println("You have selected option 3.");
                    listAll(conn, Publishers, PublisherName);
                    break;
                case 4:
                    System.out.println("You have selected option 4.");
                    listData(conn, Publishers, PublisherName);
                    break;
                case 5:
                    System.out.println("You have selected option 5.");
                    listAll(conn, Books, BookTitle);
                    break;
                case 6:
                    System.out.println("You have selected option 6.");
                    System.out.println();
                    int choice = selectKeyForBook(conn);
                    if (choice == 1){
                        listBooks_WritingGroups(conn);
                    }
                    else if (choice == 2){
                        listBooks_Publishers(conn);
                    }
                    break;
                case 7:
                    System.out.println("You have selected option 7.");
                    insertBook(conn,in);
                    break;
                case 8:
                    System.out.println("You have selected option 8.");
                    option8(conn,in);
                    break;
                case 9: 
                    System.out.println("You have selected option 9.");
                    removeBook(conn,in);
                    break;
                case 10:
                    System.out.println("You have selected option 10.");
                    finished = true;
                    break;
                default:
                    System.out.println("Default Case has been selected. We will exit the program. Have a good Day!");
                    finished = true;
            }
            //End of the Menu Execution
            System.out.println();
            
        }
        //End of the menu process.
   }
}
    
    
