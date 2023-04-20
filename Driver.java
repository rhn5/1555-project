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
                while(success == -1)
                {
                    System.out.println("<-----FAILED TO CREATE USER PROFILE----->");
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
                
                /**
                 * Continuously prompts the user for their email and password until a valid loggedIn is returned from the beSocial.login method.
                 * If the loggedIn is -1, the user is informed that their username or password is incorrect and prompted again.
                 */
                while(loggedIn == -1)
                {
                    System.out.println("<-----INCORRECT USERNAME/PASSWORD----->");
                    System.out.print("Email: ");
                    userEmail = kbd.next();
                    System.out.print("Password: ");
                    userPass = kbd.next();
                    loggedIn = beSocial.login(userEmail, userPass);
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
}
