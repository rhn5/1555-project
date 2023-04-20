import java.util.Scanner;
import javax.naming.spi.DirStateFactory.Result;
import java.sql.*;
import java.lang.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Hashtable;

public class BeSocial {

    private Connection connection;
    private Statement statement;
    private String url;
    private String username;
    private String password;
    private Scanner scan = new Scanner(System.in);
    private int userID;
    private Timestamp clockTime;
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        BeSocial beSocial = new BeSocial();
        Scanner kbd = new Scanner(System.in);
        ArrayList<String> validate = new ArrayList<String>();
        String input = "";
        Boolean exit = false;
        int topLevel = 0;
        int bottomLevel = 0;
        int loggedIn = -1;

        try {
            BufferedReader logo = new BufferedReader(new FileReader("logo.txt"));
            while (logo.ready()) {
                System.out.println(logo.readLine());
            }
        } catch (Exception e) {

        }

        System.out.println();

        while (!exit) {
            /**
             * Displays the main menu options for the user to select from.
             * If the user enters an invalid operation, they will be prompted to enter a
             * valid one.
             */
            if (topLevel == 0 && bottomLevel == 0) // TOP LEVEL MENU
            {
                System.out.println("<-----LOGIN/REGISTER----->");
                System.out.println("1. Create Profile");
                System.out.println("2. Login");
                System.out.println("3. Delete Profile");
                System.out.println("4. Exit");
                System.out.print("Input:");

                topLevel = kbd.nextInt();
                System.out.println(topLevel);
                while (topLevel <= 0 || topLevel >= 5) {
                    System.out.println("PLEASE ENTER A VALID OPERATION:");
                    topLevel = Integer.parseInt(kbd.next());
                }
            } else if (topLevel == 1 && bottomLevel == 0) // CREATING A USER PROFILE
            {
                System.out.println("<-----CREATE PROFILE----->");
                System.out.print("Name: ");
                String name = kbd.next();
                System.out.print("Email: ");
                String userEmail = kbd.next();
                System.out.print("Password\n");
                System.out.print("Minimum 8 Characters\nOne Uppercase\nOne Lowercase\nOne Number\n");
                String userPass = kbd.next();
                System.out.print("DOB (yyyy-mm-dd): ");
                String dob = kbd.next();  
                Date dateOfBirth = Date.valueOf(dob);

                beSocial.createProfile(name, userEmail, userPass, dateOfBirth);
                
                int success = beSocial.createProfile(name, userEmail, userPass, dateOfBirth);
                /**
                 * This loop creates a user profile using the provided name, email, password, and date of birth.
                 * If the profile creation fails, the user is prompted to enter their information again.
                 */
                int count = 1;
                while(success == -1 && count < 4)
                {
                    System.out.println("<-----FAILED TO CREATE USER PROFILE----->");
                    System.out.println("Attempt " + count + "/3");
                    System.out.print("Name: ");
                    name = kbd.next();
                    System.out.print("Email: ");
                    userEmail = kbd.next();
                    System.out.print("Password: ");
                    userPass = kbd.next();
                    System.out.print("DOB (yyyy-mm-dd): ");
                    dob = kbd.next();  
                    dateOfBirth = Date.valueOf(dob);
                    success = beSocial.createProfile(name, userEmail, userPass, dateOfBirth);
                    count++;
                }
                topLevel = 0; // PUSH USER BACK TO TOP LEVEL MENU

            } else if (topLevel == 2 && loggedIn == -1) // LOGIN
            {
                
                System.out.println("<-----LOGIN----->");
                System.out.print("Email: ");
                String userEmail = kbd.next();
                System.out.print("Password: ");
                String userPass = kbd.next();

                // LOG IN USER AND SET loggedIn TO loggedIn
                loggedIn = beSocial.login(userEmail, userPass);
                int count = 1;
                /**
                 * Continuously prompts the user for their email and password until a valid loggedIn is returned from the beSocial.login method.
                 * If the loggedIn is -1, the user is informed that their username or password is incorrect and prompted again.
                 */
                while(loggedIn == -1 && count < 4)
                {
                    System.out.println("<-----INCORRECT USERNAME/PASSWORD----->");
                    System.out.println("Attempt " + count + "/3");
                    System.out.print("Email: ");
                    userEmail = kbd.next();
                    System.out.print("Password: ");
                    userPass = kbd.next();
                    loggedIn = beSocial.login(userEmail, userPass);
                    count++;
                }
                if(count == 4)
                {
                    topLevel = 0;
                }
                
            } else if (topLevel == 3 && bottomLevel == 0) {
                System.out.println("<-----DELETE PROFILE----->");
                System.out.print("Email: ");
                String userEmail = kbd.next();
            }
            else if (topLevel == 4 && bottomLevel == 0) {
                System.out.println("<-----EXIT----->");
                exit = true;
            }



            /**
             * Displays a menu of options for the user to choose from based on their current state.
             * If the user is logged in, the menu will display options for interacting with the social network.
             * If the user is not logged in, the menu will prompt the user to log in or create an account.
             * Once the user selects an option, the corresponding action will be executed.
             */
            if (loggedIn != -1) {
                System.out.println("<-----BeSocial----->");
                System.out.println("1. initiateFriendship");
                System.out.println("2. confirmFriendRequests");
                System.out.println("3. createGroup");
                System.out.println("4. initiateAddingGroup");
                System.out.println("5. confirmGroupMembership");
                System.out.println("6. leaveGroup");
                System.out.println("7. searchForProfile");
                System.out.println("8. sendMessageToUser");
                System.out.println("9. sendMessageToGroup");
                System.out.println("10. displayMessages");
                System.out.println("11. displayNewMessages");
                System.out.println("12. displayFriends");
                System.out.println("13. rankGroups");
                System.out.println("14. rankProfiles");
                System.out.println("15. topMessages");
                System.out.println("16. threeDegrees");
                System.out.println("17. logout");
                System.out.print("Input:");
                bottomLevel = kbd.nextInt();
                if(!(bottomLevel > 0 && bottomLevel < 18))
                {
                    System.out.println("PLEASE ENTER A VALID OPERATION:");
                    bottomLevel = Integer.parseInt(kbd.next());
                }
            }
            if (bottomLevel == 1) {
                System.out.println("<-----FRIEND REQUEST----->");
                System.out.println("Enter UserID you want to become friends with:");
                int friendID;
                friendID = kbd.nextInt();
                int success = beSocial.initiateFriendship(friendID);
                if(success == -1)
                {
                    System.out.println("<-----FRIEND REQUEST FAILED----->");
                }
            }
            
            if (bottomLevel == 2) {
                System.out.println("<-----CONFIRM FRIEND REQUESTS----->");
                int success = beSocial.confirmFriendRequests();
                if(success == -1)
                {
                    System.out.println("<-----CONFIRMING FRIEND REQUEST(s) FAILED----->");
                }
            }
            if (bottomLevel == 3) {
                System.out.println("<-----CREATE GROUP----->");
                System.out.print("Name: ");
                String name = kbd.next();
                System.out.print("Description: ");
                String description = kbd.next();
                System.out.print("size:");
                int groupSize = kbd.nextInt();
                int success = beSocial.createGroup(name,description, groupSize);
                
                if(success == -1)
                {
                    System.out.println("<-----GROUP CREATION FAILED----->");
                }
            }
            if (bottomLevel == 4) {
                System.out.println("<-----GROUP JOIN REQUEST----->");
                System.out.println("Enter Request Text:");
                String request = kbd.next();
                System.out.println("Enter group ID: ");
                int gID = kbd.nextInt();
                int success = beSocial.initiateAddingGroup(gID, request);

                if(success == -1)
                {
                    System.out.println("<-----GROUP REQUEST FAILED----->");
                }
            }
            if (bottomLevel == 5) {

            }
            if (bottomLevel == 6) {

            }
            if (bottomLevel == 7) {

            }
            if (bottomLevel == 8) {

            }
            if (bottomLevel == 9) {

            }
            if (bottomLevel == 10) {

            }
            if (bottomLevel == 11) {

            }
            if (bottomLevel == 12) {

            }
            if (bottomLevel == 13) {

            }
            if (bottomLevel == 14) {

            }
            if (bottomLevel == 15) {

            }
            if (bottomLevel == 16) {

            }
            if (bottomLevel == 17) {
                loggedIn = -1;
                topLevel = 0;
                bottomLevel = 0;
                beSocial.logout();
            }
            
            

        }

    }
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
            clockSt.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createProfile(String name, String email, String password, Date dateOfBirth)
            throws SQLException {
        String count = "SELECT COUNT(*) FROM profile";
        Statement countStatement = connection.createStatement();
        ResultSet rs = countStatement.executeQuery(count);
        PreparedStatement st = null;
        int id = -1;
        if (rs.next()) {
            id = rs.getInt("count") ;
        }

        String query = "INSERT INTO profile (userID, name, email, password, dateOfBirth, lastLogin) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            st = connection.prepareStatement(query);

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
        finally{
            if(st != null){
                st.close();
            }    
            rs.close();

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
            st.close();
            rs.close();
            return 1;
        } else {
            st.close();
            rs.close();
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

            rs.close();
            st.close();
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

                pst.close();

            } catch (SQLException s) {
                System.out.println(s);
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
            st.close();
            rs.close();
        } catch (SQLException s) {
            System.out.println("NO pending Friend REquest :(");
            return -1;
        }

        System.out.println("Type in the line number that you want to add as a friend or type in all to accept all");
        String result = scan.next();
        if (result.equals("all")) {
            PreparedStatement st = null;
            String insert = "INSERT INTO friend(userID1, userID2, JDate, requestText) " + "Values(?, ? , ? , ?)";
            for(int i = 0; i < fromIDList.size(); i++){            
                try{
                    st = connection.prepareStatement(insert);
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
            if(st != null){
                try{
                    st.close();
                }
                catch(SQLException s){
                    System.out.println("error closing statement");
                }
            }
            String delete = "DELETE FROM pendingFriend where toID = " + userID;
            try{
                Statement deleteST = connection.createStatement();
                int rowsDeleted = deleteST.executeUpdate(delete);
                deleteST.close();
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
        //display formatted numbered list of all pending group member where user is group m
        //user shoud be prompted for a num of request they would liek to confirm, 1 at time, or all
        //move selectd requests to pending group member relation to groupmember using urrent time
        //if accepting pending group member would exceed group's size accepted requet should remain in pending
        //remaining requets which were not selected are declined and removed from pending
        //no pending group member request for any groups that the user is a manager of, 
        //message of no groups are curr managerd should be displated
        //no groups are currently managed if user is not a manager. 

        //find out if user is a manager
        String query = "SELECT gID FROM groupMember WHERE role = 'manager' userID=" + userID;
        int gID = 0;
        ArrayList<Integer> gidList= new ArrayList<>();
        ArrayList<Integer> sizeList = new ArrayList<>();
        ArrayList<String> textList = new ArrayList<>();
        ArrayList<Integer> uidList = new ArrayList<>();
        Hashtable<Integer,Integer> groupSize = new Hashtable<>();
        try{
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                gID = rs.getInt("gID");
                gidList.add(gID);//list of groups that user is a manager in
            }
        }
        catch(SQLException s){
            return -1;
        }
        for (int i = 0; i < gidList.size(); i ++){
            query = "SELECT size FROM groupInfo WHERE gID = " + gidList.get(i);
            Statement st = null;//connection.createStatement();
            ResultSet rs = null;//st.executeQuery(query);
            try{
                st = connection.createStatement();
                rs = st.executeQuery(query);
                if(rs.next()){
                    //sizeList.add(rs.getInt("size"));
                    groupSize.put(gidList.get(i), rs.getInt("size"));
                }
            }
            catch(SQLException s){

            }
            finally{
                if(st != null){
                    try{
                        st.close();
                    }
                    catch(SQLException s){
                        System.out.println("error closing st");
                    }
                }
                if(rs != null){
                    try{
                        rs.close();
                    }
                    catch(SQLException s){
                        System.out.println("error closing rs");
                    }
                }
            }
            
        }
        if(gidList.isEmpty()){
            System.out.println("User is not a manager in any groups");
            return 1;
        }
        for(int i = 0 ; i < gidList.size(); i ++){
            query = "SELECT requestText, userID FROM pendingGroupMember WHERE gID = " + gidList.get(i);
            try{
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    textList.add(rs.getString("requestText"));
                    uidList.add(rs.getInt("userID"));
                }
            }
            catch(SQLException s){
                System.out.println();
            }
        }
        if(uidList.isEmpty()){
            System.out.println("NO pending requests ");
            return 1;
        }
        for(int i = 1; i < textList.size() + 1; i++){
            System.out.println(i + " UserID: " + uidList.get(i-1) + " Request: " + textList.get(i));
        }
        //displayed now pick which one
        System.out.println("Type in the line number that you want to accept or type all to accept all");
        String result = scan.next();
        if(result.equals("all")){
            String insert = "INSERT INTO ";
        }

        //look for count of requests where user is man, if none display message
        
        return 1;
    }

    public int leaveGroup(String groupName) {
        int groupID = getGroupIDByName(groupName);
            try {
                PreparedStatement deleteGMemberStatement = connection
                        .prepareStatement("DELETE FROM groupMember WHERE userID = ? AND gID = ?");
                deleteGMemberStatement.setInt(1, userID);
                deleteGMemberStatement.setInt(2, groupID);
                deleteGMemberStatement.executeQuery();
                System.out.println("Successfully Left Group: " + groupName);
            } catch (SQLException e) {
                System.out.println("Error Leaving Group Please Try Again");
                return -1;
            }
        return 1;
    }

    public int searchForProfile(String searchWord) {
        
        String [] words = searchWord.split(" ");
        System.out.println(words.length);
        String query = "";
        PreparedStatement st = null;
        int c = 1;
        for(int i = 0; i < words.length; i ++){
            query += "SELECT * FROM profile WHERE name LIKE ?" + " OR email LIKE ?";
            try{
                st = connection.prepareStatement(query);
                System.out.println("%"+words[i]+"%");
                st.setString(c, "%"+words[i]+"%");
                c++;
                st.setString(c, "%"+words[i]+"%");
                c++;
            }
            catch(SQLException s){
                System.out.println("error");
            }
            if(i != words.length -1){
                query = query + " UNION ";
            }
        }
        System.out.println(query);
        //ResultSet rs = null;
        try{

            ResultSet rs = st.executeQuery(query);
            int count = 0;
            while(rs.next()){
                int id = rs.getInt("userID");
                String name = rs.getString("name");
                String email = rs.getString("email");
                System.out.println(count +" name = " + name + " email = " + "id = " + userID); 
                count++;
            }
        }
        catch(SQLException s){
            System.out.println("error executing query");
            System.out.println("Error message: " + s.getMessage());
            System.out.println("SQL state: " + s.getSQLState());
            System.out.println("Error code: " + s.getErrorCode());
            return -1;
        }
        finally{
            
            if(st != null){
                try{
                    st.close();
                }
                catch(SQLException s){
                    System.out.println("error closing statement");
                }
            }
        }
        return 1;
    }

    public int sendMessageToUser(String toUserEmail) {
        Scanner choices = new Scanner(System.in);
            try {
                PreparedStatement message = connection.prepareStatement("INSERT INTO message VALUES(?, ?, ?, ?, ?, ?)");
                String userName = getNameByEmail(toUserEmail);
                int toID = getUserIDByEmail(toUserEmail);
                int msgID;
                String msgBody;
                do {
                    msgID = (int) Math.random();
                }
                while (connection.prepareStatement("SELECT COUNT(*) FROM message WHERE msgID = %s", msgID)
                        .executeQuery().getInt(1) == 0);
                do {
                    System.out.println("Sending Message To: " + userName);
                    System.out.printf("Enter Message [Max 200 chars and can't be blank] -> ");
                    msgBody = choices.next();
                } while (msgBody.length() > 200 || msgBody.isBlank());
                message.setInt(1, msgID);
                message.setInt(2, userID);
                message.setString(3, msgBody);
                message.setInt(4, toID);
                message.setNull(5, Types.NULL);
                message.setTimestamp(6, Timestamp.from(java.time.Instant.now()));
                message.executeQuery();
            } catch (SQLException e) {
                System.out.println("Error Sending Message");
                return -1;
            }
            return 1;
    }

    public int sendMessageToGroup(String toGroupName) {
        Scanner choices = new Scanner(System.in);
        try {
            PreparedStatement message = connection.prepareStatement("INSERT INTO message VALUES(?, ?, ?, ?, ?, ?)");
            int groupID = getGroupIDByName(toGroupName);
            int msgID;
            String msgBody;
            do {
                msgID = (int) Math.random();
            }
            while (connection.prepareStatement("SELECT COUNT(*) FROM message WHERE msgID = %s", msgID)
                    .executeQuery().getInt(1) == 0);
            do {
                System.out.println("Sending Message To: " + toGroupName + " GroupID: " + groupID);
                System.out.printf("Enter Message [Max 200 chars and can't be blank] -> ");
                msgBody = choices.next();
            } while (msgBody.length() > 200 || msgBody.isBlank());
            message.setInt(1, msgID);
            message.setInt(2, userID);
            message.setString(3, msgBody);
            message.setNull(4, Types.NULL);
            message.setInt(5, groupID);
            message.setTimestamp(6, Timestamp.from(java.time.Instant.now()));
            message.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error Sending Message");
            return -1;
        }    
        return 1;
    }

    public int displayMessages() {
        // Create a statement to retrieve all messages sent to the user
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM message WHERE toUserID = ?");
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            // Loop through the result set and format each message for display
            while (rs.next()) {
                int msgID = rs.getInt("msgID");
                int fromID = rs.getInt("fromID");
                int groupIDMaybe = rs.getInt("toGroupID");
                Timestamp timestamp = rs.getTimestamp("timeSent");
                String formattedTimestamp = timestamp.toLocalDateTime().toString();
                String message = rs.getString("messageBody");

                System.out.printf("[%15s]\n", String.format("Message ID: %s", msgID));
                System.out.printf("[%25s]\n", String.format("From User: %s", getNameByUserID(fromID)));
                System.out.printf("[%20s]\n", String.format("Time Sent: %s", formattedTimestamp));
                if (groupIDMaybe != 0) System.out.printf("[%20s]\n", String.format("Sent To Group: %s", getGroupNameByID(groupIDMaybe)));
                System.out.printf("[%230s]\n", String.format("%30s sent: \n %200s", getNameByUserID(fromID), message));
            }

            // Print the footer for the message list
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("Total messages: " + rs.getRow());

            // Return the number of messages displayed
            return rs.getRow();
        } catch(SQLException e) {
            e.printStackTrace();
            return -1;
        }
        
    }

    public int displayNewMessages() {
        Timestamp lastLogin = Timestamp.valueOf(userAttributes.get("lastLogin").toString());
            try {
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM message WHERE toUserID = ?" +
                        "AND timeSent > ?");

                stmt.setInt(1, userID);
                stmt.setTimestamp(2, lastLogin);
                ResultSet rs = stmt.executeQuery();

                // Loop through the result set and format each message for display
                while (rs.next()) {
                    int msgID = rs.getInt("msgID");
                    int fromID = rs.getInt("fromID");
                    int groupIDMaybe = rs.getInt("toGroupID");
                    Timestamp timestamp = rs.getTimestamp("timeSent");
                    String formattedTimestamp = timestamp.toLocalDateTime().toString();
                    String message = rs.getString("messageBody");

                    System.out.printf("[%15s]\n", String.format("Message ID: %s", msgID));
                    System.out.printf("[%25s]\n", String.format("From User: %s", getNameByUserID(fromID)));
                    System.out.printf("[%20s]\n", String.format("Time Sent: %s", formattedTimestamp));
                    if (groupIDMaybe != 0) System.out.printf("[%20s]\n", String.format("Sent To Group: %s", getGroupNameByID(groupIDMaybe)));
                    System.out.printf("[%230s]\n", String.format("%30s sent: \n %200s", getNameByUserID(fromID), message));
                }

                // Print the footer for the message list
                System.out.println("------------------------------------------------------------------------------------");
                System.out.println("Total messages: " + rs.getRow());

                // Return the number of messages displayed
                return rs.getRow();
            } catch(SQLException e) {
                e.printStackTrace();
                return -1;
            }
    }

    public int displayFriends() {
        Scanner choices = new Scanner(System.in);
            boolean done = false;

            while (!done) {
                // Display the list of user's friends
                System.out.println("Your friends:");;
                try {
                    PreparedStatement stmt = connection.prepareStatement(
                            "SELECT userID, name FROM profile WHERE userID IN (WHERE userID1 = ? OR userID2 = ?)"
                    );
                    stmt.setInt(1, userID);
                    stmt.setInt(2, userID);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        int friendID = rs.getInt("userID");
                        String name = rs.getString("name");
                        System.out.printf("%d. %s (userID=%d)\n", rs.getRow(), name, friendID);
                    }
                    System.out.println("0. Back to main menu");
                } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
                    return -1;
                }

                // Ask the user to select a friend or go back
                System.out.print("Enter friend's userID (or 0 to go back): ");
                int friendID = choices.nextInt();

                if (friendID == 0) {
                    done = true;
                } else {
                    // Display friend's profile
                    try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM profiles WHERE userID = ?")) {
                        stmt.setInt(1, friendID);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            String name = rs.getString("name");
                            String email = rs.getString("email");
                            String dob = rs.getString("dateOfBirth");
                            String active = rs.getString("lastLogin");
                            System.out.printf("Name: %s\nEmail: %s\nDOB: %s\nLast Active: %s\n",
                                    name, email, dob, active);
                        } else {
                            System.out.println("Error: friend not found");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                        return -1;
                    }
                }
            }
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
        try{
            connection.close();
        }
        catch(SQLException s){
            System.out.println("error closing connection");
        }
        return 1;
    }

    private int getGroupIDByName(String groupName) {
        /**
         * This function retrieves the groupID corresponding to the given group from the 'groupInfo' table.
         *
         * @param groupName the name of the group whose ID needs to be retrieved
         * @return the ID of the group with the given email, or -1 if the name is not found in the 'groupInfo' table
         */
        int groupID = -1;
        try {
            // Prepare the SELECT statement
            String selectQuery = "SELECT gID FROM groupInfo WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, groupName);

            // Execute the SELECT statement
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                groupID = resultSet.getInt("gID");
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error retrieving groupID: " + e.getMessage());
        }
        return groupID;
    }


    private String getNameByEmail(String userEmail) {
        /**
         * This function retrieves the user name corresponding to the given email from the 'profile' table.
         *
         * @param userEmail the email of the user whose name needs to be retrieved
         * @return the name of the user with the given email, or null if the email is not found in the 'profile' table
         */
        String name;
        try {
            // Prepare the SELECT statement
            String selectQuery = "SELECT name FROM profile WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, userEmail);

            // Execute the SELECT statement
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("name");
            } else {
                return null;
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error retrieving user name: " + e.getMessage());
            return null;
        }
        return name;
    }

    private String getNameByUserID(int userID) {
        /**
         * This function retrieves the user name corresponding to the given ID from the 'profile' table.
         *
         * @param userID the ID of the user whose name needs to be retrieved
         * @return the name of the user with the given ID, or null if the email is not found in the 'profile' table
         */
        String name;
        try {
            // Prepare the SELECT statement
            String selectQuery = "SELECT name FROM profile WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userID);

            // Execute the SELECT statement
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("name");
            } else {
                return null;
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error retrieving user name: " + e.getMessage());
            return null;
        }
        return name;
    }

    private boolean areFriends(int friendID) {
        /**
         * This function checks whether two users are already friends.
         *
         * @param friendID the ID of the second user
         * @return true if the two users are already friends, false otherwise
         */
        try {
            // Prepare the SELECT statement
            String selectQuery = "SELECT * FROM friend WHERE (userID1 = ? AND userID2 = ?) OR (userID1 = ? AND userID2 = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, friendID);
            preparedStatement.setInt(3, friendID);
            preparedStatement.setInt(4, userID);

            // Execute the SELECT statement
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error checking if users are friends: " + e.getMessage());
        }
        return false;
    }

    private boolean alreadyFollowed(int friendID) {
        /**
         * Checks if the current user already follows target user
         *
         * @param friendEmail the email of the user who was followed.
         * @return true if there is a pending friend request, false otherwise.
         */
        try {
            // Prepare SQL statement to check if curr user follows friend
            String sql = "SELECT * FROM pendingFriend WHERE fromID = ? AND toID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userID);
                stmt.setInt(2, friendID);

                try (ResultSet rs = stmt.executeQuery()) {
                    // If result set has any rows, user already followed
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking for pending friend request: " + e.getMessage());
            return false;
        }

        
    }

    private ArrayList<String> throwInList(ResultSet set) {
        ArrayList<String> tempList = new ArrayList();
        try {
            while (set.next()) {
                tempList.add(set.getRow(), set.toString());
            }
        } catch (SQLException e) {
            System.out.println("Error Getting Follower");
        }
        return tempList;
    }

    private int getUserIDByEmail(String userEmail) {
        /**
         * This function retrieves the user ID corresponding to the given email from the 'profile' table.
         *
         * @param userEmail the email of the user whose ID needs to be retrieved
         * @return the ID of the user with the given email, or -1 if the email is not found in the 'profile' table
         */
        int userID = -1;
        try {
            // Prepare the SELECT statement
            String selectQuery = "SELECT userID FROM profile WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, userEmail);

            // Execute the SELECT statement
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userID = resultSet.getInt("userID");
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error retrieving userID: " + e.getMessage());
        }
        return userID;
    }

    private String getGroupNameByID(int groupID) {
        /**
         * This function retrieves the groupName corresponding to the given group from the 'groupInfo' table.
         *
         * @param groupID the ID of the group whose name needs to be retrieved
         * @return the name of the group with the given ID, or null if the ID is not found in the 'groupInfo' table
         */
        String groupName = null;
        try {
            // Prepare the SELECT statement
            String selectQuery = "SELECT gID FROM groupInfo WHERE gID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, groupID);

            // Execute the SELECT statement
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                groupName = resultSet.getString("name");
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error retrieving groupID: " + e.getMessage());
        }
        return groupName;
    }
}