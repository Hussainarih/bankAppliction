package jabc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Scanner;

public class BankApp {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/employee_bank";
		String un = "root";
		String pwd = "root";
		Connection con = null; 
		 

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			 con = DriverManager.getConnection(url, un, pwd);
			
			 Scanner scan = new Scanner(System.in);
			 
			 // Login Module
			 System.out.println("<-----WELCOME TO MY-BANK----->");
			 System.out.println("Enter Account Number:");
			 long acc_num = scan.nextLong();
			 System.out.println("Enter Pin Number:");
			 int pin = scan.nextInt();
			 
			 PreparedStatement Statement = con.prepareStatement("select * from bank where acc_num = ? and pin = ?");
			 
			 Statement.setLong(1,acc_num);
			 Statement.setInt(2,pin);
			ResultSet set1 = Statement.executeQuery();
			 
			 set1.next();
			 
			 String name = set1.getString(2);
			 int bal = set1.getInt(7);
			 
			 System.out.println("Welcome "+name);
			 System.out.println("Available balanace is: "+bal);	 
			 
			 // Transfer Module
			 System.out.println("-----Tranfer Details----->");
			 System.out.println("Enter the beneficiary account number");
			 Long bacc_num = scan.nextLong();
			 System.out.println("Enter the transfer amount");
			 int t_amount = scan.nextInt();
			 
			 con.setAutoCommit(false);
			 Savepoint s = con.setSavepoint();
			 
			 PreparedStatement statement2 = con.prepareStatement("update bank set balance = balance - ?" + " where acc_num = ?");
			 statement2.setInt(1, t_amount);
			 statement2.setLong(2, acc_num);
			 
			 statement2.executeUpdate();
			 
			 System.out.println("<-----Incoming credit request----->");
			 System.out.println(name + "bank no "+ acc_num  + " wants to transfer " +t_amount);
			 System.out.println("Press Y to receive");
			 System.out.println("Press N to reject");
			 
			String choice = scan.next();
			
			if(choice.equals("Y")) {
				PreparedStatement statement3 = con.prepareStatement("update bank set balance = balance + ?" + "  where acc_num = ?");
				statement3.setInt(1, t_amount);
				statement3.setLong(2,bacc_num );
				statement3.executeUpdate();
				
				PreparedStatement statement4 = con.prepareStatement("select * from bank" + " where acc_num = ?");
				statement4.setLong(1,bacc_num);
				ResultSet set2 = statement4.executeQuery();
				set2.next();
				System.out.println("updated balance is: "+set2.getInt(7));
				
				
			}else {
				con.rollback(s);
				PreparedStatement statement5 = con.prepareStatement("select * from bank" + " where acc_num = ?");
				statement5.setLong(1,bacc_num);
				ResultSet set2 = statement5.executeQuery();
				set2.next();
				System.out.println("Existing balance is: "+set2.getInt(7));
				
				
			}
			con.commit();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		
		}
			catch(Exception e) {
				e.printStackTrace();
		}
		
	}

}
