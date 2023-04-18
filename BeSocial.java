import java.util.Scanner;

import java.sql.*;
import java.lang.*;

public class BeSocial {

    private Connection connection;
    private Statement statement;
    private String url;
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

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public userInstance createProfile(String name, String email, String password, Date dateOfBirth) throws SQLException {
        String count = "SELECT COUNT(*) FROM profiles";
        ResultSet rs = statement.executeQuery(count);

        int userID = rs.getInt(1);
        System.out.println(userID);

        String query = "INSERT INTO profiles (userID, name, email, password, dateOfBirth) " + "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement st = connection.prepareStatement(query);

        Date sql_date = new Date (dateOfBirth.getYear() ,dateOfBirth.getMonth(), dateOfBirth.getDay());

        st.setInt(1, userID);
        st.setString(2, name);
        st.setString(3, email);
        st.setString(4, password);
        st.setDate(5, sql_date);

        rs = st.executeQuery();
        if(rs.next()){
            return new userInstance(userID);
        }
        else{
            System.out.println();
            return null;
        }
    }

    public int login(String email, String password) throws SQLException {
        String query = "SELECT * FROM profiles WHERE email=? AND password=?";
        PreparedStatement st = connection.prepareStatement(query);

        st.setString(1,email);
        st.setString(2,password);

        return 1;
    }

    private class userInstance {
        int userID;
        private userInstance(int userID) {
            this.userID = userID;
        }

        public int dropProfile() {
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
}