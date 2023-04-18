import java.util.*;
import java.sql.*;
import java.time.*;
import java.lang.*;



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

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        BeSocial beSocial = new BeSocial();
        Scanner kbd = new Scanner(System.in);
        ArrayList<String> validate = new ArrayList<String>();
        String input = "";
        Boolean exit = false;
        int topLevel = 0;
        int bottomLevel = 0;
        String userID = null;
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
            else if(topLevel == 2 && bottomLevel == 0) //LOGIN
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
