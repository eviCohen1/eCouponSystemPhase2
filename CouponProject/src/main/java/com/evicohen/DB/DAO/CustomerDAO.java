package com.evicohen.DB.DAO;



import java.util.ArrayList;
import java.util.Set;

import com.evicohen.JavaBeans.Coupon;
import com.evicohen.JavaBeans.Customer;



public interface CustomerDAO {
	
	/**
	 * This interface defines all the methods are should implement in the CustomerDBDAO. 
	 * It Contains : 
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
	 * @throws Exception
	 * login 
	 */

	/* Create Customer method  
	 * This method created customer and insert the attributes to the Customer table 
	 * Attributes : Customer name, Password 
	 */
	void createCustomer(Customer customer) throws Exception; 
	
	/* Remove Customer method 
	 * This method remove customer from customer table, created a local object in order to get the PK. 
	 */
	void removeCustomer(Customer customer) throws  Exception;
	
	/* Remove Customer Coupon 
	 * This method remove all the customer coupon from the join table Customer_Coupon.
	 */
	public void removeCustomerCoupons(Customer customer) throws Exception;

	/* Update Customer 
	 * This method update the customer password
	 */
	void updateCustomer(Customer customer , String password) throws Exception;

	/* Get customer 
	 * This method return a customer object by customer name 
	 */
	public Customer getCustomer(String CUST_NAME) throws Exception;
	
	/* Get all Customers
	 * This method returns Set collection of customer object that contain all the customers 
	 */
	public Set<Customer> getAllCustomers() throws Exception;	
	
	/* Login 
	 * This method return boolean value if the customer exist  
	 */
	public Boolean login(String custName, String password) throws Exception; 
	
	/* Get Customer By Id 
	 * This method return a customer object by customer's ID 
	 */
	public Customer getCustomerById(long id) throws  Exception;
	
	/* Get Customer Coupons
	 * This method return Set collection of customer coupons  
	 */
	public Set<Coupon> getCustomerCoupons(Customer customer) throws Exception;
	
	/* Print All Customers 
	 * This method print all the customers 
	 */
	public void printAllCustmers() throws Exception;
	
	/* Purchase Coupon 
	 * This method insert the coupon's id and the company's id to the join table Coupon_Customer  
	 */
	public void purchaseCoupon(Coupon coupon, Customer customer) throws Exception; 
	
	
}
