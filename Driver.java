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

    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        BeSocial driver = new BeSocial();

        driver.login("admin@besocial.com", "admin");
        
        for(int i = 0; i < 100; i++)
        {
            driver.createProfile("test", "test214"+i+"@gmail.com", "21401417abAQW", Date.valueOf("2000-01-02"));
        }
        driver.displayTable("profile");
        for(int i = 0; i < 100; i++)
        {
            driver.dropProfile("test214"+i+"@gmail.com", "yes");
        }
        //driver.displayTable("profile");
        driver.logout();
        driver.login("GautamJain@gmail.com", "pDPECU*6");
        driver.createGroup("driverGroup", "test", 5);
        //driver.displayTable("groupInfo");   
        driver.createGroup("driverGroup2", "test", 5);
        //driver.displayTable("groupInfo");
        driver.initiateAddingGroup(10, "test");
        //driver.displayTable("pendingGroupMember");
        driver.confirmGroupMembership("10");
        //driver.displayTable("groupMember");
        driver.leaveGroup("driverGroup");
        //driver.displayTable("leaveGroup");
        driver.searchForProfile("NavinMahal@gmail.com");
        for(int i = 0; i<10;i++)
        {
            driver.sendMessageToUser("NavinMahal@gmail.com", "message " + i);
        }
        //driver.displayTable("message");
        driver.sendMessageToGroup("driverGroup2","text");
        //driver.displayTable("message");
        driver.displayMessages();
        driver.displayNewMessages();
        driver.displayFriends(43);
        driver.rankGroups();
        driver.rankProfiles();
        driver.topMessages(5,10);
        driver.threeDegrees(57);
        driver.logout();

        
    }

    
}
