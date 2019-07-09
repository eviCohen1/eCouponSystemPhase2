package com.evicohen.Facade;



import java.awt.Panel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.plaf.OptionPaneUI;
import javax.swing.plaf.PanelUI;

import com.evicohen.DB.DBDAO.*;
import com.evicohen.JavaBeans.*;
import com.evicohen.Logs.*;
import com.evicohen.Main.CouponSystem.clientType;





public class AdminFacade implements CouponClientFacade {

	/**
	 * This class implements the client level of the system. The user login to the
	 * system and the instance will be according to the type of the client. This
	 * level should uses the DAO level( CompanyDBDAO, CustomerDBDAO ) In this level
	 * we will implement the logic of the program. It Contains : Login createCompany
	 * removeCompany updateCoupon getCoupon getAllCoupons
	 */
	/*************************************** Attributes *****************************************/
	private CompanyDBDAO compDAO = new CompanyDBDAO();
	private CustomerDBDAO custDAO = new CustomerDBDAO();
	private CouponDBDAO coupDAO = new CouponDBDAO(); 
	private final String name = "admin";
	private final String pass = "1234";
	Logger logger = new Logger (); 

	/***************************************** CTRO *********************************************/
	public AdminFacade() {
		// TODO Auto-generated constructor stub
	}

	/**************************************** Methods*******************************************/
	/* Login
	 * This Method return a boolean value if the name and the password valid 
	 */
	@Override
	public Boolean login(String name, String password, clientType cType) throws Exception {
		// TODO Auto-generated method stub
		 if ( name.equals(this.name) && password.equals(this.pass)) { 
			 return true; 
		 }	

		return false;
	}

	/* Create Company 
	 * This method create company
	 * Check if the company already exist 
	 */
	public boolean createCompany(Company company) throws Exception {

		Set<Company> allCompanies = new HashSet<Company>();
		allCompanies = compDAO.getAllCompanies();
		Iterator<Company> itr = allCompanies.iterator();
		while (itr.hasNext()) {
			Company company2 = new Company();
			company2 = (Company) itr.next();
			if (company2 instanceof Company && company2.getCompName().equals(company.getCompName())) { 	
				Logger.log(Log.info("Company " + company.getCompName() + " Already Exist"));
//				System.out.println("Company " + company.getCompName() + " Already Exist");
				
				return false ;
			}

		}
		compDAO.createCompany(company);
		Logger.log(Log.info("Company " + company.getCompName() + " Created"));
//		System.out.println("Company " + company.getCompName() + " Created");
		          return true; 
		}

	/* Remove Company
	 * This method remove company
	 * Update the join Table Company_Coupon and remove the company coupons
	 */
	public void removeCompany(Company company) throws Exception {

		// Update the join Table Company_Coupon and remove the company coupons
		
		compDAO.removeCompanyCoupons(company);
		// remove the company
		compDAO.removeCompany(company);
	}

	/* Update company 
	 * This method update the company details except the company name
	 * Retrieve the company object with the PK by company name
	 */
	public void updateCompany(Company company, String newPassword, String newEmail) throws Exception {

		/*Retrieve the company object with the PK by company name*/ 
		Company companyLocaly;
		System.out.println(company);
		companyLocaly = compDAO.getCompany(company.getCompName());
		/* Update the company details except the company name */
		company.setId(companyLocaly.getId());
		company.setPassword(newPassword);
		company.setEmail(newEmail);
		compDAO.updateCompany(company);
	}

	/* Get Company
	 * This method return a company by id 
	 */
	public Company getCompany(String comName) throws Exception {

		return compDAO.getCompany(comName);

	}

	/* Get all companies 
	 * This method return a set collection type Company object, contain all the companies  
	 * @throws Exception
	 */
	public Set<Company> getAllCompanies() throws Exception {

		return compDAO.getAllCompanies();
	}

	/* Create Customer 
	 * This method create customer, check if the customer already  exist
	 */
	public boolean createCustomer(Customer customer) throws Exception {
		Set<Customer> allCustomers = new HashSet<Customer>();
		allCustomers = custDAO.getAllCustomers();
		Iterator<Customer> itrIterator = allCustomers.iterator();

		while (itrIterator.hasNext()) {
			Customer customer2 = new Customer();
			customer2 = (Customer) itrIterator.next();
			if (customer2 instanceof Customer && customer2.getCustomerName().equals(customer.getCustomerName())) {
				JFrame frame = new JFrame("JOptionPane showMessageDialog example");
				JOptionPane.showMessageDialog(frame, "Customer " + customer.getCustomerName() + " Already Exist");
				Logger.log(Log.info("Customer " + customer.getCustomerName() + " Already Exist"));
				return false;
			}
		}
		custDAO.createCustomer(customer);
		Logger.log(Log.info("Customer " + customer.getCustomerName() + " Created"));
	    return true ; 
	}

	/* Remove Customer 
	 * This method remove customer from CUSTOMER table 
	 * Update Customer coupons in CUSTOMER_COUPON Table
	 */
	public void removeCustomer(Customer customer) throws Exception {

		//Update Customer coupons in CUSTOMER_COUPON Table 
		custDAO.removeCustomerCoupons(customer);
		//Remove Customer from CUSTOMER Table
	    custDAO.removeCustomer(customer);
		
	}

	/* Update Customer 
	 * This method update customer attributes 
	 */
	public void updateCustomer(Customer customer,String password ) throws Exception {

		custDAO.updateCustomer(customer,password);
	}

	/* Get customer
	 * This method return customer by customer name
	 */
	public Customer getCustomer(String custName) throws Exception {

		return custDAO.getCustomer(custName);

	}

	/* Get All Customers 
	 * This method return set collection type Customer, cpontian all the customers 
	 */
	public Set<Customer> getAllCustomers() throws Exception {

		return custDAO.getAllCustomers();
	}
	
	/* Update Coupons Expiration
	 * This method update Coupons expiration 
	 */
	public void updateCouponsExpiration() throws Exception { 
		
		coupDAO.updateCouponsExpiration();
		
	}

}
