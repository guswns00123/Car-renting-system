import java.sql.*;
import java.util.Scanner;
import java.util.InputMismatchException;

public class UserFunctions{
    Connection conn;
    int option;

    public UserFunctions(Connection conn) {
        this.option = 0;
        this.conn = conn;
    }
    
    public void ShowUserFunctions(){
        System.out.println();
        while(true){
            System.out.println("-----Operations for user menu-----");
            System.out.println("what kind of operation would you like to perform?");
            System.out.println("1. Search for Cars");
            System.out.println("2. Show loan record of a user");
            System.out.println("3. Return to the main menu");
            System.out.print("Enter you Choice: ");
            try{
              Scanner sc = new Scanner(System.in);
              option = sc.nextInt();
              if (option == 1){
                  search_car(conn); 
              }
              else if(option == 2){
                  show_loan_record(conn);
                
              }
              else if(option == 3){
                  System.out.println();
                  break;
              }
              else{
                  System.out.println("Invalid input!");
              }
            }catch(Exception e){
                System.out.println("Different type input!");
            }
            
        }
        
        
        
    }
	public static void search_car(Connection conn){
        int num1;
        String callnumber, searchKey;
        while(true){
            System.out.println("Choose the Search criterion");
            System.out.println("1. call number");
            System.out.println("2. name");
            System.out.println("3. company");
            System.out.print("Choose the search criterion: ");
            try{
                Scanner sc = new Scanner(System.in);
                num1 = sc.nextInt();
                if (num1 >= 1 && num1 <= 3) break;
                else System.out.println("Invalid input!");
            }catch(Exception e){
                System.out.println("Different type input!");
            }
            
        }
          
        try{
            System.out.print("Type in the Search Keyword:" );
            Scanner keyword = new Scanner(System.in);
            PreparedStatement pstm;
            if (num1 == 1){
                callnumber = keyword.nextLine();
                String sql = "SELECT * from car, car_category CC, copy, produce  WHERE car.callnum = copy.callnum AND car.ccid = CC.ccid AND car.callnum = produce.callnum AND copy.callnum = produce.callnum AND car.callnum = ? order by car.callnum ASC";
                pstm = conn.prepareStatement(sql);
                pstm.setString(1, callnumber);
                    
            }
            else if(num1 == 2){
                searchKey = keyword.nextLine();
                String sql = "SELECT * from car, car_category CC, copy, produce WHERE car.callnum = copy.callnum AND car.ccid = CC.ccid AND car.callnum = produce.callnum AND copy.callnum = produce.callnum AND car.name LIKE BINARY ? order by car.callnum ASC";
                pstm = conn.prepareStatement(sql);
                searchKey = "%" + searchKey + "%";
                pstm.setString(1, searchKey);
    
            }
            else{
                searchKey = keyword.nextLine();
                String sql = "SELECT * from car, car_category CC, copy, produce WHERE car.callnum = copy.callnum AND car.ccid = CC.ccid AND car.callnum = produce.callnum AND copy.callnum = produce.callnum AND produce.cname LIKE BINARY ? order by car.callnum ASC";
                pstm = conn.prepareStatement(sql);
                searchKey = "%" + searchKey + "%";
                pstm.setString(1, searchKey);
            }
            
            ResultSet rs = pstm.executeQuery();
            Boolean isresult = false;
            String rs_call_num = "", rs_car_name = "", rs_name = "", rs_company = "";
            int rs_copy_num;
            System.out.println("|Call Num|Name|Car Category|Company|Available No. of Copy|");
            while (rs.next()){
                isresult = true;
                rs_call_num = rs.getString("callnum");
                rs_car_name = rs.getString("name");
                rs_name = rs.getString("ccname");
                rs_company = rs.getString("cname");
                rs_copy_num = rs.getInt("copynum");
                System.out.println("|" + rs_call_num + "|" + rs_car_name + "|" + rs_name + "|" + rs_company + "|" + rs_copy_num + "|");
            }
            if (!isresult) System.out.println("No Result");
            else System.out.println("End of Query");  
        
        }catch (SQLException e){
          System.out.println("Wrong SQL!");
        }catch (Exception e){
          System.out.println("Invalid input!");
        }
        

    
    }
    public static void show_loan_record(Connection conn){
        String uid;
        try{
            System.out.print("Enter The cuser ID: ");
            Scanner sc = new Scanner(System.in);
            uid = sc.nextLine();
            System.out.println("Loan Record:");
            PreparedStatement pstm;
            String sql = "SELECT * FROM car, copy, rent, produce WHERE car.callnum = copy.callnum AND car.callnum = produce.callnum AND produce.callnum = copy.callnum AND rent.callnum = car.callnum AND rent.uid = ? order by rent.checkout DESC";
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, uid);
            ResultSet rs = pstm.executeQuery();
            System.out.println("|CallNum|CopyNum|Name|Company|Check-out|Returned?|");
            String rs_call_num = "", rs_name ="", rs_company = "", rs_returned = "";
            int rs_copynum;
            Date rs_rent, rs_return;
            Boolean isresult = false;
            while (rs.next()){
                isresult = true;
                rs_call_num = rs.getString("callnum");
                rs_copynum = rs.getInt("copynum");
                rs_name = rs.getString("name");
                rs_company = rs.getString("cname");
                rs_rent = rs.getDate("checkout");
                rs_return = rs.getDate("return_date");
                if (rs_return == null){
                    rs_returned = "No";
                }
                else{
                    rs_returned = "Yes";
                }
                System.out.println("|"+ rs_call_num +"|"+ rs_copynum +"|"+ rs_name +"|"+ rs_company +"|"+ rs_rent +"|"+ rs_returned +"|" );
            }
            if (!isresult) System.out.println("No Result");
            else System.out.println("End of Query");
        }catch(SQLException e){
            System.out.println("Wrong SQL!");
        }catch(Exception e){
            System.out.println("Invalid input!");
            
        }

    }
}