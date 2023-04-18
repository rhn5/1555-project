import java.util.*;
import java.sql.*;
import java.time.*;
import java.lang.*;

public class BeSocial{

    private Connection connection;
	private Statement statement;
	private String url;
	private String username;
	private String password;

    public BeSocial(){
        Scanner input = new Scanner(System.in);
        System.out.println("<-----LOG INTO DB----->");
        
    
        /**
         * Prompts the user for a URL, username, and password to connect to a database.
         * If the URL is "localhost", it is converted to a PostgreSQL URL.
         *
         */
        System.out.print("URL:");
        url = input.next();
        if(url.equals("localhost"))
        {
            url = "jdbc:postgresql://localhost:5432/";
        }
        
        System.out.print("Username:");
        username = input.next();
        
        System.out.print("Password:");
        password = input.next();

        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public int createProfile(String name, String email, String password, LocalDate dateOfBirth) {
        String query = "INSERT INTO profiles (userID, name, email, password, dateOfBirth) " +
                       "VALUES (?, ?, ?, ?, ?)";
        return 1;
    }

    public int dropProfile(String email)
    {
        return 1;
    }

    public int login(String email, String password)
    {
        return 1;
    }

    public int initiateFriendship(String userID)
    {
        return 1;
    }

    public int confirmFriendRequests()
    {
        return 1;
    }

    public int createGroup()
    {
        return 1;
    }
    public int initiateAddingGroup()
    {
        return 1;
    }
    public int confirmGroupMembership()
    {
        return 1;
    }
    public int leaveGroup()
    {
        return 1;
    }

    public int searchForProfile()
    {
        return 1;
    }
    public int sendMessageToUser()
    {
        return 1;
    }
    public int sendMessageToGroup()
    {
        return 1;
    }
    public int displayMessages()
    {
        return 1;
    }
    public int displayNewMessages()
    {
        return 1;
    }
    public int displayFriends()
    {
        return 1;
    }
    public int rankGroups()
    {
        return 1;
    }
    public int rankProfiles()
    {
        return 1;
    }
    public int topMessages()
    {
        return 1;
    }
    public int threeDegrees()
    {
        return 1;
    }
    public int logout()
    {
        return 1;
    }
    public int exit()
    {
        return 1;
    }
    
}