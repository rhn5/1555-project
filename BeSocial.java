import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

import java.sql.*;
import java.lang.*;
import java.util.ArrayList;

public class BeSocial {

    private Connection connection;
    private Statement statement;
    private String url;
    private String username;
    private String password;
    private Scanner scan = new Scanner(System.in);
    private int userID;
    private Timestamp clockTime;

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

            String clock = "SELECT * FROM clock";
    
            Statement clockSt = connection.createStatement();
            ResultSet rs = clockSt.executeQuery(clock);
            if(rs.next()){
                clockTime = rs.getTimestamp("pseudotime");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createProfile(String name, String email, String password, Date dateOfBirth)
            throws SQLException {
        String count = "SELECT COUNT(*) FROM profile";
        Statement countStatement = connection.createStatement();
        ResultSet rs = countStatement.executeQuery(count);

        int id = -1;
        if (rs.next()) {
            id = rs.getInt("count") ;
        }

        String query = "INSERT INTO profile (userID, name, email, password, dateOfBirth, lastLogin) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement st = connection.prepareStatement(query);

            st.setInt(1, id);
            st.setString(2, name);
            st.setString(3, email);
            st.setString(4, password);
            st.setDate(5, dateOfBirth);
            st.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        } catch (SQLException s) {
            System.out.println("error adding new profile into database ");
            return -1;
        }

        return 1;

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

            return 1;
        } else {
            return -1;
        }
    }

    public int dropProfile() {

        return 1;
    }

    public int initiateFriendship(int friendID) {
        // use select to get name and info from frined ID
        String select = "SELECT name FROM profile WHERE userID=" + friendID;
        String friendName = "";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(select);

            if (rs.next()) {
                friendName = rs.getString("name");
            }
        } catch (SQLException s) {
            return -1;
        }
        System.out.println("Sending request to " + friendName);
        System.out.print("Type in message you would like to send:");
        String text = scan.nextLine();

        // scanner to get confirmation from user
        System.out.println("Are you sure you want to send a friend request to " + friendName + " type yes or no: ");
        String confirmation = scan.next();
        if (confirmation.equals("yes")) {
            String insert = "INSERT INTO pendingFriend(fromID, toID, requestText) " + "VALUES(?, ?, ?)";

            try {
                PreparedStatement pst = connection.prepareStatement(insert);
                pst.setInt(1, userID);
                pst.setInt(2, friendID);
                pst.setString(3, text);
                pst.executeUpdate();

            } catch (SQLException s) {
                System.out.println("error adding into db ");
                return -1;
            }
            return 1;
        } else {
            return -1;
        }

    }

    public int confirmFriendRequests() {
        String select = "SELECT requestText, fromID from pendingfriend where toID = " + userID + " order by fromID asc";
        int friendID = -1;
        String text = "";
        ArrayList<Integer> fromIDList = new ArrayList<>();
        ArrayList<String> textList = new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(select);
            int count = 1;
            while (rs.next()) {
                text = rs.getString("requestText");
                friendID = rs.getInt("fromID");
                fromIDList.add(friendID);
                textList.add(text);
    
                System.out.println(count + ". User id " + friendID + " said " + text);
                count++;
            }
        } catch (SQLException s) {
            System.out.println("NO pending Friend REquest :(");
            return -1;
        }

        System.out.println("Type in the line number that you want to add as a friend or type in all to accept all");
        String result = scan.next();
        if (result.equals("all")) {

            String insert = "INSERT INTO friend(userID1, userID2, JDate, requestText) " + "Values(?, ? , ? , ?)";
            for(int i = 0; i < fromIDList.size(); i++){            
                try{
                    PreparedStatement st = connection.prepareStatement(insert);
                    st.setInt(1, userID);
                    st.setInt(2, fromIDList.get(i));
                    st.setTimestamp(3,clockTime);
                    st.setString(4,textList.get(i));
                    st.executeUpdate();
                }
                catch(SQLException s){
                    System.out.println();
                }
            }
                String delete = "DELETE FROM pendingFriend where toID = " + userID;
                try{
                    Statement st = connection.createStatement();
                    int rowsDeleted = st.executeUpdate(delete);
                }
                catch(SQLException s){
                    return -1;
                }
                
        } else {
            int tempID = fromIDList.get(Integer.parseInt(result));
            String tempText = textList.get(Integer.parseInt(result));
            String insert = "INSERT INTO friend(userID1, userID2, JDate, requestText) " + "Values(?, ? , ? , ?)";
            try{
                PreparedStatement st = connection.prepareStatement(insert);
                st.setInt(1, userID);
                st.setInt(2, tempID);
                st.setTimestamp(3,clockTime);
                st.setString(4,tempText);
                st.executeUpdate();

                //now delete
                String delete = "DELETE FROM pendingFriend where toID = " + userID+ " AND fromID = " + tempID;
                
                    Statement deleteSt = connection.createStatement();
                    int rowsDeleted = deleteSt.executeUpdate(delete);
                
            }
            catch(SQLException s){
                return -1;
            }      
        }
        return 1;
    }

    public int createGroup(String name, String description, int size) {


        String count = "SELECT COUNT(*) FROM groupInfo";
        int groupID = -1;
        try{
            Statement countStatement = connection.createStatement();
            ResultSet rs = countStatement.executeQuery(count);
            if(rs.next()){
                groupID = rs.getInt("count");
            }
        }
        catch(SQLException s){
            return -1;
        }
        
        String query = "INSERT INTO groupInfo(gID, name, size, description)" + "VALUES (?, ?, ? ,?)";
        try{
            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, groupID);
            st.setString(2, name);
            st.setInt(3, size);
            st.setString(4, description);
            st.executeUpdate();
        }
        catch(SQLException s){
            return -1;
        }
        return 1;
    }

    public int initiateAddingGroup(int groupID, String requestText) {
        //given group ID and request text, create a pending request of adding the logged
        //in user to the group new entry pending gorup mem
        String select = "SELECT name FROM groupInfo WHERE gID= "+ groupID;
        String groupName = "";
        try{
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(select);
            if(rs.next()){
                groupName = rs.getString("name");
            }
        }
        catch(SQLException s){
            return -1;
        }
        String insert = "INSERT INTO pendingGroupMember(gID, userID, requestText, requestTime)"
            + "VALUES (?, ?, ?, ?)";
        try{
            PreparedStatement pst = connection.prepareStatement(insert);
            pst.setInt(1, groupID);
            pst.setInt(2, userID);
            pst.setString(3, requestText);
            pst.setTimestamp(4, clockTime);
            pst.executeUpdate();
        }
        catch(SQLException s){
            return -1;
        }

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
        String select = "SELECT * from message where toID = " + userID;
        try{
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(select);
            if(rs.next()){
                return 1;
            }
        }
        catch(SQLException s){
            return -1;
        }
        
        
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
        userID = -1;
        
        return 1;
    }

    public int exit() {
        return 1;
    }

}