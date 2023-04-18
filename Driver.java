import java.util.*;
import java.sql.*;
import java.time.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Driver {
    
    public static boolean validateInputs(ArrayList<String> s) {
		boolean result = true;
		for(String entry : s) {
			entry = entry.toLowerCase();
			if (entry.contains("update") || entry.contains("delete") || entry.contains("select") || entry.contains("drop") ) {
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
        String userID = "";
        
        try
        {
            BufferedReader logo = new BufferedReader(new FileReader("logo.txt"));
            while(logo.ready())
            {
                System.out.println(logo.readLine());
            }
        }
        catch(Exception e)
        {

        }

        
        while(!exit)
        {
            /**
             * Displays the main menu options for the user to select from.
             * If the user enters an invalid operation, they will be prompted to enter a valid one.
             */
            if(topLevel == 0 && bottomLevel == 0) //TOP LEVEL MENU
            {
                System.out.println("<-----LOGIN/REGISTER----->");
                System.out.println("1. Create Profile");
                System.out.println("2. Login");
                System.out.println("3. Delete Profile");
                System.out.print("Input:");

                topLevel = Integer.parseInt(kbd.next());
                while(topLevel <= 0)
                {
                    System.out.println("PLEASE ENTER A VALID OPERATION:");
                    topLevel = Integer.parseInt(kbd.next());
                }
            }
            else if(topLevel == 1 && bottomLevel == 0) //CREATING A USER PROFILE
            {
                System.out.println("<-----CREATE PROFILE----->");
                System.out.print("Name: ");
                String name = kbd.next();
                System.out.print("Email: ");
                String userEmail = kbd.next();
                System.out.print("Password: ");
                String userPass = kbd.next();
                System.out.print("DOB: ");
                String dob = kbd.next();

                topLevel = 0; //PUSH USER BACK TO TOP LEVEL MENU

            }
            else if(topLevel == 2 && !userID.equals("")) //LOGIN
            {
                System.out.println("<-----CREATE PROFILE----->");
                System.out.print("Email: ");
                String userEmail = kbd.next();
                System.out.print("Password: ");
                String userPass = kbd.next();
                //LOG IN USER AND SET USERID TO USERID
                
            }
            else if(topLevel == 3 && bottomLevel == 0)
            {
                System.out.println("<-----DELETE PROFILE----->");
                System.out.print("Email: ");
                String userEmail = kbd.next();
            }
            if(!userID.equals(""))
            {
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
                System.out.print("Input:");
            }
            if(bottomLevel == 1)
            {

            }
            if(bottomLevel == 2)
            {
                
            }
            if(bottomLevel == 3)
            {
                
            }
            if(bottomLevel == 4)
            {
                
            }
            if(bottomLevel == 5)
            {
                
            }
            if(bottomLevel == 6)
            {
                
            }
            if(bottomLevel == 7)
            {
                
            }
            
        }
        
    }
}
