package com.evicohen.DB.DBDAO;


import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.evicohen.DB.ConnPool;
import com.evicohen.DB.DAO.*;
import com.evicohen.Exceptions.*;
import com.evicohen.JavaBeans.*;
import com.evicohen.Logs.*;







public class CompanyDBDAO implements CompanyDAO {

	/**
	 * This class implement basic methods between the application level to the DB
	 * such as C.R.U.D. the logic of the program dosen't implement in this level.
	 * this level is the only connection to the SQL database,this level uses a
	 * connection pool as a data access pattern  
	 * It contains : 
	 * login 
	 * createCompany
	 * removeComapny 
	 * removeCompanyCoupons
	 * updateComapny 
	 * getCompany 
	 * getAllCompanies 
	 * getCoupons
	 * printAllCompanies 
	 * getCompany
	 * getCompanyCoupons
	 * @throws DBException
	 */

	/*************************************** Attributes*******************************************/
	static Connection conn;
	private Company company;
	CouponDBDAO couponDBDAO = new CouponDBDAO(); 
	Logger logger = new Logger(); 

	/******************************************* CTOR*********************************************/

	public CompanyDBDAO() {
		// TODO Auto-generated constructor stub
	}

	/****************************************** Methods*******************************************/
	/* Create Company 
	 * This method create company and insert to company table
	 */
	@Override
	public void createCompany(Company company) throws Exception  {
		
		// Open a connection from the connection pool class 
		System.out.println(company);
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		String sql = "INSERT INTO COMPANY (COMP_NAME,PASSWORD,EMAIL)  VALUES(?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, company.getCompName());
			pstmt.setString(2, company.getPassword());
			pstmt.setString(3, company.getEmail());
			pstmt.executeUpdate();
			try {
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.getCause();
			}
		} catch (SQLException e) {
			// Handle errors for JDBC
			throw new CreateException("Company creation failed");
		} finally {
			// finally block used to close resources, return the connection pool 
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
		
		Logger.log(Log.info("Insert company " + company.getCompName() + " to the database successfully"));

	}

	/* Remove Company
	 * This method remove company from company table
	 */
	public void removeCompany(Company company) throws Exception  {

		// retrieve the PK by the company name
		Company companyLocal = new Company(); 
		companyLocal = getCompany(company.getCompName());
		
		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		String sql = "DELETE FROM COMPANY WHERE id=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(false);// turn off auto-commit
			pstmt.setLong(1, companyLocal.getId()); // Sets the designated parameter to the given Java long value
			pstmt.executeUpdate();
			conn.commit();// Commit the changes,If there is no error.

		} catch (SQLException e) {
			try {
				conn.rollback();// roll back updates to the database , If there is error
			} catch (SQLException e1) {
				throw new DBException(e1.getMessage());
			}
//			throw new RemoveException("The remove action is failed"); 
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
		
		Logger.log(Log.info("Removed company " + company.getCompName()  + " successfully"));
		
	}

	/* Remove Company coupons 
	 * Remove all the company coupons from the join table
	 * Remove all the company coupons from coupon table
	 */
	public void removeCompanyCoupons (Company company) throws Exception { 
		
		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = getCompanyCoupons(company);
		long couponId ; 
		// Open a connection
		conn =ConnPool.getInstance().getConnection();
		String sql= "DELETE FROM COMPANY_COUPON WHERE COUPON_ID=?";
		PreparedStatement pstmt = null; 
		
		try {
			// Remove all the company coupons from the join table 
			Iterator<Coupon> itr = allCoupons.iterator();
			while (itr.hasNext()) {
				pstmt = conn.prepareStatement(sql);
				conn.setAutoCommit(false);// turn off auto-commit
				couponId =  itr.next().getId();
				pstmt.setLong(1,couponId);
				pstmt.executeUpdate();
				conn.commit();
				
			}
			
			//Remove all the company coupons from coupon table  
			Iterator<Coupon> itr2 = allCoupons.iterator(); 
			while(itr2.hasNext()) { 
				couponDBDAO.removeCoupon(itr2.next());
				
				Logger.log(Log.info(" Coupon " + itr.next().getTitle() + " removed successfully !!!!"));
			}
			
			
		} catch (SQLException e) {
			try {
				conn.rollback();// roll back updates to the database , If there is error
			} catch (SQLException e1) {
				throw new DBException("THe rollback is failed");
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
			

			
	 }
	
	/* Update Company
	 * This method update company attributes, password and Email   
	 */
	@Override
	
	public void updateCompany(Company company) throws Exception {
	
		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// create the Execute query String
		PreparedStatement pstms = null;
		String sqlString = "UPDATE COMPANY SET PASSWORD = ?, EMAIL = ?  WHERE ID = ? ";

		try {
			// create PreparedStatement and build the SQL query
			pstms = conn.prepareStatement(sqlString);
			pstms.setString(1, company.getPassword());
			pstms.setString(2, company.getEmail());
			pstms.setLong(3,company.getId());
			System.out.println(pstms);
			pstms.executeUpdate();
		}catch (SQLException e) {
			throw new UpdateException("Update Company failed");
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
		
		Logger.log(Log.info( "Company " + company.getCompName()  + " Updated successfully"));
		

	}

	/* Get Company
	 * This method return company object by id  
	 */
	@Override
	public Company getCompany(long id) throws DBException, IOException {

		Company company = new Company();
		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COMPANY WHERE ID=" + id;
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			resultSet.next();
			company.setId(resultSet.getLong(1));
			company.setCompName(resultSet.getString(2));
			company.setPassword(resultSet.getString(3));
			company.setEmail(resultSet.getString(4));

			// TODO - Add the coupons list from the ArrayCollection

		} catch (SQLException e) {
			throw new DBException("get company failed with id=" + id);
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
		
		Logger.log(Log.info( "Company " + company.getCompName()  + " Updated successfully"));
		
		return company;
	}

	/* Get All Companies 
	 * This method return Set collection of Company object that contain all the companies  
	 */
	@Override
	public synchronized Set<Company> getAllCompanies() throws DBException, IOException {

		Set<Company> companies = new HashSet<Company>();

		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COMPANY";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			while (resultSet.next()) {
				Company company = new Company();
				company.setId(resultSet.getLong(1));
				company.setCompName(resultSet.getString(2));
				company.setPassword(resultSet.getString(3));
				company.setEmail(resultSet.getString(4));
				companies.add(company);
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
		
		Logger.log(Log.info( "Get all compnies successfully"));

		return companies;
	}

	/* Print All Companies 
	 * This method print all the companies 
	 */
	public void printAllCompanies() throws DBException, IOException {

		Company company = new Company();

		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		java.sql.Statement stmt = null;

		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COMPANY";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			while (resultSet.next()) {

				company.setId(resultSet.getLong(1));
				;
				company.setCompName(resultSet.getString(2));
				company.setPassword(resultSet.getString(3));
				company.setEmail(resultSet.getString(4));

				System.out.println(company);
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
		
		Logger.log(Log.info( "Print all compnies successfully"));

	}

	/* Login
	 * This method return boolean value if the company exist 
	 */
	@Override
	public Boolean login(String compName, String password) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	/* Get company 
	 * This method return company object by company name 
	 */
	public Company getCompany(String comp_name) throws DBException, IOException {
		Company company = new Company();
		
		// Open a connection from the connection pool class 
		try {
			conn =ConnPool.getInstance().getConnection();
		} catch (Exception e) {
			throw new DBException("The Connection is faild");
		}
		// Define the Execute query
		java.sql.Statement stmt = null;
		try {
			stmt = conn.createStatement();
			// build The SQL query
			String sql = "SELECT * FROM COMPANY";
			// Set the results from the database
			ResultSet resultSet = stmt.executeQuery(sql);
			// constructor the object, retrieve the attributes from the results
			while (resultSet.next()) {
				if (resultSet.getString(2).equals(comp_name)) {

					company.setId(resultSet.getLong(1));
					company.setCompName(resultSet.getString(2));
					company.setPassword(resultSet.getString(3));
					company.setEmail(resultSet.getString(4));
				}

			}
		} catch (SQLException e) {
			// Handle errors for JDBC
			throw new DBException("get Company from Database failed");
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
		
		Logger.log(Log.info( "Get company " +company.getCompName()+ " successfully"));
		
		return company;
	

	}

	/* Get Company Coupon 
	 * This method returns a Set collection of Coupon type, contain all the company coupons 
	 */
	@Override
	public synchronized Set<Coupon> getCompanyCoupons(Company company) throws DBException, IOException {

		Set<Coupon> coupons = new HashSet<Coupon>();
		ArrayList<Long> couponIDs = new ArrayList<Long>();
		
		//Retrieve the PK of the company by name 
		Company companyLocal = new Company(); 
		companyLocal = getCompany(company.getCompName()); 

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
			String sql1 = "SELECT * FROM COMPANY_COUPON";
			// Set the results from the database,
			ResultSet resultSet = stmt.executeQuery(sql);
			ResultSet resultSet2 = stmt1.executeQuery(sql1);
			// constructor the object, retrieve the attributes from the results

			while (resultSet2.next()) {

				if (resultSet2.getLong(1) == companyLocal.getId()) {
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
					coupon.setActive(resultSet.getBoolean(10));
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
		
		Logger.log(Log.info( "Get company " +company.getCompName()+ " coupons successfully"));
		return coupons;
	}

}
