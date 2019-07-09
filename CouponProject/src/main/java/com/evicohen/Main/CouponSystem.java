package com.evicohen.Main;

import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.evicohen.DB.ConnPool;
import com.evicohen.Facade.AdminFacade;
import com.evicohen.Facade.CompanyFacade;
import com.evicohen.Facade.CouponClientFacade;
import com.evicohen.Facade.CustomerFacade;
import com.evicohen.Threads.DailyCouponExpirationTask;





/** The CouponSystem is singleton class 
 * Contains : 
 * DailyCouponExpirationTask, start and scheduling  in the  constructor 
 * Login mechanism 
 * getCouponSystem
 */
public class CouponSystem {

	private static CouponSystem instance = new CouponSystem();

	/************************************** GET-DAO ******************************/

	public enum clientType {
		Admin, Customer, Company
	};

	/**************************************CTOR***********************************/
	private CouponSystem() {
		Timer time = new Timer();    // Instantiate Timer Object
	    DailyCouponExpirationTask dailyCouponExpirationTask = new DailyCouponExpirationTask();  // Instantiate SheduledTask class
	    time.schedule(dailyCouponExpirationTask, Utils.timeScheduler(), Utils.minToMilliSec(5));// Create task repeating every selected time, in millisecond
      
	}
	
	/** This method return instance of couponSystem
	 * @return
	 */
	public static CouponSystem getCouponSystem() {
		return instance;
	}

	/** This method return CouponClientFacade object ( casting object according to the clientType ) 
	 * 
	 */
	public CouponClientFacade login(String name, String password, clientType cType) throws Exception {
		

		switch (cType) {
		case Admin:
			AdminFacade adminFacade = new AdminFacade();
			if (adminFacade.login(name, password, cType)) {
				return adminFacade;
				
			}
			else { 
				JFrame frame = new JFrame("JOptionPane showMessageDialog example");
				JOptionPane.showMessageDialog(frame, "The login failed, the Name or Password of the Admin is not valid, please try again !!!!");
				return null; 
			}

			

		case Customer:
			CustomerFacade customerFacade = new CustomerFacade();
			if(customerFacade.login(name, password, cType)) { 
				return customerFacade;
		
			}
			else { 
				JFrame frame = new JFrame("JOptionPane showMessageDialog example");
				JOptionPane.showMessageDialog(frame, "The login failed, the Name or Password of the Customer is not valid, please try again !!!!");
				return null; 
			}
			
			

		case Company:
			CompanyFacade companyFacade = new CompanyFacade();
			if (companyFacade.login(name, password, cType))  { 
				return companyFacade;
				
			}
			else { 
				JFrame frame = new JFrame("JOptionPane showMessageDialog example");
				JOptionPane.showMessageDialog(frame, "The login failed, the Name or Password of the Company is not valid, please try again !!!!");
				return null; 
			}
			
		}
		return null;
		

	}

	public void ShutDown() throws Exception {

		ConnPool.getInstance().closeAllConnections(); 
	
		// TODO - Stop the DailyTask
	}

}
