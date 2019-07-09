package com.evicohen.DB.DBDAO;

import java.beans.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.omg.CORBA.PRIVATE_MEMBER;
import com.evicohen.DB.*;
import com.evicohen.DB.DAO.*;
import com.evicohen.Exceptions.*;
import com.evicohen.JavaBeans.*;
import com.evicohen.Logs.*;


/**
 * @author evic
 *
 */

public class CustomerDBDAO implements CustomerDAO {

	/**
	 * This class implements basic methods between the application level to the DB
	 * such as C.R.U.D. the logic of the program doesn't implement in this level.
	 * this level is the only connection to the SQL database,this level uses a
	 * connection pool as a data access pattern 
	 * It Contains: 
	 * login
	 * createCustomer
	 * removeCustomer
	 * removeCustomerCoupons
	 * updateCustomer
	 * getCustomerById
	 * getCustomerCoupons
	 * getAllCustomer
	 * printAllCustmers
	 * getCustomer
	 * printAllCustmers
	 * @throws DBException
	 */

	/****************************************** Attributes ********************************************/

	Connection conn;

	/********************************************* CTOR ***********************************************/

	public CustomerDBDAO() {
		// TODO Auto-generated constructor stub
	}

	/******************************************** Methods 
	 * @throws IOException *********************************************/
	/* Create Customer method  
	 * This method created customer and insert the attributes to the Customer table 
	 * Attributes : Customer name, Password 
	 */
	@Override
	public void createCustomer(Customer customer) throws Exception{

		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		
		// Define the Execute query
		String sql = "INSERT INTO Customer (CUST_NAME,PASSWORD) VALUES (?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, customer.getCustomerName());
			pstmt.setString(2, customer.getPassword());
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// Handle errors for JDBC
			throw new CreateException("Customer creation faild");
		} finally {
			// finally block used to close resources
			try {
				if (pstmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}
		
		Logger.log(Log.info("Insert customer " + customer.getCustomerName() + " successfully"));

	}

	/* Remove Customer method 
	 * This method remove customer from customer table, created a local object in order to get the PK. 
	 */
	@Override
	public void removeCustomer(Customer customer) throws Exception {
	
		// retrieve the PK by the customer name
		Customer customerLocaly = new Customer() ; 
		customerLocaly = getCustomer(customer.getCustomerName()); 
		
		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		
		//Create the SQL query 
		String sql = "DELETE FROM CUSTOMER WHERE id=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(false);// turn off auto-commit
			pstmt.setLong(1, customerLocaly.getId()); // Sets the designated parameter to the given Java long value
			pstmt.executeUpdate();
			conn.commit();// Commit the changes,If there is no error.
		} catch (SQLException e) {
			try {
				conn.rollback();// roll back updates to the database , If there is error
			} catch (SQLException e1) {
				throw new DBException("The Rollback connection failed");
			}
			throw new RemoveException("The remove action is failed"); 
		} finally {
			// finally block used to close resources
			try {
				if (pstmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}

		Logger.log(Log.info("Removed customer " + customer.getCustomerName() + " successfully"));
	}

	/* Remove Customer Coupon 
	 * This method remove all the customer coupon from the join table Customer_Coupon.
	 */
	public void removeCustomerCoupons(Customer customer) throws Exception {
		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = getCustomerCoupons(customer);

		long couponID;
		
		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		
		//Create the SQL query 
		String sql = "DELETE FROM CUSTOMER_COUPON WHERE COUPON_ID=?";
		PreparedStatement pstmt = null;

		try {
			// Remove all the company coupons from the join table
			java.util.Iterator<Coupon> itr = allCoupons.iterator();
			while (itr.hasNext()) {
				pstmt = conn.prepareStatement(sql);
				conn.setAutoCommit(false);// turn off auto-commit
				couponID = itr.next().getId();
				pstmt.setLong(1, couponID);
				pstmt.executeUpdate();
				conn.commit();
			}
		}

		catch (SQLException e) {
			try {
				conn.rollback();// roll back updates to the database , If there is error
			} catch (SQLException e1) {
				throw new DBException("The rollback is failed");
			}
			throw new RemoveException("The remove action is failed"); 
		} finally {
			// finally block used to close resources
			try {
				if (pstmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}
		
		Logger.log(Log.info("Removed customer " + customer.getCustomerName()+ " coupons successfully"));

	}

	/* Update Customer 
	 * This method update the customer password
	 */
	@Override
	public void updateCustomer(Customer customer, String password) throws Exception {

		// retrieve the customer details from DB 
		Customer customerLocaly = new Customer() ; 
		customerLocaly = getCustomer(customer.getCustomerName()); 	
		// Open a connection from the connection pool class 
			conn =ConnPool.getInstance().getConnection();
		// create the Execute query
		PreparedStatement pstms = null;
		String sqlString = "UPDATE CUSTOMER SET  PASSWORD = ? WHERE ID = ? ";
		try {
			// create PreparedStatement and build the SQL query
			pstms = conn.prepareStatement(sqlString);
			pstms.setString(1, password);
			pstms.setLong(2, customerLocaly.getId());
			pstms.executeUpdate();
		} catch (SQLException e) {
			throw new UpdateException("update customer failed");
		} finally {
			// finally block used to close resources
			try {
				if (pstms != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}

		Logger.log(Log.info("Update customer " + customer.getCustomerName() + " successfully"));
	}

	/* Get Customer By Id 
	 * This method return a customer object by customer's ID 
	 */
	public Customer getCustomerById(long id) throws Exception {

		Customer customer = new Customer(); 
		
		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM CUSTOMER WHERE ID=" + id;
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			resultSet.next();
			customer.setId(resultSet.getLong(1));
			customer.setCustomerName(resultSet.getString(2));
			customer.setPassword(resultSet.getString(3));
			
		} catch (SQLException e) {
			throw new DBException("update customer failed");
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}
		
		Logger.log(Log.info("Get customer " + customer.getCustomerName() + " by ID successfully"));
		return customer;
	}

	/* Get Customer Coupons
	 * This method return Set collection of customer coupons  
	 */
	public Set<Coupon> getCustomerCoupons(Customer customer) throws Exception {
		Set<Coupon> coupons = new HashSet<Coupon>();
		ArrayList<Long> couponIDs = new ArrayList<Long>();

		// Retrieve the PK of the customer by name
		Customer customerLocaly = new Customer();
		customerLocaly = getCustomer(customer.getCustomerName());

		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		
		// Define the Execute query
		java.sql.Statement stmt = null;
		java.sql.Statement stmt1 = null;
		try {
			stmt = conn.createStatement();
			stmt1 = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COUPON";
			String sql1 = "SELECT * FROM CUSTOMER_COUPON";
			// Set the results from the database,
			ResultSet resultSet = stmt.executeQuery(sql);
			ResultSet resultSet2 = stmt1.executeQuery(sql1);
			// constructor the object, retrieve the attributes from the results

			while (resultSet2.next()) {

				if (resultSet2.getLong(1) == customerLocaly.getId()) {
					couponIDs.add(resultSet2.getLong(2));
				}
			}
			while (resultSet.next()) {
				if (couponIDs.contains(resultSet.getLong(1))) {

					Coupon coupon = new Coupon();
					coupon.setId(resultSet.getLong(1));
					coupon.setTitle(resultSet.getString(2));
					coupon.setStartDate((Date) resultSet.getDate(3));
					coupon.setEndDate((Date) resultSet.getDate(4));

					coupon.setAmount(resultSet.getInt(5));
					CouponType type = CouponType.valueOf(resultSet.getString(6)); // Convert String to Enum
					coupon.setType(type);
					coupon.setMessage(resultSet.getString(7));
					coupon.setPrice(resultSet.getDouble(8));
					coupon.setImage(resultSet.getString(9));

					coupons.add(coupon);
				}

			}

		} catch (SQLException e) {
			throw new DBException("Retriev all the coupons failed");

		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
		}

		Logger.log(Log.info("Get all customer " + customer.getCustomerName() + " coupons successfully"));
		return coupons;
	}

	/* Login 
	 * This method return boolean value if the customer exist  
	 */
	@Override
	public Boolean login(String ccustName, String password) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* Get all Customers
	 * This method returns Set collection of customer object that contain all the customers 
	 */
	@Override
	public Set<Customer> getAllCustomers() throws Exception {

		Set<Customer> customers = new HashSet<Customer>();

		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM CUSTOMER";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			while (resultSet.next()) {
				Customer customer = new Customer();
				customer.setId(resultSet.getLong(1));
				customer.setCustomerName(resultSet.getString(2));
				customer.setPassword(resultSet.getString(3));

				customers.add(customer);
			}

		} catch (SQLException e) {
			throw new DBException("Retriev all the coupons failed");
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}
		
		Logger.log(Log.info("Get all customers successfully"));
		return customers;

	}

	/* Print All Customers 
	 * This method print all the customers 
	 */
	public void printAllCustmers() throws Exception {

		Customer customer = new Customer();

		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM CUSTOMER";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			while (resultSet.next()) {

				customer.setId(resultSet.getLong(1));
				customer.setCustomerName(resultSet.getString(2));
				customer.setPassword(resultSet.getString(3));

				System.out.println(customer);
			}

		} catch (SQLException e) {
			throw new DBException("Retriev all the coupons failed");
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}
		
		Logger.log(Log.info("Print all customer successfully"));

	}

	/* Get customer 
	 * This method return a customer object by customer name 
	 */
	public Customer getCustomer(String CUST_NAME) throws Exception {

		Customer customer = new Customer();
		
		// Open a connection from the connection pool class 
			conn =ConnPool.getInstance().getConnection();
		java.sql.Statement stmt = null;

		try {

			stmt = conn.createStatement();
			String sql = "SELECT * FROM CUSTOMER";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) {
				if (resultSet.getString(2).equals(CUST_NAME)) {			
					customer.setId(resultSet.getLong(1));
					customer.setCustomerName(resultSet.getString(2));
					customer.setPassword(resultSet.getString(3));
					break;
				}

			}


		} catch (SQLException e) {
			throw new DBException("get customer failed");
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}
		Logger.log(Log.info("Get customer "+  customer.getCustomerName() + " successfully"));
		return customer;
	}

	/* Purchase Coupon 
	 * This method insert the coupon's id and the company's id to the join table Coupon_Customer  
	 */
	public void purchaseCoupon(Coupon coupon, Customer customer) throws Exception {

		long couponID = 0;
		
		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}

		String sql1 = "SELECT * FROM COUPON";
		String sql2 = " INSERT INTO CUSTOMER_COUPON(CUST_ID,COUPON_ID) VALUES(?,?)";
		// Set the results from the database
		PreparedStatement pstmt = null;
		java.sql.Statement stmt = null;
		try {

			//Retrieve the PK of the coupon 
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql1);
			while (resultSet.next()) {
				if (coupon.getTitle().equals(resultSet.getString(2))) {
					couponID = resultSet.getLong(1);
				}
			}

			// Insert the new coupon to join table COMPANY_COUPON
			pstmt = conn.prepareStatement(sql2);
			pstmt.setLong(1, customer.getId());
			pstmt.setLong(2, couponID);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// Handle errors for JDBC
			throw new DBException("Purchased Coupon failed");
		} finally {
			// finally block used to close resources
			try {
				if (pstmt != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}
			try {
				if (conn != null)
					ConnPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}
		
	    Logger.log(Log.info("For Customer " +  customer.getCustomerName() +  " the purchase coupon process" + coupon.getTitle()  + " succeed"));
	}

}
