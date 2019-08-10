package com.evicohen.DB.DBDAO;

import java.beans.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;
import javax.management.Query;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.evicohen.DB.*;
import com.evicohen.DB.DAO.*;
import com.evicohen.Exceptions.*;
import com.evicohen.JavaBeans.*;
import com.evicohen.Logs.*;

import java.util.*;

public class CouponDBDAO implements CouponDAO {

	/**
	 * This class implements basic methods between the application level to the DB
	 * such as C.R.U.D. the logic of the program doesn't implement in this level.
	 * this level is the only connection to the SQL database,this level uses a
	 * connection pool as a data access pattern Used prepared statements to prevent
	 * SQL injection It contains : removeCoupon removeCustomerCoupon
	 * removeCompanyCoupon updateCoupon getCoupon getCouponByTitle getAllCoupouns
	 * getCouponByType createCoupon (create coupon and update join table
	 * Company_Coupon) createCoupon
	 * 
	 * @throws DBException
	 */

	/******************************************
	 * Attributes
	 *********************************************/

	private Connection conn;
	private Coupon couponLocal;
	private long id;

	/*********************************************
	 * CTOR
	 ************************************************/
	public CouponDBDAO() {
		// TODO Auto-generated constructor stub
	}

	/*********************************************
	 * Methods
	 *********************************************/
	/*
	 * Remove Company This method remove a company from company table
	 */
	@Override
	public void removeCoupon(Coupon coupon) throws Exception {

		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = getAllCoupouns();
		Iterator<Coupon> itr = allCoupons.iterator();

		// Open a connection from the connection pool class
		try {
			conn = ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		String sql = "DELETE FROM COUPON WHERE id=?";
		PreparedStatement pstmt = null;

		try {
			while (itr.hasNext()) {
				Coupon coupon2 = new Coupon();
				coupon2 = itr.next();
				if (coupon2.getTitle().equals(coupon.getTitle())) {
					pstmt = conn.prepareStatement(sql);
					conn.setAutoCommit(false);// turn off auto-commit
					pstmt.setLong(1, coupon2.getId()); // Sets the designated parameter to the given Java long value
					pstmt.executeUpdate();// Execute the query and update
					conn.commit();// Commit the changes,If there is no error.
				}
			}

		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new DBException(e1.getMessage());
			}
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

		Logger.log(Log.info("coupon " + coupon.getTitle() + " successfully Removed from the DB"));
	}

	/*
	 * Remove Customer Coupon This method remove coupon from join table,
	 * Customer_Coupon
	 */
	public void removeCustomerCoupon(Coupon coupon) throws Exception {

		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = getAllCoupouns();
		Iterator<Coupon> itr = allCoupons.iterator();

		// Open a connection from the connection pool class
		try {
			conn = ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}

		String sql = "DELETE FROM CUSTOMER_COUPON WHERE COUPON_ID=?";
		PreparedStatement pstmt = null;

		try {
			while (itr.hasNext()) {
				Coupon coupon2 = new Coupon();
				coupon2 = itr.next();
				if (coupon2.getTitle().equals(coupon.getTitle())) {
					pstmt = conn.prepareStatement(sql);
					conn.setAutoCommit(false);// turn off auto-commit
					id = coupon2.getId();
					pstmt.setLong(1, id);
					pstmt.executeUpdate();
					conn.commit();

					Logger.log(Log
							.info("coupon " + coupon2.getTitle() + " successfully Removed from Customer_Coupon table"));
					break;
				}
			}

		} catch (SQLException e) {
			try {
				conn.rollback();// roll back updates to the database , If there is error
			} catch (SQLException e1) {
				throw new DBException(e1.getMessage());
			}
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
	}

	/*
	 * Remove Company Coupons This method remove coupon from join table,
	 * Company_Coupon
	 */
	public void removeCompanyCoupon(Coupon coupon) throws Exception {

		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = getAllCoupouns();
		Iterator<Coupon> itr = allCoupons.iterator();

		// Open a connection from the connection pool class
		try {
			conn = ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		String sql = "DELETE FROM COMPANY_COUPON WHERE COUPON_ID=?";
		PreparedStatement pstmt = null;

		try {
			// Remove all the company coupons from the join table
			while (itr.hasNext()) {

				Coupon coupon2 = new Coupon();
				coupon2 = itr.next();
				if (coupon2.getTitle().equals(coupon.getTitle())) {
					pstmt = conn.prepareStatement(sql);
					conn.setAutoCommit(false);// turn off auto-commit
					pstmt.setLong(1, coupon2.getId());
					pstmt.executeUpdate();
					conn.commit();

					Logger.log(Log
							.info("coupon " + coupon2.getTitle() + " successfully Removed from Company_Coupon table"));
				}
			}

		} catch (SQLException e) {
			try {
				conn.rollback();// roll back updates to the database , If there is error
			} catch (SQLException e1) {
				throw new DBException(e1.getMessage());
			}
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

	}

	/*
	 * Update Coupon This method update coupon attributes ( Start date, End Date,
	 * Amount , Active )
	 */
	@Override
	public void updateCoupon(Coupon coupon) throws Exception {

		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = getAllCoupouns();
		Iterator<Coupon> itr = allCoupons.iterator();

		// Open a connection from the connection pool class
		conn = ConnPool.getInstance().getConnection();
		// create the Execute query
		PreparedStatement pstms = null;
		String sqlString = "UPDATE COUPON SET START_DATE = ?, END_DATE = ? , AMOUNT = ? , ACTIVE = ? WHERE ID = ? ";
		try {
			// create PreparedStatement and build the SQL query

			while (itr.hasNext()) {

				Coupon coupon2 = new Coupon();
				coupon2 = itr.next();

				if (coupon.getTitle().equalsIgnoreCase(coupon2.getTitle())) {

					pstms = conn.prepareStatement(sqlString);
					pstms.setDate(1, (Date) coupon.getStartDate());
					pstms.setDate(2, (Date) coupon.getEndDate());
					pstms.setInt(3, coupon.getAmount());
					pstms.setBoolean(4, coupon.getActive());
					pstms.setLong(5, coupon2.getId());
					pstms.executeUpdate();

				}
			}
		} catch (SQLException e) {
			throw new DBException("update coupon failed with id =" + coupon.getId());
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

		Logger.log(Log.info("The Coupon " + coupon.getTitle() + " is Updated"));

	}

	/*
	 * Get Coupon This method returns Coupon object by id
	 */
	@Override
	public Coupon getCoupon(long id) throws Exception {

		Coupon coupon = new Coupon();
		// Open a connection from the connection pool class
		try {
			conn = ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COUPON WHERE ID=" + id;
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			resultSet.next();
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
			coupon.setActive(resultSet.getBoolean(10));

			// TODO - Add the coupons list from the ArrayCollection
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
		Logger.log(Log.info("Get Coupon " + coupon.getTitle() + " seccssufully "));
		return coupon;
	}

	/*
	 * Update Coupon Expiration This method check the coupon expiration and update
	 * the Active column ( boolean )
	 */
	public void updateCouponsExpiration() throws Exception {

		Coupon coupon = new Coupon();
		Set<Coupon> allCoupons = new HashSet<Coupon>();

		allCoupons = getAllCoupouns();
		Iterator<Coupon> itr = allCoupons.iterator();

		while (itr.hasNext()) {
			coupon = itr.next();

			// Check if the coupon is expired and update the Active value
			if (coupon.getStartDate().compareTo(coupon.getEndDate()) > 0
					|| coupon.getAmount() == 0 && coupon.getActive()) {
				coupon.setActive(false);
				updateCoupon(coupon);
				Logger.log(Log.info("The Coupon " + coupon.getTitle() + " is Expired and updated coupon details "));
			}

			// Check if the coupon is valid and update the Active value
			if (coupon.getStartDate().compareTo(coupon.getEndDate()) < 0 && !coupon.getActive()
					&& coupon.getAmount() > 0) {
				coupon.setActive(true);
				updateCoupon(coupon);
				Logger.log(Log.info("The Coupon " + coupon.getTitle() + " is Valide and updated coupon details"));

			}

		}

	}

	/*
	 * Get Coupon By Title This method return a coupon object by coupon title
	 */
	public Coupon getCouponByTitle(String couponTitle) throws Exception {

		Coupon coupon = new Coupon();
		Set<Coupon> allCoupons = new HashSet<Coupon>();

		allCoupons = getAllCoupouns();
		Iterator<Coupon> itr = allCoupons.iterator();

		while (itr.hasNext()) {
			coupon = itr.next();
			if (coupon.getTitle().equals(couponTitle)) {
				Logger.log(Log.info("The Coupon " + coupon.getTitle() + " is exist"));
				return coupon;
			}

		}
		JFrame frame = new JFrame("JOptionPane showMessageDialog example");
		JOptionPane.showMessageDialog(frame, "The Coupon " + coupon.getTitle() + " is not exist");
		Logger.log(Log.info("The Coupon " + coupon.getTitle() + " is not exist"));
		return null;

	}

	/*
	 * Get all Coupons This method return Set collection of coupon type, contain all
	 * the coupons
	 */
	@Override
	public synchronized Set<Coupon> getAllCoupouns() throws Exception {

		Set<Coupon> coupons = new HashSet<Coupon>();

		// Open a connection from the connection pool class
		try {
			conn = ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COUPON";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			while (resultSet.next()) {
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
				coupon.setActive(resultSet.getBoolean(10));
				coupons.add(coupon);

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
		Logger.log(Log.info("Get all Coupons successfully"));
		return coupons;
	}

	/*
	 * Print All coupons This method print all the coupons
	 */
	public void printAllCoupons() throws Exception {

		Coupon coupon = new Coupon();

		// Open a connection from the connection pool class
		try {
			conn = ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COUPON";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			while (resultSet.next()) {

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
				coupon.setActive(resultSet.getBoolean(10));

				System.out.println(coupon);

			}

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

		Logger.log(Log.info("Print all Coupons successfully"));
	}

	/*
	 * Get coupon By type This method return Set Collection of Coupon type, contain
	 * all the coupons with the same type
	 */
	@Override
	public synchronized Set<Coupon> getCouponByType(Coupon coupon) throws Exception {

		Set<Coupon> coupons = new HashSet<Coupon>();

		// Open a connection from the connection pool class
		try {
			conn = ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COUPON";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results

			while (resultSet.next()) {
				CouponType type = CouponType.valueOf(resultSet.getString(6));
				// Check the type of the Coupon
				if (coupon.getType().equals(type)) {

					Coupon coupon1 = new Coupon();
					coupon1.setId(resultSet.getLong(1));
					coupon1.setTitle(resultSet.getString(2));
					coupon1.setStartDate((Date) resultSet.getDate(3));
					coupon1.setEndDate((Date) resultSet.getDate(4));
					coupon1.setAmount(resultSet.getInt(5));
					CouponType type2 = CouponType.valueOf(resultSet.getString(6)); // Convert String to Enum
					coupon1.setType(type2);
					coupon1.setMessage(resultSet.getString(7));
					coupon1.setPrice(resultSet.getDouble(8));
					coupon1.setImage(resultSet.getString(9));
					coupon1.setActive(resultSet.getBoolean(10));
					coupons.add(coupon1);

				}

			}

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
		Logger.log(Log.info("Get all Coupons by type successfully"));

		return coupons;
	}

	/*
	 * Create Coupon This method create coupon and update the join table
	 * Company_Coupon
	 */
	public void createCoupon(Coupon coupon, Company company) throws Exception, SQLException {

		long companyID = company.getId();
		long couponID = 0;

		// Open a connection from the connection pool class
		try {
			conn = ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		String sql = "INSERT INTO COUPON (TITLE,START_DATE,END_DATE,AMOUNT,TYPE,MESSAGE,PRICE,IMAGE,ACTIVE)  VALUES(?,?,?,?,?,?,?,?,?)";
		String sql2 = " INSERT INTO COMPANY_COUPON (COMP_ID,COUPON_ID) VALUES(?,?)";
		String sql3 = "SELECT * FROM COUPON";
		// Set the results from the database
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		java.sql.Statement stmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, coupon.getTitle());
			pstmt.setDate(2, (Date) coupon.getStartDate());
			pstmt.setDate(3, (Date) coupon.getEndDate());
			pstmt.setInt(4, coupon.getAmount());
			pstmt.setString(5, coupon.getType().name()); // **.name() casting the ENUM to String
			pstmt.setString(6, coupon.getMessage());
			pstmt.setDouble(7, coupon.getPrice());
			pstmt.setString(8, coupon.getImage());
			pstmt.setBoolean(9, coupon.getActive());
			// Execute the query and update
			pstmt.executeUpdate();
			// Insert the new coupon to join table COMPANY_COUPON
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql3);
			while (resultSet.next()) {
				couponID = resultSet.getLong(1);
			}
			// constructor the object, retrieve the attributes from the results
			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setLong(1, companyID);
			pstmt2.setLong(2, couponID);
			pstmt2.executeUpdate();

		} catch (SQLException e) {
			// Handle errors for JDBC
			throw new DBException("Coupon creation failed");
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

		Logger.log(Log.info("Insert coupon " + coupon.getTitle() + " successfully !!!!"));
	}

}
