import java.util.Scanner;

import java.sql.*;
import java.lang.*;

public class BeSocial {

    private Connection connection;
    private Statement statement;
    private String url;
    private String username;
    private String password;
    private Scanner scan = new Scanner(System.in);
    private int userID; 

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
        if (url.equals("localhost")) {
            url = "jdbc:postgresql://localhost:5432/";
        }

        System.out.print("Username:");
        username = input.next();

        System.out.print("Password:");
        password = input.next();

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createProfile(String name, String email, String password, Date dateOfBirth)
            throws SQLException {
        String count = "SELECT COUNT(*) FROM profile";
        Statement countStatement = connection.createStatement();
        ResultSet rs = countStatement.executeQuery(count);

        // testing purpose
        int userID = 0;
        if (rs.next()) {
            userID = rs.getInt("count");
        }

        String query = "INSERT INTO profile (userID, name, email, password, dateOfBirth, lastLogin) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement st = connection.prepareStatement(query);

        // Date sql_date = new Date (dateOfBirth.getYear() ,dateOfBirth.getMonth(),
        // dateOfBirth.getDay());

        st.setInt(1, userID);
        st.setString(2, name);
        st.setString(3, email);
        st.setString(4, password);
        st.setDate(5, dateOfBirth);
        st.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        try{
            st.executeUpdate();
            return 1;
        }
        catch(SQLException e)
        {
            return -1;
        }
        

    }

    public int login(String email, String password) throws SQLException {
        String query = "SELECT * FROM profile WHERE email = ? AND password = ?";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, email);
        st.setString(2, password);

        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            String rname = rs.getString("name");
            System.out.println("Welcome " + rname + " to BeSocial");
            userID = rs.getInt(("userID"));
            // update last_login

            return userID;
        } else {
            return -1;
        }
    }

    

    public int dropProfile() {

        return 1;
    }

    public int initiateFriendship(int friendID) {
        //use select to get name and info from frined ID
        String select = "SELECT name FROM profile WHERE userID="+ friendID;
        String friendName= "";
        try{
            Statement st =  connection.createStatement();
            ResultSet rs = st.executeQuery(select);
            
            if(rs.next()){
                friendName = rs.getString("name");
            }
        }
        catch(SQLException s){

        }
        System.out.println("Sending request to "+ friendName);
        System.out.print("Type in message you would like to send ");
        String text = scan.nextLine();            

        //scanner to get confirmation from user
        System.out.println("Are you sure you want to send a friend request to " + friendName + " type yes or no: ");
        String confirmation = scan.next();
        if(confirmation.equals("yes")){
            String insert = "INSERT INTO pendingFriend(fromID, toID, requestText) " + "VALUES(?, ?, ?)";
        
            try{
                PreparedStatement pst = connection.prepareStatement(insert);
                pst.setInt(1, userID);
                pst.setInt(2, friendID);
                pst.setString(3, text);
                pst.executeUpdate();
                

            }
            catch(SQLException s){
                System.out.println("error adding into db ");
            }
            return 1;
        }
        else{
            return -1;
        }

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