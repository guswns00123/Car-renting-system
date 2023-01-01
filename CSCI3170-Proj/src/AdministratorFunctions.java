import java.sql.*;
import java.util.Scanner;

import java.util.Date;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.IOException;
import java.io.File;

public class AdministratorFunctions {

	int option;
	Connection conn;

	public AdministratorFunctions(Connection conn) {
		this.option = 0;
		this.conn = conn;
	}
//create all tables for this system based on the relational schema
		public void CreateTables() {
		try {
			Statement sql_statement = this.conn.createStatement();
			String table1 = "CREATE TABLE IF NOT EXISTS user_category(" + "ucid INT NOT NULL," + "max INT NOT NULL,"
					+ "period INT NOT NULL," + "PRIMARY KEY(ucid)" + ")";
			String table2 = "CREATE TABLE IF NOT EXISTS user(" + "uid CHAR(12) NOT NULL,"
					+ "name VARCHAR(25) NOT NULL," + "age INT NOT NULL," + "occupation VARCHAR(20) NOT NULL,"
					+ "ucid INT NOT NULL," + "PRIMARY KEY(uid),"
					+ "FOREIGN KEY(ucid) REFERENCES user_category(ucid) ON DELETE CASCADE ON UPDATE CASCADE" + ")";
			String table3 = "CREATE TABLE IF NOT EXISTS car_category(" + "ccid INT NOT NULL,"
					+ "ccname VARCHAR(20) NOT NULL," + "PRIMARY KEY(ccid)" + ")";			
			String table4 = "CREATE TABLE IF NOT EXISTS car(" + "callnum CHAR(8) NOT NULL,"
					+ "name VARCHAR(10) NOT NULL," + "manufacture DATE NOT NULL, " 
					+ "time_rent INT DEFAULT '0' NOT NULL," + "ccid INT NOT NULL," + "PRIMARY KEY(callnum),"
					+ "FOREIGN KEY(ccid) REFERENCES car_category(ccid) ON DELETE CASCADE ON UPDATE CASCADE" + ")";
			String table5 = "CREATE TABLE IF NOT EXISTS copy(" + "callnum CHAR(8) NOT NULL," + "copynum INT NOT NUll,"
					+ "PRIMARY KEY(callnum, copynum),"
					+ "FOREIGN KEY(callnum) REFERENCES car(callnum) ON DELETE CASCADE ON UPDATE CASCADE" + ")";
			String table6 = "CREATE TABLE IF NOT EXISTS rent(" + "uid CHAR(12) NOT NULL,"
					+ "callnum CHAR(8) NOT NULL," + "copynum INT NOT NUll," + "checkout DATE NOT NULL,"
					+ "return_date DATE," + "PRIMARY KEY(uid, callnum, copynum, checkout),"
					+ "FOREIGN KEY(callnum, copynum) REFERENCES copy(callnum, copynum) ON DELETE CASCADE ON UPDATE CASCADE,"
					+ "FOREIGN KEY(uid) REFERENCES user(uid) ON DELETE CASCADE ON UPDATE CASCADE" + ")";
			String table7 = "CREATE TABLE IF NOT EXISTS produce(" + "cname VARCHAR(25) NOT NULL,"
					+ "callnum CHAR(8) NOT NULL," + "PRIMARY KEY(cname, callnum),"
					+ "FOREIGN KEY(callnum) REFERENCES car(callnum) ON DELETE CASCADE ON UPDATE CASCADE" + ")";

			sql_statement.addBatch(table1);
			sql_statement.addBatch(table2);
			sql_statement.addBatch(table3);
			sql_statement.addBatch(table4);
			sql_statement.addBatch(table5);
			sql_statement.addBatch(table6);
			sql_statement.addBatch(table7);
			sql_statement.executeBatch();
			sql_statement.close();
			System.out.println("Processing...Done. Database is initialized.\n");
			
		} catch (SQLException e) {
			System.out.println("Error encounter: " + e.getMessage() + "\n");
		}
	}
	
    
	// Delete all existing tables in the system
	public void DeleteTables() {
		try {
			Statement sql_statement = this.conn.createStatement();
			String stm1 = "SET FOREIGN_KEY_CHECKS = 0";
			String stm2 = "SELECT table_name " + "FROM information_schema.tables " + "WHERE table_schema = 'db14'";			
			sql_statement.execute(stm1);
			ResultSet rs = sql_statement.executeQuery(stm2);
			while (rs.next()) {
				Statement updateStatement = this.conn.createStatement();
				String Table_Name= rs.getString(1);
				String stm3 = "DROP TABLE IF EXISTS " + Table_Name;
				updateStatement.executeUpdate(stm3);
				updateStatement.close();
			}
			rs.close();
			String stm4 = "SET FOREIGN_KEY_CHECKS = 1";
			sql_statement.execute(stm4);
			sql_statement.close();
			System.out.println("Processing...Done. Database is removed.\n");

		} catch (SQLException e) {
			System.out.println("Error encounter: " + e.getMessage() + "\n");
		}
	}

	// Load data from a dataset
	public void LoadData(String path) throws IOException {
		boolean error = false;		
		String Parent_location = new File(System.getProperty("user.dir")).getParent();
		File folder_path = new File(Parent_location + "/" + path);
		Scanner scan = null;
					
		if (folder_path.list() != null) {
			//car categories
			File carCategories = new File(folder_path + "/" + "car_category.txt");
			scan = new Scanner(carCategories);
			String input;
			while (scan.hasNextLine()) {
				try {
					PreparedStatement prep_statement = this.conn
							.prepareStatement("INSERT INTO car_category(ccid,ccname) VALUES (?,?)");

					input = scan.nextLine();
					String[] values = input.split("\t", -1);
					int ccid = Integer.parseInt(values[0]);
					String ccname = values[1];
					
					prep_statement.setInt(1, ccid);
					prep_statement.setString(2, ccname);
					prep_statement.executeUpdate();
					prep_statement.close();

				} catch (SQLException e) {
					error = true;
					break;
				}
			}

			// cars
			File cars = new File(folder_path + "/" + "car.txt");
			scan = new Scanner(cars);
			

			while (scan.hasNextLine()) {
				try {
					input = scan.nextLine();
					String[] values = input.split("\t", -1);
					String callnum = values[0];
					int copynum = Integer.parseInt(values[1]);
					String name = values[2];
					String cname = values[3];
					String dateString = values[4];
					
					try {						
						
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
						java.sql.Date manufacture = new java.sql.Date(date.getTime()); 
						int time_rent = Integer.parseInt(values[5]);                    
                   		int ccid = Integer.parseInt(values[6]);

                    	PreparedStatement prep_statement = this.conn.prepareStatement(
								"INSERT INTO car(callnum,name,manufacture,time_rent,ccid) VALUES (?,?,?,?,?)");	

						prep_statement.setString(1, callnum);
						prep_statement.setString(2, name);
						prep_statement.setDate(3, manufacture);
						prep_statement.setInt(4, time_rent);
						prep_statement.setInt(5, ccid);
						prep_statement.executeUpdate();
						prep_statement.close();

					}catch (ParseException e) {
						System.out.println("Invalid date: " + e + "\n");
						break;
					}

					for (int x = 1; x <= copynum; x++) {
						PreparedStatement prep_statement1 = this.conn
								.prepareStatement("INSERT INTO copy(callnum,copynum) VALUES (?,?)");
						prep_statement1.setString(1, callnum);
						prep_statement1.setInt(2, x);
						prep_statement1.executeUpdate();
						prep_statement1.close();
					}
						PreparedStatement prep_statement2 = this.conn
								.prepareStatement("INSERT INTO produce(cname,callnum) VALUES (?,?)");
						prep_statement2.setString(1, cname);
						prep_statement2.setString(2, callnum);
						prep_statement2.executeUpdate();
						prep_statement2.close();
					
				} catch (SQLException e) {
					error = true;
					break;
				}
			}

			//user categories 
			
			File userCategories = new File(folder_path + "/" + "user_category.txt");
			
			scan = new Scanner(userCategories);			
			while (scan.hasNextLine()) {
				try {
					PreparedStatement prep_statement = this.conn
							.prepareStatement("INSERT INTO user_category(ucid,max,period) VALUES (?,?,?)");

					input = scan.nextLine();
					String[] values = input.split("\t", -1);
					int ucid = Integer.parseInt(values[0]);
					int max = Integer.parseInt(values[1]);
					int period = Integer.parseInt(values[2]);

					prep_statement.setInt(1, ucid);
					prep_statement.setInt(2, max);
					prep_statement.setInt(3, period);
					prep_statement.executeUpdate();
					prep_statement.close();
				} catch (SQLException e) {
					System.out.println("Could not load records, Please make sure all tables are created\n");
					error = true;
					break;
				}
			}

			//users
			File users = new File(folder_path + "/" + "user.txt");
			
			scan = new Scanner(users);
			while (scan.hasNextLine()) {
				try {
					PreparedStatement prep_statement = this.conn
							.prepareStatement("INSERT INTO user(uid,name,age,occupation,ucid) VALUES (?,?,?,?,?)");

					input = scan.nextLine();
					String[] values = input.split("\t", -1);
					String uid = values[0];
					String name = values[1];
					int age = Integer.parseInt(values[2]);
					String occupation = values[3];
					int ucid = Integer.parseInt(values[4]);
				
					prep_statement.setString(1, uid);
					prep_statement.setString(2, name);
					prep_statement.setInt(3, age);
					prep_statement.setString(4, occupation);
					prep_statement.setInt(5, ucid);
					prep_statement.executeUpdate();
					prep_statement.close();

				} catch (SQLException e) {
					System.out.println("Could not load records, Please make sure all tables are created\n");
					error = true;
					break;
				}
			}

			//records

			File records = new File(folder_path + "/" + "rent.txt");
			
			scan = new Scanner(records);
			while (scan.hasNextLine()) {
				try {
					input = scan.nextLine();
					String[] values = input.split("\t", -1);
					String callnum = values[0];
					int copynum = Integer.parseInt(values[1]);
					String uid = values[2];
					String dateString = values[3];

					try {	
						PreparedStatement prep_statement = this.conn.prepareStatement(
								"INSERT INTO rent(uid,callnum,copynum,checkout,return_date) VALUES (?,?,?,?,?)");											
						Date checkoutDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
						java.sql.Date checkoutSqlDate = new java.sql.Date(checkoutDate.getTime());
						if (values[4].equals("NULL")) {
							prep_statement.setString(5, null);
						} else {
							String returnString = values[4];
							
							Date returnDate = new SimpleDateFormat("yyyy-MM-dd").parse(returnString);
							java.sql.Date returnSqlDate = new java.sql.Date(returnDate.getTime());
							prep_statement.setDate(5, returnSqlDate);
						}
						
						prep_statement.setString(1, uid);
						prep_statement.setString(2, callnum);
						prep_statement.setInt(3, copynum);
						prep_statement.setDate(4, checkoutSqlDate);
						prep_statement.executeUpdate();
						prep_statement.close();

					} catch (ParseException e) {
						System.out.println("Invalid date: " + e + "\n");
						break;
					}
				} catch (SQLException e) {
					System.out.println("Could not load records, Please make sure all tables are created\n");
					error = true;
					break;
				}
			}

			if (!error) {
				System.out.println("Processing...Done. Data is inputted to the database.\n");
			}
		} else {
			throw new IOException("Folder does not exist!\n");
		}

	}


// show number of records in each table 
	public void ShowRecords() {
		Statement statement_showRecords;
		try {
			statement_showRecords = this.conn.createStatement();
			String statement1 = "SELECT table_name " + "FROM information_schema.tables " + "WHERE table_schema = 'db14'";
			ResultSet rs = statement_showRecords.executeQuery(statement1);

			if (!rs.next()) {
				System.out.println("No tables exist in the database.");
			} 
			else {
				System.out.println("Number of records in each table:");
				rs.previous();
			}while (rs.next()) {
				Statement statment_query = this.conn.createStatement();
				String Table_Name= rs.getString(1);

				String statement2 = "SELECT COUNT(*) FROM " + Table_Name;
				ResultSet Result_Table = statment_query.executeQuery(statement2);

				Result_Table.next();
				System.out.println(Table_Name+ ": " + Result_Table.getInt(1));
				statment_query.close();
			}
			System.out.print("\n");
			rs.close();

		} catch (SQLException e) {
			System.out.println("Error encounter: " + e.getMessage() + "\n");
		}
	}

	//Administrator Menu
	public void ShowAdministratorFunctions() {

		System.out.print("\n");
		while (true) {
			System.out.println("-----Operations for administrator menu-----");
			System.out.println("What kind of operation would you like to perform?");
			System.out.println("1. Create all tables");
			System.out.println("2. Delete all tables");
			System.out.println("3. Load from datafile");
			System.out.println("4. Show number of records in each table");
			System.out.println("5. Return to the main menu");
			System.out.print("Enter your choice: ");

			Scanner input_option = new Scanner(System.in);
			option = input_option.nextInt();
			input_option.nextLine();
			switch (option) {
			//create teables
			case 1:
				CreateTables();
				break;
			//delete tables
			case 2:
				DeleteTables();
				break;
			//load records
			case 3:
				System.out.print("\nType in the Source Data Folder Path: ");
				String path = input_option.nextLine();
				try {
					LoadData(path);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				break;
			//show records
			case 4:
				ShowRecords();
				break;
			//return to the main menu
			case 5:
				System.out.print("\n");
				return;
			default:
				System.out.println("Invalid Choice! Please try again with a valid choice!\n");
			}
		}
	}

}
