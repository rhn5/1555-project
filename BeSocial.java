import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.*;
import java.util.*;

public class BeSocial {
    public static boolean validateInputs(ArrayList<String> s) {
        boolean result = true;
        for (String entry : s) {
            entry = entry.toLowerCase();
            if (entry.contains("update") || entry.contains("delete") || entry.contains("select")
                    || entry.contains("drop")) {
                result = false;
            }
        }
        return result;
    }
    private Connection connection;
    private Statement statement;
    private String url;
    private String username;
    private String password;
    private Scanner scan = new Scanner(System.in);
    private Scanner choices = new Scanner(System.in);
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
                System.out.print("Account to Delete: ");
                String deleteProfile = kbd.next();
                // Prompt user to confirm deletion
                System.out.println("Are you sure you want to delete your profile? Enter 'yes' to confirm");
                String confirm = kbd.next();
                beSocial.dropProfile(deleteProfile,confirm);
                topLevel = 0;
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
                System.out.println("BOTTOM LEVEL = "+ bottomLevel);
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
                System.out.println("<-----GROUP MEMBERSHIP CONFIRMATION----->");
                int success = beSocial.confirmGroupMembership("-1");
                if(success == -1)
                {
                    System.out.println("<-----GROUP CONFIRMATION FAILED----->");
                }
            }
            if (bottomLevel == 6) {
                System.out.println("<-----LEAVE GROUP----->");
                System.out.println("Enter group name:");
                String groupName = kbd.next();
                int success = beSocial.leaveGroup(groupName);
                if(success == -1)
                {
                    System.out.println("<-----GROUP LEAVE FAILED----->");
                }
            }
            if (bottomLevel == 7) {
                System.out.println("<-----PROFILE SEARCH----->");
                System.out.println("Enter name to search: ");
                kbd.nextLine();
                String name = kbd.nextLine();
                int success = beSocial.searchForProfile(name);
                if(success == -1)
                {
                    System.out.println("<-----PROFILE SEARCH FAILED----->");
                }
            }
            if (bottomLevel == 8) {
                System.out.println("<-----SEND MESSAGE TO USER----->");
                System.out.println("Sending Message To (email): ");
                String toUser = kbd.next();
                System.out.println("Message: ");
                String message = kbd.next();
                int success = beSocial.sendMessageToUser(toUser,message);
                if(success == -1)
                {
                    System.out.println("<-----MESSAGE SEND FAILED----->");
                }
            }
            if (bottomLevel == 9) {
                System.out.println("<-----SEND MESSAGE TO GROUP----->");
                System.out.println("Enter group name");
                String groupName = kbd.next();
                System.out.println("Enter Message:");
                String groupMsg = kbd.next();
                int success = beSocial.sendMessageToGroup(groupName, groupMsg);
                if(success == -1)
                {
                    System.out.println("<-----MESSAGE SEND FAILED----->");
                }
            }
            if (bottomLevel == 10) {
                System.out.println("<-----MESSAGES----->");
                int success = beSocial.displayMessages();
                if(success == -1)
                {
                    System.out.println("<-----MESSAGE DISPLAY FAILED----->");
                }
            }
            if (bottomLevel == 11) {
                System.out.println("<-----NEW MESSAGES----->");
                int success = beSocial.displayNewMessages();
                if(success == -1)
                {
                    System.out.println("<-----MESSAGE DISPLAY FAILED----->");
                }
            }
            if (bottomLevel == 12) {
                System.out.println("<-----YOUR FRIENDS----->");
                int success = beSocial.displayFriends(-1);
                if(success == -1)
                {
                    System.out.println("<-----MESSAGE DISPLAY FAILED----->");
                }
            }
            if (bottomLevel == 13) {
                System.out.println("<-----TOP 10 GROUPS----->");
                int success = beSocial.rankGroups();
                if(success == -1)
                {
                    System.out.println("<-----GROUP RANKING FAILED----->");
                }
            }
            if (bottomLevel == 14) {
                System.out.println("<-----TOP 10 PROFILES----->");
                int success = beSocial.rankProfiles();
                if(success == -1)
                {
                    System.out.println("<-----PROFILE RANKING FAILED----->");
                }
            }
            if (bottomLevel == 15) {
                System.out.println("<-----TOP MESSAGES----->");
                System.out.println("T0P NUMBER OF MESSAGES: ");
                int messageCNT = kbd.nextInt();
                System.out.println("X MONTHS: ");
                int monthCNT = kbd.nextInt();
                int success = beSocial.topMessages(messageCNT, monthCNT);
            }
            if (bottomLevel == 16) {
                System.out.println("Enter user ID to find three degrees:");
                int id= kbd.nextInt();
                int success = beSocial.threeDegrees(id);
                if(success == -1)
                {
                    System.out.println("<-----THIRD DEGREE FIND FAILED----->");
                }
            }
            if(bottomLevel == 17 ){
                loggedIn = -1;
                topLevel = 0;
                bottomLevel = 0;
                int success = beSocial.logout();
                if(success == -1)
                {
                    System.out.println("<-----LOGOUT FAILED----->");
                }
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
                clockTime = rs.getTimestamp("pseudoTime");
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

    public boolean dropProfile(String email, String confirm) {
        try {
            if (!confirm.equals("yes")) {
                System.out.println("Deletion not confirmed. Profile was not deleted.");
                return false;
            }

            // Delete profile from 'profile' table
            PreparedStatement deleteProfileStatement = connection
                    .prepareStatement("DELETE FROM profile WHERE userID = ?");
            deleteProfileStatement.setInt(1, userID);
            deleteProfileStatement.executeUpdate();
            deleteProfileStatement.close();

            PreparedStatement deleteMessageStatemnt = connection
                    .prepareStatement("DELETE FROM message WHERE fromID = ?");
            deleteMessageStatemnt.setInt(1, userID);
            deleteMessageStatemnt.executeUpdate();
            deleteMessageStatemnt.close();

            System.out.println("Profile deleted successfully.");
        } catch (SQLException e) {
            System.out.println("An error occurred while deleting profile: " + e.getMessage());
            System.out.println("Please Try Deleting Again. ");
            return false;
        }
        return true;
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

    public int confirmGroupMembership(String lineNum) {

        //find out if user is a manager
        String query = "SELECT gID FROM groupMember WHERE role = 'manager' userID=" + userID;
        int gID = 0;
        ArrayList<Integer> gidList= new ArrayList<>();
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
            Statement st = null;
            ResultSet rs = null;
            try{
                st = connection.createStatement();
                rs = st.executeQuery(query);
                if(rs.next()){
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
                System.out.println("error executing query");
                System.out.println("Error message: " + s.getMessage());
                System.out.println("SQL state: " + s.getSQLState());
                System.out.println("Error code: " + s.getErrorCode());
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
        String result = "";
        if(!lineNum.equals("-1")){
            result = lineNum;
        }
        else{
            result = scan.next();

        }
        //String result = scan.next();
        if(result.equals("all")){
            String insert = "INSERT INTO groupMember VALUES (?, ?, ?, ?)";
            for(int i = 0; i < gidList.size(); i ++){
                try{
                    PreparedStatement st = connection.prepareStatement(insert);
                    st.setInt(1, gidList.get(i));
                    st.setInt(2, uidList.get(i));
                    st.setString(3, "Member");
                    st.setTimestamp(4, clockTime);

                }
                catch(SQLException s){
                    System.out.println("error executing query");
                    System.out.println("Error message: " + s.getMessage());
                    System.out.println("SQL state: " + s.getSQLState());
                    System.out.println("Error code: " + s.getErrorCode());
                    return -1;
                }
            }
        }
        else{
            String insert = "INSERT INTO groupMember VALUES (?, ?, ?, ?)";

            int temp = Integer.parseInt(result);
            try{
                PreparedStatement st = connection.prepareStatement(insert);
                st.setInt(1, gidList.get(temp));
                st.setInt(2, uidList.get(temp));
                st.setString(3, "Member");
                st.setTimestamp(4, clockTime);
            }
            catch(SQLException s){
                System.out.println("error executing query");
                System.out.println("Error message: " + s.getMessage());
                System.out.println("SQL state: " + s.getSQLState());
                System.out.println("Error code: " + s.getErrorCode());
                return -1;
            }
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
        String query = "SELECT * FROM profile WHERE ";
        for(int i = 0; i < words.length; i ++){
            query += " name LIKE ? OR email LIKE ?";
            if(i != words.length -1){
                query +=  " AND ";
            }
        }
        try{
            PreparedStatement st = connection.prepareStatement(query);
            int c = 1;
            for(int i = 0 ; i < words.length; i ++){
                st.setString(c, "%"+words[i]+"%");
                c++;
                st.setString(c, "%"+words[i]+"%");
                c++;
            }

            ResultSet rs = st.executeQuery();
            int count = 0;
            while(rs.next()){
                int id = rs.getInt("userID");
                String name = rs.getString("name");
                String email = rs.getString("email");
                System.out.println(count +" name = " + name + " email = " + "id = " + userID);
                count++;
            }
            st.close();
        }
        catch(SQLException s){
            System.out.println("error executing query");
            System.out.println("Error message: " + s.getMessage());
            System.out.println("SQL state: " + s.getSQLState());
            System.out.println("Error code: " + s.getErrorCode());
            return -1;
        }
        //st.close();
        return 1;
    }

    public int sendMessageToUser(String toUserEmail, String msgBody) {

        try {
            PreparedStatement message = connection.prepareStatement("INSERT INTO message VALUES(?, ?, ?, ?, ?, ?)");
            String userName = getNameByEmail(toUserEmail);
            System.out.println(userName);
            int toID = getUserIDByEmail(toUserEmail);
            System.out.println(toID);
            int msgID;

            String count = "SELECT COUNT(*) FROM message";
            Statement countStatement = connection.createStatement();
            ResultSet rs = countStatement.executeQuery(count);
            msgID = -1;
            if (rs.next()) {
                msgID = rs.getInt("count") ;
            }


            System.out.println("Sending Message To: " + userName);

            if (msgBody.length() > 200 || msgBody.isBlank())
            {
                System.out.println("Error Sending Message (Too many chars or is blank)");
                return -1;
            }
            message.setInt(1, msgID);
            message.setInt(2, userID);
            message.setString(3, msgBody);
            message.setInt(4, toID);
            message.setInt(5, Types.NULL);
            message.setTimestamp(6, Timestamp.from(java.time.Instant.now()));
            message.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error Sending Message");
            System.out.println("Error message: " + e.getMessage());
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Error code: " + e.getErrorCode());
            return -1;
        }
        return 1;
    }

    public int sendMessageToGroup(String toGroupName, String groupMessage) {
        try {
            PreparedStatement message = connection.prepareStatement("INSERT INTO message VALUES(?, ?, ?, ?, ?, ?)");
            int groupID = getGroupIDByName(toGroupName);
            int msgID;

            String count = "SELECT COUNT(*) FROM message";
            Statement countStatement = connection.createStatement();
            ResultSet rs = countStatement.executeQuery(count);
            msgID = -1;
            if (rs.next()) {
                msgID = rs.getInt("count") ;
            }

            System.out.println("Sending Message To: " + toGroupName + " GroupID: " + groupID);
            if (groupMessage.length() > 200 || groupMessage.isBlank())
            {
                System.out.println("Error Sending Message (Too many chars or is blank)");
                return -1;
            }
            message.setInt(1, msgID);
            message.setInt(2, userID);
            message.setString(3, groupMessage);
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
        Timestamp lastLogin = clockTime;
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

    public int displayFriends(int friendNo) {
        Scanner choices = new Scanner(System.in);
        int friendID = 0;
        while (true) {
            // Display the list of user's friends
            System.out.println("Your friends:");;
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT userID2 FROM friend WHERE userID1 = ?"
                );
                stmt.setInt(1, userID);
                //stmt.setInt(2, userID);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    friendID= rs.getInt("userID2");
                    PreparedStatement stmt2 = connection.prepareStatement("Select name from profile where userID=?");
                    stmt2.setInt(1,friendID);
                    ResultSet rs2 = stmt2.executeQuery();
                    String friendName = "";
                    while(rs2.next()){
                        friendName = rs2.getString("name");
                        System.out.printf("%d. %s (userID=%d)\n", rs.getRow(), friendName, friendID);
                    }

                    //int friendID = rs.getInt("userID");
                }
                System.out.println("0. Back to main menu");
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("SQL state: " + e.getSQLState());
                System.out.println("Error code: " + e.getErrorCode());
                return -1;
            }

            // Ask the user to select a friend or go back
            System.out.print("Enter friend's userID (or 0 to go back): ");
            if(friendNo == -1)
            {
                friendID = choices.nextInt();
            }
            else
            {
                friendID = friendNo;
            }

            if (friendID == 0) {
                break;
            } else {
                // Display friend's profile
                try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM profile WHERE userID = ?")) {
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
                        return -1;
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
        try {
            // Execute the query to retrieve the group rankings
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT groupInfo.name as name, COUNT(groupMember.userID) AS numMembers FROM groupInfo " +
                    "LEFT JOIN groupMember ON groupInfo.gID = groupMember.gID GROUP BY groupInfo.gID, groupInfo.name ORDER BY numMembers DESC");

            // Check if there are any results
            if (!rs.next()) {
                System.out.println("No Groups to Rank");
                return -1;
            }

            // Print the group rankings
            System.out.println("Group Rankings:");
            int rank = 1;
            do {
                String groupName = rs.getString("name");
                int numMembers = rs.getInt("numMembers");
                System.out.println(rank + ". " + groupName + " (" + numMembers + " members)");
                rank++;
            } while (rs.next());

            // Close the statement and return success
            stmt.close();
            return 1;
        } catch (SQLException e) {
            // Handle any errors that may occur
            e.printStackTrace();
            return -1;
        }
    }

    public int rankProfiles() {
        try {
            // Create a statement object
            Statement stmt = connection.createStatement();

            // Execute a query to get the number of friends for each profile
            ResultSet rs = stmt.executeQuery("SELECT p.userID, p.name, COUNT(DISTINCT f.userID2) + " +
                    "COUNT(DISTINCT gm.userID) AS numFriends FROM profile p LEFT JOIN friend f ON p.userID" +
                    " = f.userID1 LEFT JOIN groupMember gm ON p.userID " +
                    "= gm.userID GROUP BY p.userID ORDER BY numFriends DESC;");

            // Print the results
            int rank = 1;
            while (rs.next()) {
                int userID = rs.getInt("userID");
                int numFriends = rs.getInt("numFriends");
                System.out.println(rank + ". UserID: " + userID + ", Number of Friends: " + numFriends);
                rank++;
            }

            // Close the statement and result set
            rs.close();
            stmt.close();

            return 0;
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            return -1;
        }
    }

    public int topMessages(int k, int x) {
        try {
            // Get the current date from the Clock table
            PreparedStatement getCurrentDate = connection.prepareStatement("SELECT pseudoTime FROM Clock");
            ResultSet currentDateResult = getCurrentDate.executeQuery();
            currentDateResult.next();
            Timestamp currentDate = currentDateResult.getTimestamp("pseudoTime");

            // Calculate the date x months ago
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.MONTH, -x);
            //Date xMonthsAgo = (Date)calendar.getTime();
            Timestamp time = new Timestamp(calendar.getTimeInMillis());

            // Get the messages sent to or received from the logged-in user in the past x months
            PreparedStatement getMessages = connection.prepareStatement(
                    "SELECT fromID, toUserID FROM message " +
                            "WHERE fromID = ? OR toUserID = ? " +
                            "AND timesent >= ?"
            );
            getMessages.setInt(1, this.userID);
            getMessages.setInt(2, this.userID);
            getMessages.setTimestamp(3, time);
            ResultSet messagesResult = getMessages.executeQuery();

            // Count the number of messages sent to or received from each user
            Map<Integer, Integer> messageCount = new HashMap<>();
            while (messagesResult.next()) {
                int fromID = messagesResult.getInt("fromID");
                int toUserID = messagesResult.getInt("toUserID");
                int otherUserID = (fromID == this.userID) ? fromID : toUserID;
                if (messageCount.containsKey(otherUserID)) {
                    messageCount.put(otherUserID, messageCount.get(otherUserID) + 1);
                } else {
                    messageCount.put(otherUserID, 1);
                }
            }

            // Sort the users by the number of messages sent to or received from them
            List<Integer> sortedUserIDs = new ArrayList<>(messageCount.keySet());
            sortedUserIDs.sort((userID1, userID2) -> messageCount.get(userID2) - messageCount.get(userID1));

            // Display the top k users
            for (int i = 0; i < k && i < sortedUserIDs.size(); i++) {
                int userID = sortedUserIDs.get(i);
                int count = messageCount.get(userID);
                System.out.println("User " + userID + ": " + count + " messages");
            }

            // Close the database connection
            //connection.close();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int threeDegrees(int userFriendID) {
        Queue<Integer> q = new LinkedList<>();
        HashMap<Integer, Integer> visited = new HashMap<>();
        q.add(this.userID);
        visited.put(this.userID, 0);

        while (!q.isEmpty()) {
            int currUser = q.poll();
            int currDepth = visited.get(currUser);

            if (currDepth > 3) {
                break;
            }

            try {
                PreparedStatement stmt = connection.prepareStatement("SELECT userID2 FROM friend WHERE userID1 = ?");

                stmt.setInt(1, currUser);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    if (currDepth > 3) {
                        break;
                    }
                    int nextUser = rs.getInt("userID2");

                    if (!visited.containsKey(nextUser)) {
                        q.add(nextUser);
                        visited.put(nextUser, currDepth + 1);
                    }

                    if (nextUser == userFriendID) {
                        System.out.println("Path: " + getNameByUserID(this.userID) + " -> " + getNameByUserID(currUser) + " -> " + getNameByUserID(nextUser));
                        return -1;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("No path found.");
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
