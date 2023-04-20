import java.util.Scanner;
import javax.naming.spi.DirStateFactory.Result;
import java.sql.*;
import java.lang.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


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
                
            }
        }
        catch(SQLException s){
            return -1;
        }
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
        userID = -1;
        
        return 1;
    }

    public int exit() {
        return 1;
    }

}