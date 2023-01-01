import java.sql.*;
import java.util.Scanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagerFunctions {

	int option;
	Connection conn;

	public ManagerFunctions(Connection conn) {
		this.option = 0;
		this.conn = conn;
	}

	public void CarRenting(String uid, String callnum, int copynum) {
        try {
            PreparedStatement statement2 = conn.prepareStatement("SELECT * FROM rent WHERE callnum = ? and copynum = ? and return_date IS NOT NULL;");
            statement2.setString(1, callnum);
            statement2.setInt(2, copynum);
            ResultSet rs2 = statement2.executeQuery();
            Boolean CarAvailable = rs2.next();
            statement2.close();

            if (CarAvailable) {
                PreparedStatement statement3 = conn.prepareStatement("INSERT INTO rent (uid,callnum,copynum,checkout,return_date) VALUES (?,?,?,?,NULL);");
                statement3.setString(1, uid);
                statement3.setString(2, callnum);
                statement3.setInt(3, copynum);
                Date a = new Date();
                java.sql.Date new_checkout_date =new java.sql.Date(a.getTime());
                statement3.setDate(4, new_checkout_date);
                statement3.executeUpdate();
                statement3.close();
                System.out.println("car renting performed successfully.\n");
            }
            else {
                System.out.println("car renting performed unsuccessfully, inputted car is currently being rented.\n");
            }
        } catch (SQLException e){
            System.out.println("[Error]: No Matching car copy found.\n");
        }
	}
    
	public void CarReturning(String uid1, String callnum1, int copynum1) {
		try {
            boolean RecordExist = false;

            PreparedStatement statement = conn.prepareStatement("SELECT * FROM rent WHERE uid = ? and callnum = ? and copynum = ? and return_date IS NULL;");
            statement.setString(1, uid1);
            statement.setString(2, callnum1);
            statement.setInt(3, copynum1);
            ResultSet rs = statement.executeQuery();
            RecordExist = rs.next();

            if (RecordExist) {
            PreparedStatement statement1 = conn.prepareStatement("UPDATE rent SET return_date = ? where uid = ? and callnum = ? and copynum = ?;");
            Date b = new Date();
            java.sql.Date new_return_date = new java.sql.Date(b.getTime());
            statement1.setDate(1, new_return_date);
            statement1.setString(2, uid1);
            statement1.setString(3, callnum1);
            statement1.setInt(4, copynum1);
            statement1.executeUpdate();
            statement1.close();
            System.out.println("car returning performed successfully.\n");
            }
            else {
                System.out.println("car returning performed unsuccessfully, no existing record from inputted specifications.\n");
            }
		} catch (SQLException e) {
			System.out.println("[Error]: No Matching car copy found.\n");
		}
	}

    public void ListUnreturned(String startingdate, String endingdate) {
        SimpleDateFormat inputformat = new SimpleDateFormat("dd/MM/YYYY");

        try {
            java.util.Date sdate = inputformat.parse(startingdate);
            java.util.Date edate = inputformat.parse(endingdate);

            java.sql.Date sdatesql = new java.sql.Date(sdate.getTime());
            java.sql.Date edatesql = new java.sql.Date(edate.getTime());
            
            PreparedStatement statement1 = conn.prepareStatement("SELECT * FROM rent WHERE return_date IS NULL and checkout BETWEEN ? and ? ORDER BY checkout DESC;");
            statement1.setDate(1, sdatesql);
            statement1.setDate(2, edatesql);
            ResultSet rs1 = statement1.executeQuery();
            if (!rs1.next()) {
                System.out.println("No such car copies exist in the database.\n");
            } else {
                System.out.println("List of UnReturned Cars:");
                rs1.previous();
                System.out.println("|UID|CallNum|CopyNum|Checkout|");
            }
            while (rs1.next()) {
                String uid = rs1.getString("uid");
                String callnum = rs1.getString("callnum");
                int copynum = rs1.getInt("copynum");
                Date checkout = rs1.getDate("checkout");
                System.out.println("|" + uid + "|" + callnum + "|" + copynum +
                                    "|" + checkout + "|");  
            }
            statement1.close();
            System.out.println("End of Query.\n");
		} catch (SQLException | ParseException e) {
			System.out.println("[Error]: Incorrect date format.\n");
		}

	}

	public void ShowManagerFunctions() throws ParseException, SQLException {
		System.out.print("\n");
		while (true) {
			System.out.println("-----Operations for manager menu-----");
			System.out.println("What kind of operation would you like to perform?");
			System.out.println("1. Car Renting");
			System.out.println("2. Car Returning");
			System.out.println("3. List all un-returned car copies which are checked-out within a period");
			System.out.println("4. Return to the main menu");
			System.out.print("Enter your choice: ");
			Scanner keyboard = new Scanner(System.in);
			option = keyboard.nextInt();
			keyboard.nextLine();
			switch (option) {
			case 1:
                System.out.print("Enter The User ID: ");
                String uid = keyboard.nextLine();
                System.out.print("Enter The Call Number: ");
                String callnum = keyboard.nextLine();
                System.out.print("Enter The Copy Number: ");
                int copynum = keyboard.nextInt();
                CarRenting(uid,callnum,copynum);
				break;
			case 2:
                System.out.print("Enter The User ID: ");
                String uid1 = keyboard.nextLine();
                System.out.print("Enter The Call Number: ");
                String callnum1 = keyboard.nextLine();
                System.out.print("Enter The Copy Number: ");
                int copynum1 = keyboard.nextInt();
                CarReturning(uid1,callnum1,copynum1);
				break;
			case 3:
                System.out.print("Type in the starting date [dd/mm/yyyy]: ");
                String startingdate = keyboard.nextLine();
                System.out.print("Type in the ending date [dd/mm/yyyy]: ");
                String endingdate = keyboard.nextLine(); 	
                ListUnreturned(startingdate,endingdate);	
                break;		
			case 4:
				System.out.print("\n");
				return;
			default:
				System.out.println("Invalid Choice! Please try again with a valid choice!\n");
			}
		}
	}
}
