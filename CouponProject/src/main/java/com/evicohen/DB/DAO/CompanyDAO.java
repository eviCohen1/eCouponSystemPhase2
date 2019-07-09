package com.evicohen.DB.DAO;

import java.util.Set;

import com.evicohen.JavaBeans.*;


public interface CompanyDAO {
	
	/**
	 * This interface defines all the methods are should implement in the CustomerDBDAO. 
	 * It Contains : 
	 * createCompany
	 * removeComapny 
	 * updateComapny 
	 * getCompany by name and by ID
	 * getAllCompanies
	 * getCoupons
	 * login 
	 * @throws Exception
	 */
      
		/**
		 * Create Company 
		 * This method create company and insert to company table 
		 */
		void createCompany(Company company ) throws Exception; 

		/* Remove Company
		 * This method remove company from company table
		 */
		void removeCompany(Company company) throws Exception;
		
		/* Remove Company coupons 
		 * Remove all the company coupons from the join table
		 * Remove all the company coupons from coupon table
		 */
		public void removeCompanyCoupons (Company company) throws Exception;

		/* Update Company
		 * This method update company attributes, password and Email   
		 */
		void updateCompany(Company company) throws Exception;

		/* Get Company
		 * This method return company object by id  
		 */
		Company getCompany(long id) throws Exception;
		
		/**
		 * @return
		 * @throws DBException
		 */
		Set<Company> getAllCompanies() throws Exception;	
		
		/* Get All Companies 
		 * This method return Set collection of Company object that contain all the companies  
		 */
		public void printAllCompanies() throws Exception;
		
		/* Print All Companies 
		 * This method print all the companies 
		 */
		public Set<Coupon> getCompanyCoupons(Company company) throws Exception; 
		
		/* Login
		 * This method return boolean value if the company exist 
		 */
		Boolean login(String compName, String password) throws Exception; 
	
		/* Get company 
		 * This method return company object by company name 
		 */
		public Company getCompany(String comp_name) throws Exception;
		
		
		
	}