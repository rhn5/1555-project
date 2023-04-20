/**
 * This class represents the main driver for the BeSocial application. It allows users to create profiles, login, and perform various actions such as initiating friendships, creating groups, and sending messages. 
 * 
 * The validateInputs method checks if the user input contains any SQL injection keywords such as "update", "delete", "select", or "drop". 
 * 
 * The main method initializes the BeSocial object and prompts the user to either create a profile, login, or exit. Once the user logs in, they can perform various actions by selecting a number corresponding to the desired action. 
 * 
 * @throws SQLException if there is an error with the SQL database
 * @throws ClassNotFoundException if the class is not found
 * @
 */
import java.util.*;
//import java.util.Date;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Driver {

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
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        BeSocial driver = new BeSocial();

        //drop the profile for driver@gmail.com if it already exists. If not, fails
        driver.dropProfile("driver@gmail.com");

        //creating user profile for driver@gmail.com
        driver.createProfile("test","driver@gmail.com" , "Datadata123", Date.valueOf("2000-1-1"));
        //showing that if run again it fails
        driver.createProfile("test","driver@gmail.com" , "Datadata123", Date.valueOf("2000-1-1"));

        //drop the profile for driver@gmail.com, should be successful
        driver.dropProfile("driver@gmail.com");

        //creating user profile for driver@gmail.com again
        driver.createProfile("test","driver@gmail.com" , "Datadata123", Date.valueOf("2000-1-1"));

        //login the driver
        driver.login("driver@gmail.com", "Datadata123");

        //send friend request to userID 10
        driver.initiateFriendship(10);

        //Group Creation
        driver.createGroup("driverGroup", "Driver Group", 10);

        //User request to join groupID = 1
        driver.initiateAddingGroup(1, "You have been invited to join the group");

        //Driver leaves driverGroup
        driver.leaveGroup("driverGroup");

        //testing search Profile
        driver.searchForProfile("a");

        //sending a message
        String message = "Ping";
        driver.sendMessageToUser("NishaParmar@gmail.com", message);
        driver.sendMessageToUser("NishaParmar@gmail.com", message);
        driver.sendMessageToUser("NishaParmar@gmail.com", message);
        driver.sendMessageToUser("NishaParmar@gmail.com", message);
        driver.sendMessageToUser("NishaParmar@gmail.com", message);

        //Group Creation
        driver.createGroup("driverGroup2", "Driver Group2", 10);
        
        //Testing message to group
        driver.sendMessageToGroup("driverGroup2");

        //Display Messages
        driver.displayMessages();

        //Display New Messages
        driver.displayNewMessages();

        //Test Rank Groups
        driver.rankGroups();

        //Test Rank Profiles
        driver.rankProfiles();

        //Testing top Messages
        driver.topMessages();

        //Test Three Degrees
        driver.threeDegrees();

        //Test Logout
        driver.logout();

        //Test Application exit
        driver.exit();
    }

    
}
