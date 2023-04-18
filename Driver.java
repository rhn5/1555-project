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
        switch(input)
        {
            case "":
        }
    }
}
