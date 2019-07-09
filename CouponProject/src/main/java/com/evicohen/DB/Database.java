package com.evicohen.DB;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;

import com.evicohen.Exceptions.DBException;
import com.evicohen.Main.Utils;



public class Database {

	/*
	 * Singleton class
	 */
    /*****************************************Attributes ********************************/
	private static Database instance = new Database();
	static Connection conn;

	/********************************************CTOR************************************/
	private Database() {
	}
	
	/*****************************************Methods************************************/
	/*Get Database 
	 * This methods return instance of database 
	 */
	public static Database getDatabase() {
		return instance;
	}

	/* Create Tables 
	 * This method create the tables (COMPANY,CUSTOMER,COUPON,COMPANY_COUPON,CUSTOMER_COUPON)
	 * @throws DBException
	 * @throws SQLException
	 */
	public static void createTables() throws Exception {

		// Connection
			Class.forName(Utils.getDriverData());
		try {

			conn = DriverManager.getConnection(Utils.getDBUrl());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new DBException("The Connection is faild");
		}
		String sql;

		// Table 1 creation (Company)
		try {
			java.sql.Statement stmt = conn.createStatement();
			sql = "create table Company("
					+ "ID bigint NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1), "
					+ "COMP_NAME varchar(30) not null, " 
					+ "PASSWORD varchar(30) not null,"
					+ "EMAIL varchar(30) not null)";
			stmt.executeUpdate(sql);
			System.out.println("success:" + sql);
		} catch (SQLException e) {
			// TODO: handle exception
			throw new DBException("The create a Company table is failed");
		}

		// Table 2 creation (Customer)
		try {
			java.sql.Statement stmt2 = conn.createStatement();
			sql = "create table Customer("
					+ "ID bigint not null primary key generated always as identity(start with 1, increment by 1), "
					+ "CUST_NAME varchar(30) not null, " 
					+ "PASSWORD varchar(30) not null)";
			stmt2.executeUpdate(sql);
			System.out.println("success:" + sql);
		} catch (SQLException e) {
			throw new DBException("The create a Customer table is failed");
		}

		// Table 3 creation (Coupon)
		try {
			java.sql.Statement stmt3 = conn.createStatement();
			sql = "create table Coupon("
					+ "ID bigint not null primary key generated always as identity(start with 1, increment by 1), "
					+ "TITLE varchar(30) not null, " 
					+ "START_DATE DATE not null, " 
					+ "END_DATE DATE not null,"
					+ "AMOUNT INTEGER not null," 
					+ "TYPE varchar(10) not null," 
					+ "MESSAGE varchar(30) not null,"
					+ "PRICE double not null," 
					+ "IMAGE varchar(200) not null," 
					+ "ACTIVE BOOLEAN NOT NULL)";
			stmt3.executeUpdate(sql);
			System.out.println("success:" + sql);
		} catch (SQLException e) {
			throw new DBException("The create a Coupon table is failed");
		}

		// Table 4 creation (Customer_Coupon - Join table)
		try {
			java.sql.Statement stmt4 = conn.createStatement();
			sql = "create table Customer_Coupon(" 
			        + "CUST_ID bigint not null REFERENCES Customer(ID),"
					+ "COUPON_ID bigint not null REFERENCES Coupon(ID))";
			stmt4.executeUpdate(sql);
			System.out.println("success:" + sql);
		} catch (SQLException e) {
			throw new DBException("The create a Customer_Coupon table is failed");
		}

		// Table 5 creation (Company_Coupon - Join table)
		try {
			java.sql.Statement stmt5 = conn.createStatement();
			sql = "create table Company_Coupon(" 
			        + "COMP_ID bigint not null REFERENCES Company(ID),"
					+ "COUPON_ID bigint not null REFERENCES Coupon(ID))";
			stmt5.executeUpdate(sql);
			System.out.println("success:" + sql);

		} catch (SQLException e) {
			throw new DBException("The create a Company_Coupon table is failed");
		} finally {
			// finally block used to close resources
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				throw new DBException("The close connection action faild");
			}

		}
	}

}