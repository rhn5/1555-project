import java.sql.Date;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BeSocial {

    private Connection connection;
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

    public userInstance createProfile(String name, String email, String password, String dateOfBirth) {
        String count_query = "SELECT COUNT(*) FROM profiles";
        java.sql.Date dateOfBirthSQL;
        PreparedStatement ps = null;
        ResultSet rs;
        int userID;

        try {
            ps = connection.prepareStatement(count_query);
            rs = ps.executeQuery();
            userID = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error Connecting To Database..Please Try Again Later");
            return null;
        }

        try {
            SimpleDateFormat dFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateOfBirthSQL = (java.sql.Date) (dFormat.parse(dateOfBirth));
        } catch (ParseException e) {
            System.out.println("BeSocial Error, Please Try Again Later...");
            return null;
        }

        try {
            String query = "INSERT INTO profiles (userID, name, email, password, dateOfBirth) "
                         + "VALUES (?, ?, ?, ?, ?)";

            ps = connection.prepareStatement(query);

            ps.setInt(1, userID);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setDate(5, dateOfBirthSQL);

            rs = ps.executeQuery();


            userInstance o;
            if (!rs.next()) {return null;}

        } catch (SQLException e){
            System.out.println("Error In User Data. Please Check Your Inputs... ");
            System.out.printf("Error Message: %s\n", e.getMessage());
            return null;
        }

        return new userInstance(userID);
    }

    public userInstance login(String email, String password) {
        String query = "SELECT userID FROM profiles WHERE email=? AND password=?";
        PreparedStatement st = null;
        ResultSet rs;

        try {
            st = connection.prepareStatement(query);
            st.setString(1, email);
            st.setString(2, password);

            rs = st.executeQuery(); //Execute Login Query
        } catch (SQLException e) {
            System.out.println("Error With Login Credentials! Please Try Again.");
            return null;
        }

        // Create userInstance linked to userID
        try {
            return new userInstance(rs.getInt("userID"));
        } catch (SQLException e) {
            System.out.println("Error Connecting To BeSocial. Possible Issues On Our End");
            System.out.println("Please Try Again Later.");
            return null;
        }
    }

    public int dropProfile() {
        return 1;
    }
    public void exit() {
        System.out.println("Exited");
        System.exit(0);
    }

    private static BeSocial logout(userInstance loggedOut) {
        loggedOut = null;
        return new BeSocial();
    }


    private class userInstance<T> {
        int userID;
        Map<String, Object> userAttributes;
        Scanner choices;

        private userInstance(int userID) {
            /**
             * Log User In And Pull Profile Info
             *
             * @param: userID. Store userID For Queries
             */
            this.userID = userID;
            this.userAttributes = new HashMap<>();
            try {
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM profile WHERE userID = ?");

                stmt.setInt(1, userID);
                ResultSet rs = stmt.executeQuery();

                // loop through result set and store attribute-value pairs in map
                if (rs.next()) {
                    userAttributes.put("name", rs.getString("name"));
                    userAttributes.put("email", rs.getString("email"));
                    userAttributes.put("password", rs.getString("password"));
                    userAttributes.put("dateOfBirth", rs.getDate("dateOfBirth"));
                    userAttributes.put("lastLogin", rs.getTimestamp("lastLogin"));
                }
            } catch (SQLException ex) {
                System.out.println("Error Pulling Profile Data and Logging You In");
                System.out.println("Please Try Again. Error Message: " + ex.getMessage());
            }
        }

        public boolean dropProfile() {
            /**
             * Drops the row with the current user's userID from the
             * 'profile' table and all references to it in other tables.
             * Prompts the user to confirm the deletion by inputting 'delete [name]'.
             * If confirmed, drops the row and all references.
             *
             * @return True if profile was successfully deleted, False, otherwise
             */
            try {
                // Prompt user to confirm deletion
                System.out.println("Are you sure you want to delete your profile? Enter 'delete [name]' to confirm.");

                choices = new Scanner(System.in);
                String input = choices.nextLine().trim();

                if (!input.equals("delete " + userAttributes.get("name"))) {
                    System.out.println("Deletion not confirmed. Profile was not deleted.");
                    return false;
                }

                // Delete profile from 'profile' table
                PreparedStatement deleteProfileStatement = connection
                        .prepareStatement("DELETE FROM profile WHERE userID = ?");
                deleteProfileStatement.setInt(1, userID);
                deleteProfileStatement.executeUpdate();
                deleteProfileStatement.close();
                System.out.println("Profile deleted successfully.");
            } catch (SQLException e) {
                System.out.println("An error occurred while deleting profile: " + e.getMessage());
                System.out.println("Please Try Deleting Again. ");
                return false;
            }
            return true;
        }

        public boolean followUser(String friendEmail) {
            /**
             * This function will "follow" a user as long as the email belongs to a valid profile
             * If the email does not exist, returns false with a message
             * @param friendEmail - The email of the person the user wishes to follow
             */

            // Get friends userID from email
            int friendID = getUserIDByEmail(friendEmail);

            // Check if the friendship already exists in the 'friends' table
            if (areFriends(friendID)) {
                System.out.println("Error: You Are Already Friends With User:  '" + friendEmail + "'.");
                return false;
            }

            // Check if the friendship request already exists in the 'friend_requests' table
            if (alreadyFollowed(friendID)) {
                System.out.println("Error: You Already Follow User:  '" + friendEmail + "'.");
                return false;
            }

            // Insert a new row into the 'friend_requests' table to initiate the friendship request
            String sql = "INSERT INTO friend_requests (userID, friendID) VALUES (?, ?)";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, userID);
                stmt.setInt(2, friendID);
                stmt.executeUpdate();
                System.out.println("Successfully Followed User: '" + friendEmail + "'.");
                return true;
            } catch (SQLException e) {
                System.out.println("Error: Failed To Follow User:  '" + friendEmail + "'.");
                System.out.println("Reason: " + e.getMessage());
                return false;
            }
        }

        public boolean addOrRemoveFollowers(String friendEmail, Object flag) {
            choices = new Scanner(System.in);
            PreparedStatement getFollowers;
            ArrayList<String> followers;
            PreparedStatement misc;
            int friendID;
            ResultSet rs;
            String email;
            String name;
            int choice;

            System.out.printf("%s\n", ((boolean) flag) ?
                    "Error When Selecting Follower Decision.... Returned To Followers Menu" : " ");

            while (true) {
                try {
                    getFollowers = connection.prepareStatement("SELECT * FROM pendingFriend");
                    followers = throwInList(getFollowers.executeQuery());
                    System.out.printf("| %-7d | %-25s | %-20 |\n", "Index", "Follower Name", "Follower Email");
                    for (Object follower : followers) {
                        String[] followerArray = follower.toString().trim().split(",");
                        System.out.printf("| %-7d | %-25s | %-20s |\n", (int) followers.indexOf(follower),
                                followerArray[0].toString().trim(),
                                followerArray[2].toString().trim());
                    }
                    System.out.print("Choose A Follower [0 to Exit | -1 To Follow Back All] -> ");

                    choice = (choices.nextInt() == 0
                            ? 0
                            : choices.nextInt());

                    String[] follower = followers.get(choice).toString().trim().split(",");
                    name = follower[0];
                    email = follower[2];
                    friendID = getUserIDByEmail(friendEmail);

                    System.out.printf("[*] %s Followed You! Follow Back?");

                    System.out.printf("| %-7s | %-25s | %-20 |\n", "Index", "Follower Name", "Follower Email");
                    System.out.printf("| %-7d | %-25s | %-20s |\n", (int) followers.indexOf(follower),
                            name,
                            email);

                    System.out.println("1. Follow Back\n"
                            + "2. Remove Follower\n"
                            + "3. Ignore\n");
                    System.out.printf("Choose What to Do With Follower [1|2|3] -> ");
                    choice = choices.nextInt();

                    switch (choice) {
                        case 1:
                            misc = connection.prepareStatement("INSERT INTO friend VALUES(?, ?, ?, ?)");

                            String friend_message = " ";
                            do {
                                System.out.print("Send Friendship Message? [Limit 200 chars] -> ");
                                friend_message = choices.next();
                            } while (friend_message.length() > 200 || !friend_message.isBlank());

                            misc.setInt(1, userID);
                            misc.setInt(2, friendID);
                            misc.setTimestamp(3, new Timestamp(java.time.Instant.now().toEpochMilli()));
                            misc.setString(4, friend_message.isBlank()
                                    ? friend_message.trim()
                                    : null);
                            flag = misc.executeQuery().next()
                                    ? System.out.printf("You and %s are now friends!!\n", name)
                                    : addOrRemoveFollowers(friendEmail, true);

                            misc = connection.prepareStatement("DELETE FROM pendingFriend WHERE fromID = ? AND toID = ?");
                            misc.setInt(1, friendID);
                            misc.setInt(2, userID);
                            flag = misc.executeQuery().next()
                                    ? (System.out.printf("User: %s Removed From Followers.", name))
                                    : addOrRemoveFollowers(friendEmail, true);
                            break;

                        case 2:
                            misc = connection.prepareStatement("DELETE FROM pendingFriend WHERE fromID = ? AND toID = ?");
                            misc.setInt(1, friendID);
                            misc.setInt(2, userID);
                            System.out.print("Are you sure you want to remove this person as a follower? [Y|N] -> ");
                            char removeChoice = choices.next().toLowerCase().charAt(0);
                            flag = removeChoice == 'N' ?
                                    addOrRemoveFollowers(friendEmail, false)
                                    : (
                                    misc.executeQuery().next()
                                            ? (System.out.printf("User: %s Removed From Followers.", name))
                                            : addOrRemoveFollowers(friendEmail, true)
                            );

                            break;
                        case 3:
                            addOrRemoveFollowers(friendEmail, false);
                            break;
                        case -1:
                            for (Object followBack : followers) {
                                String[] followerArray = followBack.toString().trim().split(",");
                                System.out.printf("| %-7d | %-25s | %-20s | Followed Back!\n", (int) followers.indexOf(follower),
                                        followerArray[0].toString().trim(),
                                        followerArray[2].toString().trim());
                                friendID = getUserIDByEmail(followerArray[2]);
                                PreparedStatement stmt = connection.prepareStatement(
                                        "INSERT INTO friend VALUES (?, ?, ?, ?)"
                                );
                                stmt.setInt(1, userID);
                                stmt.setInt(2, friendID);
                                stmt.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));
                                stmt.setString(4, "User " + userAttributes.get("name") + " followed you back!");
                                rs = stmt.executeQuery();
                                if (!rs.next()) {
                                    System.out.println("Error Adding " + followerArray[0] + " " + followerArray[2]);
                                    continue;
                                }
                                misc = connection.prepareStatement("DELETE FROM pendingFriend WHERE fromID = ? AND toID = ?");
                                misc.setInt(1, friendID);
                                misc.setInt(2, userID);
                                flag = misc.executeQuery().next()
                                        ? (System.out.printf("User: %s Removed From Followers.", name))
                                        : addOrRemoveFollowers(friendEmail, true);
                                System.out.println(followerArray[0] + " Successfully Added");
                            }
                            break;
                        default:
                            System.out.println("Invalid Option");
                    }

                } catch (SQLException e) {
                    System.out.println("Error Getting Followers. Please Try Again");
                    return false;
                }
            }
        }

        public int createGroup() {
            choices = new Scanner(System.in);
            String name;
            int size;
            String description;
            try {
                int count = 0;
                int gID = 0;
                PreparedStatement groupCreation = connection.prepareStatement("INSERT INTO groupInfo VALUES (?, ?, ?, ?)");
                do {
                    gID = (int) Math.random();
                }
                while (connection.prepareStatement("SELECT COUNT(*) FROM groupInfo WHERE gID = %s", gID)
                        .executeQuery().getInt(1) == 0);
                groupCreation.setInt(1, gID);
                do {
                    System.out.print("Group Name [Up to 50 chars] -> ");
                    name = choices.next();
                    System.out.print("Max Group Size -> ");
                    size = choices.nextInt();
                    System.out.print("Give A Group Description? [200 char max] -> ");
                    description = choices.next();
                } while (name.length() > 50
                        && description.length() > 200);

                groupCreation.setString(2, name);
                groupCreation.setInt(3, size);
                groupCreation.setString(4, description);
            } catch (SQLException e) {

            }
            return 1;
        }

        public boolean initiateAddingGroup() {
            choices = new Scanner(System.in);
            String description;

            while (true) {
                System.out.print("Enter Group Name To Join [Case Sensitive] -> ");
                int groupID = getGroupIDByName(choices.next().trim());
                if (groupID == -1) {
                    System.out.println("Error Retrieving Group Info, Group Name Not Found. Try Again");
                    initiateAddingGroup();
                }
                System.out.println("Send Request Text? [Max 200 chars] -> ");
                description = choices.next();
                if (description.length() > 200) {
                    System.out.println("Too Many Chars. Try Again.");
                    continue;
                }

                try {
                    PreparedStatement joinGroup = connection.prepareStatement("INSERT INTO pendingGroupMemmber VALUES (?, ?, ?, ?)");
                    joinGroup.setInt(1, groupID);
                    joinGroup.setInt(2, userID);
                    joinGroup.setString(3, description);
                    joinGroup.setTimestamp(4, Timestamp.from(java.time.Instant.now()));
                    joinGroup.executeQuery();
                } catch (SQLException e) {
                    System.out.println("Error Sending Group Request, Please Try Again.");
                    continue;
                }
                System.out.println("Group Request Sent!");
                break;
            }
            return true;
        }

        public int confirmGroupMembership() {
            return 1;
        }

        public boolean leaveGroup(String groupName) {
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
                return false;
            }
            return true;
        }

        public int searchForProfile() {
            return 1;
        }

        public boolean sendMessageToUser(String toUserEmail) {
            choices = new Scanner(System.in);
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
                return false;
            }
            return true;
        }

        public boolean sendMessageToGroup(String toGroupName) {
            choices = new Scanner(System.in);
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
                return false;
            }
            return true;
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
            // Create a statement to retrieve all messages sent to the user
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
            choices = new Scanner(System.in);
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

        public boolean rankGroups() {
            try {
                // Execute the query to retrieve the group rankings
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT groupInfo.name as name, COUNT(groupMember.userID) AS numMembers FROM groupInfo " +
                        "LEFT JOIN groupMember ON groupInfo.gID = groupMember.gID GROUP BY groupInfo.gID, groupInfo.name ORDER BY numMembers DESC");
                
                // Check if there are any results
                if (!rs.next()) {
                    System.out.println("No Groups to Rank");
                    return false;
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
                return true;
            } catch (SQLException e) {
                // Handle any errors that may occur
                e.printStackTrace();
                return false;
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
        public void topMessages(int k, int x) {
            try {
                // Get the current date from the Clock table
                PreparedStatement getCurrentDate = connection.prepareStatement("SELECT date FROM Clock");
                ResultSet currentDateResult = getCurrentDate.executeQuery();
                currentDateResult.next();
                Date currentDate = currentDateResult.getDate("date");

                // Calculate the date x months ago
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.MONTH, -x);
                Date xMonthsAgo = (Date) calendar.getTime();

                // Get the messages sent to or received from the logged-in user in the past x months
                PreparedStatement getMessages = connection.prepareStatement(
                        "SELECT fromID, toUserID FROM message " +
                                "WHERE (fromID = ? OR toUserID = ?) " +
                                "AND timestamp >= ?"
                );
                getMessages.setInt(1, this.userID);
                getMessages.setInt(2, this.userID);
                getMessages.setDate(3, xMonthsAgo);
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
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public boolean threeDegrees(int userFriendID) {
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
                            System.out.println("Path: " + this.userID + " -> " + currUser + " -> " + nextUser);
                            return false;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("No path found.");
            return true;
        }

        public BeSocial logout() {
            return BeSocial.logout(this);
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
    }
}