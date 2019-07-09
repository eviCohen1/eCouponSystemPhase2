package com.evicohen.Threads;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.TimerTask;

import javax.xml.crypto.Data;

import com.evicohen.DB.DBDAO.*;
import com.evicohen.Logs.*;




/** This class update daily the coupons are expired, the task is thread that implements Runnable interface 
 * @author evic
 *
 */

public class DailyCouponExpirationTask extends TimerTask implements Runnable {

	/****************************************** Attributes************************************/
	public Boolean quit;
	CouponDBDAO couponDBDAO = new CouponDBDAO();
	Logger logger = new Logger(); 

	/******************************************** CTOR***************************************/
	public DailyCouponExpirationTask() {

	}

	/******************************************** Methods************************************/
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {

        this.quit  = true ; 
		try {
			while (quit) {
				
				couponDBDAO.updateCouponsExpiration();
				Thread.sleep((long) 1000);		//sleep one second, i can use the  sleep method to schedule the daily task ( Should define  86400000 millisecond) 
				terminate();   //terminate the task 
				Logger.log(Log.info("Daily Task, update Coupons expiration "));
			}
		} catch (Exception e) {
			System.out.println("error running thread " + e.getMessage());
			terminate();
		}
	}
	/* This method kill the task 
	 * @see java.util.TimerTask#run()
	 */
	public void terminate() {		
		this.quit = false; 
		
	}

}
