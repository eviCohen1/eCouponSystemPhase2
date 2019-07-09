package com.evicohen.Facade;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.evicohen.DB.DBDAO.*;
import com.evicohen.JavaBeans.*;
import com.evicohen.Logs.*;
import com.evicohen.Main.CouponSystem.clientType;


public class CustomerFacade implements CouponClientFacade {

	/**
	 * This class implements the client level of the system. The user login to the
	 * system and the instance will be according to the type of the client. This
	 * level should uses the DAO level( CompanyDBDAO, CustomerDBDAO ) In this level
	 * we will implement the logic of the program. It Contains : Login
	 * purchaseCoupon getAllPurchasedCoupons getAllPurchasedCouponsByType
	 * getAllPurchasedCouponsByPrice getAllCoupons
	 */

	/***************************************
	 * Attributes
	 *****************************************/
	private Customer customerLocaly = new Customer();
	private String CUST_NAME = null;
	private String pass = null;
	private clientType clientType = null;
	private Connection conn;
	private CustomerDBDAO customerDBDAO = new CustomerDBDAO();
	private CouponDBDAO couponDBDAO = new CouponDBDAO();

	/**************************************
	 * CTOR
	 ***********************************************/
	public CustomerFacade() {
		// TODO//

	}

	/*************************************
	 * Methods
	 * 
	 * @throws Exception
	 ********************************************/
	/*
	 * Login This method check if the name and the password are valid Create a
	 * instance locally of company
	 */
	@Override
	public Boolean login(String name, String password, clientType cType) throws Exception {
		this.CUST_NAME = name;
		this.pass = password;
		this.clientType = cType;
		this.customerLocaly = customerDBDAO.getCustomer(CUST_NAME);
		// Authentication of the password and company name
		if (customerLocaly.getCustomerName().equals(this.CUST_NAME) && customerLocaly.getPassword().equals(this.pass)
				&& customerLocaly != null) {
			Logger.log(Log.info("Customer" + name + " succeed to login to the coupon system"));
			return true;
		} else
			return false;
	}
	
	public Customer getCustmer() { 
		
		return customerLocaly; 
	}

	/**
	 * Purchase Coupon This method purchase coupon Check if the customer already
	 * bought this coupon Check if the coupon is available, amount and the expired
	 * date.
	 * 
	 * @throws Exception
	 */
	public void purchaseCoupon(Coupon coupon) throws Exception {

		Set<Coupon> allCoupons = new HashSet<Coupon>();
		Coupon coupon1 = new Coupon();
		Coupon coupon2 = new Coupon();
		allCoupons = customerDBDAO.getCustomerCoupons(customerLocaly);
		Iterator<Coupon> itr = allCoupons.iterator();
		// Check if the customer already have the coupon
		while (itr.hasNext()) {
			Coupon coupon3 = new Coupon();
			coupon3 = itr.next();
			if (coupon3.getTitle().equals(coupon.getTitle())) {
				JFrame frame = new JFrame("JOptionPane showMessageDialog example");
				JOptionPane.showMessageDialog(frame, "The Coupon " + coupon.getTitle() + " is already exist");
				Logger.log(Log.info("The Coupon " + coupon.getTitle() + " for customer "
						+ customerLocaly.getCustomerName() + " is already exist"));

				return;
			}

		}

		// Check if the coupon is available
		coupon1 = couponDBDAO.getCouponByTitle(coupon.getTitle());

		if (coupon1.getAmount() > 0 && coupon1.getActive()) {
			// Update the amount of the coupon
			coupon2 = coupon1;
			coupon2.setAmount(coupon1.getAmount() - 1);
			if (coupon2.getAmount() == 0) {

				coupon2.setActive(false);

			}
			couponDBDAO.updateCoupon(coupon2);
			customerDBDAO.purchaseCoupon(coupon, customerLocaly);
			Logger.log(Log.info("The Coupon " + coupon.getTitle() + " amount is updated to " + coupon2.getAmount()));
		} else {
			JFrame frame = new JFrame("JOptionPane showMessageDialog example");
			JOptionPane.showMessageDialog(frame, "The Coupon " + coupon.getTitle() + " is not availble");
			Logger.log(Log.info("The Coupon " + coupon.getTitle() + " is not availble"));
		}

	}

	/**
	 * Get All Purchased Coupons This method returns a set collection type coupon,
	 * contain all the Purchased coupons
	 * 
	 * @throws Exception
	 */
	public Set<Coupon> getAllPurchasedCoupons() throws Exception {

		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = customerDBDAO.getCustomerCoupons(customerLocaly);

		if (!(allCoupons.isEmpty())) {

			return allCoupons;
		} else {
			JFrame frame = new JFrame("JOptionPane showMessageDialog example");
			JOptionPane.showMessageDialog(frame, "To Customer," + customerLocaly.getCustomerName() + " hasn't coupons");
			Logger.log(Log.info("To Customer," + customerLocaly.getCustomerName() + " hasn't coupons"));
			return null;
		}

	}

	/**
	 * Get all Purchased coupons by type This method return all the purchased
	 * coupons by type
	 */
	public Set<Coupon> getAllPurchasedCouponsByType(CouponType cType) throws Exception {

		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = customerDBDAO.getCustomerCoupons(customerLocaly);
		Set<Coupon> allCouponsByType = new HashSet<Coupon>();
		Coupon coupon = new Coupon();
		JFrame frame = new JFrame("JOptionPane showMessageDialog example");

		if (!(allCoupons.isEmpty())) {
			Iterator<Coupon> itr = allCoupons.iterator();
			while (itr.hasNext()) {
				coupon = itr.next();
				if (coupon.getType().equals(cType)) {

					allCouponsByType.add(coupon);
				}
			}
			if (allCouponsByType.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "To Customer," + customerLocaly.getCustomerName()
						+ " hasn't coupons with type " + cType.name());
				Logger.log(Log.info("To Customer," + customerLocaly.getCustomerName() + " hasn't coupons with type "
						+ cType.name()));
				return null;
			} else {
				return allCouponsByType;
			}

		} else {

			JOptionPane.showMessageDialog(frame,
					"To Customer," + customerLocaly.getCustomerName() + " hasn't coupons ");
			Logger.log(Log.info("To Customer," + customerLocaly.getCustomerName() + " hasn't coupons "));
			return null;
		}
	}

	/**
	 * Get all purchased coupon by price This method return set collection coupon
	 * type, contain all purchased coupons by price
	 * 
	 * @throws Exception
	 */
	public Set<Coupon> getAllPurchasedCouponsByPrice(double price) throws Exception {
		// Get all customer coupons
		Set<Coupon> allCoupons = new HashSet<Coupon>();
		allCoupons = customerDBDAO.getCustomerCoupons(customerLocaly);
		Set<Coupon> allPurchasedCouponsByType = new HashSet<Coupon>();
		Coupon coupon = new Coupon();
		JFrame frame = new JFrame("JOptionPane showMessageDialog example");

		if (!(allCoupons.isEmpty())) {
			Iterator<Coupon> itr = allCoupons.iterator();

			while (itr.hasNext()) {
				coupon = itr.next();
				if (coupon.getPrice() <= price) {

					allPurchasedCouponsByType.add(coupon);
				}
			}
			if (allPurchasedCouponsByType.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "To Customer " + customerLocaly.getCustomerName()
						+ " hasn't coupons with price lower than " + price + "[NIS]");
				Logger.log(Log.info("To Customer," + customerLocaly.getCustomerName()
						+ " hasn't coupons with price lower than " + price + "[NIS]"));
				return null;
			} else {
				return allPurchasedCouponsByType;
			}

		} else {

			JOptionPane.showMessageDialog(frame,
					"To Customer," + customerLocaly.getCustomerName() + " hasn't coupons ");
			Logger.log(Log.info("To Customer," + customerLocaly.getCustomerName() + " hasn't coupons "));
			return null;
		}
	}

}
