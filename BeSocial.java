import java.util.*;

import javax.naming.spi.DirStateFactory.Result;

import java.sql.*;
import java.text.ParseException;
import java.time.*;
import java.lang.*;

public class BeSocial {

    private Connection connection;
    private Properties properties;
    private Statement statement;
    private String url = "jdbc:postgresql://localhost:5432/";
    private String username;
    private String password;

    public BeSocial() {
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
        String count = "SELECT COUNT(*) FROM profiles";
        ResultSet rs = statement.executeQuery(count);
        rs.next();
        int userId = rs.getInt(1);
        System.out.println(userId);

        String query = "INSERT INTO profiles (userID, name, email, password, dateOfBirth) " + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st;
        try{
            st = connection.prepareStatement(query);
        }
        catch(SQLException s){
            System.out.println();
        }
        
        Date sql_date = new Date (dateOfbirth.getYear() ,dateOfbirth.getMonth(), dateOfbirth.getDay())
        

        
        st.setInt(1, userId);
        st.setString(2, name);
        st.setString(3, email);
        st.setString(4, password);
        st.setDate(5, dateOfBirth.toString());

        rs = st.executeQuery();
        if(rs.next()){
            return 1;
        }
        else{
            System.out.println();
            return -1;
        }
    }

    public int dropProfile(String email) {
        return 1;
    }

    public int login(String email, String password) {
        String query = "SELECT * FROM profiles WHERE email=? AND password=?";
        PreparedStatement st;// = connection.prepareStatement(query);
        try{
            st = connection.prepareStatement(query);
        }
        catch(SQLException s){
            System.out.println();
        }

        st.setString(1,email);
        st.setString(2,password);

        return 1;
    }

    public int initiateFriendship(String userID) {
        return 1;
    }

    public int confirmFriendRequests() {
        return 1;
    }

    public int createGroup() {
        return 1;
    }

    public int initiateAddingGroup() {
        return 1;
    }

    public int confirmGroupMembership() {
        return 1;
    }

    public int leaveGroup() {
        return 1;
    }

    public int searchForProfile() {
        return 1;
    }

    public int sendMessageToUser() {
        return 1;
    }

    public int sendMessageToGroup() {
        return 1;
    }

    public int displayMessages() {
        return 1;
    }

    public int displayNewMessages() {
        return 1;
    }

    public int displayFriends() {
        return 1;
    }

    public int rankGroups() {
        return 1;
    }

    public int rankProfiles() {
        return 1;
    }

    public int topMessages() {
        return 1;
    }

    public int threeDegrees() {
        return 1;
    }

    public int logout() {
        return 1;
    }

    public int exit() {
        return 1;
    }

}