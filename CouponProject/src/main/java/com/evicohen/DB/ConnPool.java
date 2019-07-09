package com.evicohen.DB;

import java.beans.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;




import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.evicohen.Exceptions.*;
import com.evicohen.Logs.*;
import com.evicohen.Main.*;






public class ConnPool {
	
	/**
	 * Singleton class, return instance of Connpool
	 * The Connection pooling is a well-known data access pattern,whose main purpose is to reduce the overhead involved in performing 
	 * database connections and read/write database operations. 
	 * a connection pool is, at the most basic level, a database connection cache implementation
	 * It Contains: 
	 * BlockingQueue<Connection> - A Queue that additionally supports operations that wait for the queue to become non-empty when retrieving an element, 
	 * and wait for space to become available in the queue when storing an element.
	 * methods : 
	 * Insert - offer(e)
	 * Remove -poll()
	 * Examine - peek() 
	 */
	
	/****************************************************Attributes**********************************/ 
	private static ConnPool instance;
	private final int MaxConNumber = 10; 
	private BlockingQueue<Connection> conQ = new LinkedBlockingDeque<>(MaxConNumber);
	private java.sql.Statement stmtStatement;

	
	
	/** Connection pool constructor 
	 * Class.forName start the connection to the derby driver
	 * Check if the tables exist, if not create tables 
	 * open 10 connections and insert them to the blocking queue  
	 * @throws Exception
	 */
	private ConnPool() throws Exception {
		
		Class.forName(Utils.getDriverData());
		
		//TODO - Read about reflection - Design pattern 
	
		Connection con = DriverManager.getConnection(Utils.getDBUrl());
		DatabaseMetaData metaData; 
		ResultSet tabelResultSet;  
		/*Retrieves the meta data of the tables */
		metaData = con.getMetaData(); 
	    stmtStatement = con.createStatement(); 
	    /*Check if the tables exist, in not create tables */
	    tabelResultSet = metaData.getTables(null,"APP","CUSTOMER",null); 
        if(!tabelResultSet.next()) { 

        	Database.createTables();
        	Logger.log(Log.info("Creat Tables successfully"));
        }
        con.close();
        
        /*open 10 connections and insert them to the blocking queue  */
		while (conQ.size() < MaxConNumber) {
			con = DriverManager.getConnection(Utils.getDBUrl());
			conQ.offer(con);
		}
	}
	
	/**
	 * Get Instance 
	 * This method create new instance and return it, this class is singleton so the instance type is Connpool 
	 */
	public static ConnPool getInstance() throws Exception {
		
		if (instance==null){
			try {
				instance = new ConnPool();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new DBException ("Failed to return instansiation of connection to the DB"); 
			}
		}
		
		return instance;
	}
	
	/*Get Connection 
	 * This class return connection from the connection pool  
	 * poll method - get a connection from the connection pool
	 * @throws DBException
	 */
	public synchronized Connection getConnection()throws Exception { 
		try{
			Connection c=conQ.poll();
			c.setAutoCommit(true);
			return c;
		} catch (SQLException e) {
			
			throw new DBException("failed to connect server");			
		}
	}
	
	/** Return Connection 
	 *  This method get a connection and return it to the connection pool 
	 *  offer - return method 
	 */
	public synchronized void returnConnection(Connection connection) {
		conQ.offer(connection); 
		
	}
	
	/** Close all the connection 
	 * This method close all the open
	 * peek method - check if the connection is open 
	 * @throws IOException 
	 * @throws DBException 
	 */
	public void closeAllConnections() throws Exception {
		Connection connection;
		while (conQ.peek()!=null){
			connection=conQ.poll();
			try {
				connection.close();
			} catch (SQLException e) {
				//throw new CouponException("Unable to close all connections");
				throw new DBException("Unable to close all connections");
			}
		}
	}
	
	/**Print the available connections
	 * This method print and return all the available connection 
	 */
	public int printTheAvilabelConnections() { 	
		System.out.println("The avilable connections: " + this.conQ.size());
		return this.conQ.size(); 
	}
	

}