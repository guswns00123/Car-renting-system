
import java.sql.*;
import java.util.Scanner;


import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.File;
import java.io.IOException;
public class MainFunctions {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db14"; //check thid 
		String dbUsername = "Group14";
		String dbPassword = "Group14JAJ";

		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
		} catch (ClassNotFoundException e) {
			System.out.println("[Error]: Java MySQL DB Driver not found!!");
			System.exit(0);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		Scanner keyboard = new Scanner(System.in);
		int option = 0;
		System.out.println("Welcome to Car Renting System!\n");
		while (true) {
			System.out.println("-----Main menu-----");
			System.out.println("What kinds of operations would you like to perform?");
			System.out.println("1. Operations for Administrator");
			System.out.println("2. Operations for User");
			System.out.println("3. Operations for Manager");
			System.out.println("4. Exit this program");
			System.out.print("Enter your choice: ");

			option = keyboard.nextInt();

			switch (option) {
			case 1:
				AdministratorFunctions adminFunc = new AdministratorFunctions(conn);
				adminFunc.ShowAdministratorFunctions();
				break;
			case 2:
				UserFunctions userFunc = new UserFunctions(conn);
				userFunc.ShowUserFunctions();
				break;
			//case 3:
			//	ManagerFunctions managerFunc = new ManagerFunctions(conn);
			//	managerFunc.ShowManagerFunctions();
			//	break;
			case 4:
				System.out.println("Thankyou bye!");
				System.exit(0);
				break;
			default:
			System.out.println("Invalid Choice! Please try again.\n");
			}
		}

	}

}







