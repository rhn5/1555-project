import java.util.*;
import java.sql.*;
import java.time.*;
import java.lang.*;

public class BeSocial{

    private Connection connection;
	private Properties properties;
	private Statement statement;
	private String url;
	private String username;
	private String password;

    public BeSocial(){
        Scanner input = new Scanner(System.in);

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