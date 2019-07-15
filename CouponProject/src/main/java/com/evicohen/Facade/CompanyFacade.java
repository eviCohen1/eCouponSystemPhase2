package com.evicohen.Facade;




import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.evicohen.DB.DBDAO.*;
import com.evicohen.JavaBeans.*;
import com.evicohen.Logs.*;
import com.evicohen.Main.CouponSystem.clientType;


public class CompanyFacade implements CouponClientFacade {
	
	
	/**
	 * This class implements the client level of the system. 
	 * The user login to the system and the instance will be according to the type of the client. 
	 * This level should uses the DAO level ( couponDBDAO, CompanyDBDAO ) 
	 * In this level we will implement the logic of the program. 
	 * It Contains : 
	 * Login
	 * createCoupon
	 * removeCoupon
	 * updateCoupon
	 * getCoupon
	 * getAllCoupons
	 */

	/**************************************Attributes*****************************************/ 
	private Company company = new Company(); 
	private Connection conn; 
	private long ID_comp ; 
	private String compName = null; 
	private String pass = null;     
	private CouponDBDAO couponDBDAO = new CouponDBDAO(); 
	private CompanyDBDAO companyDBDAO = new CompanyDBDAO(); 
   
	/***************************************CTRO*********************************************/
	public CompanyFacade() {
	}
	
	/**************************************Methods *******************************************/
	/* Login
	 * This method check if the name and the password are valid
	 * Create a instance locally of company 
	 */
	@Override
	public Boolean login(String name, String password, clientType cType) throws Exception {
		// TODO Auto-generated method stub
		this.compName = name; 
		this.pass = password;
		//Create instance locally of company 		
		company = getCompany(compName);
		//Authentication of the password and company name  
		if (company.getCompName().equals(this.compName) && company.getPassword().equals(this.pass) && company != null) { 
			Logger.log(Log.info("Company" + name + " login to the system"));
			return true;
		}
		else return false;
		
		
	}
	
	public Company getCompany() {
		return this.company;
	}
	
	
	/* Create Coupon
	 * This method create company and insert details to company table, check if the company is already exist 
	 * @throws DBException
	 */
	public Boolean createCoupon(Coupon coupon) throws Exception{ 
		
		Set<Coupon> allCoupons = new HashSet<Coupon>() ; 
		allCoupons = getCompanyCoupons(company); 
	    
		if(allCoupons != null ) { 
			Iterator<Coupon> itr = allCoupons.iterator();
			while(itr.hasNext())
			{ 
				
				Coupon coupon2 = new Coupon(); 
				coupon2 = (Coupon) itr.next(); 
				
				if (coupon2 instanceof Coupon && coupon2.getTitle().equals(coupon.getTitle())) { 
					JFrame frame = new JFrame("JOptionPane showMessageDialog example");
					JOptionPane.showMessageDialog(frame, "Coupon " + coupon.getTitle() + " Already Exist");
					Logger.log(Log.info("Coupon " + coupon.getTitle() + " Already Exist"));
					return false;		
				}
			}
		}
		 
		
		couponDBDAO.createCoupon(coupon, company);	
		Logger.log(Log.info("Created coupon " + coupon.getTitle() + " successfully"));
		return true ; 
	}
	
	/**Remove Coupon
	 * This method remove coupon from Coupon table
	 * Remove and update Customer_Coupon Table
	 * Remove and update Company_Coupon table 
	 */
	public void removeCoupon(Coupon coupon) throws Exception {
		
		//Remove and update Customer_Coupon Table
		couponDBDAO.removeCustomerCoupon(coupon);
		//Remove and update Company_Coupon table 
		couponDBDAO.removeCompanyCoupon(coupon);
		//Remove coupon from Coupon table 
		couponDBDAO.removeCoupon(coupon);
		
	}
	
	/**Update coupon 
	 * This method update coupon attributes 
	 */
	public void updateCoupon(Coupon coupon) throws Exception {
		
		couponDBDAO.updateCoupon(coupon); 
		
	}

	/**Get Coupon
	 * This method return Coupon object by id 
	 */
	public Coupon getCoupon(long id ) throws Exception {
		
		return couponDBDAO.getCoupon(id); 
		
	}
	
	/*Get all coupons 
	 * This method return set collection type Coupon,return all the coupons 
	 * @throws DBException
	 */
	public Set<Coupon> getAllCoupons() throws Exception 

	{
		Set<Coupon> allCoupons = new HashSet<Coupon>(); 
		allCoupons = couponDBDAO.getAllCoupouns(); 
		
		return allCoupons; 
		
	}
	
	/**Get Company 
	 * Return company by compName
	 */
	public Company getCompany(String compName) throws Exception {
		
		Company companyLocaly = new Company(); 
		Set<Company> allCompanies = new HashSet<Company>() ; 
		allCompanies = companyDBDAO.getAllCompanies(); 
		Iterator<Company> itr = allCompanies.iterator(); 
		
		while (itr.hasNext() ) { 
			companyLocaly = itr.next(); 
			if (companyLocaly.getCompName().equals(compName))
			{
				return companyLocaly;
			}
			 
		}
		 return null ;
		
 
		
	}

	/**Get Company Coupons 
	 * This method return set collection type Coupon, contain company coupons 
	 */
	public Set<Coupon> getCompanyCoupons(Company company) throws Exception { 
		
		Set<Coupon> allCoupons = new HashSet<Coupon>() ; 
 		allCoupons =companyDBDAO.getCompanyCoupons(company);
		
				if(!(allCoupons.isEmpty())) {
					
					return allCoupons;
				}
				else 
				{ 
					Logger.log(Log.info("To a comapny, " + company.getCompName() + " hasn't coupons"));
					return null;
				}
		
	}


	/**This method return a set collection type Coupon, contain coupons by type  
	 * @throws DBException
	 */
	public synchronized Set<Coupon> getCouponByType(CouponType type) throws Exception {
		
		Set<Coupon> coupons2 = new HashSet<Coupon>();
		Set<Coupon> coupons = new HashSet<Coupon>();
		coupons = getCompanyCoupons(company);
		Iterator<Coupon> itr = coupons.iterator(); 

			while (itr.hasNext()) {
				Coupon coupon2 = new  Coupon(); 
				coupon2 = itr.next();
				// Check the type of the Coupon
				if (type.equals(coupon2.getType())) {

					coupons2.add(coupon2);
                
				}

			}
			if(coupons2.isEmpty()) { 
			
					JFrame frame = new JFrame("JOptionPane showMessageDialog example");
					JOptionPane.showMessageDialog(frame, "To comapny, " + company.getCompName() + " hasn't coupons in type" +type.name()); 
					Logger.log(Log.info("To comapny, " + company.getCompName() + " hasn't coupons in type" +type.name()));
					return null;
			}

		return coupons2;
	}
	
	/** Get Coupons By type  
	 * This method return set collection type coupon, contain coupon that lower than the price limit  
	 * @throws DBException
	 */
	public synchronized Set<Coupon> getCouponsByPrice(Double priceLimt) throws Exception {
		
		Set<Coupon> coupons2 = new HashSet<Coupon>();
		Set<Coupon> coupons = new HashSet<Coupon>();
		coupons = getCompanyCoupons(company);
		Iterator<Coupon> itr = coupons.iterator(); 

			while (itr.hasNext()) {
				Coupon coupon2 = new  Coupon(); 
				coupon2 = itr.next();
				// Check the type of the Coupon
				System.out.println(coupon2.getPrice());
				if (priceLimt >= coupon2.getPrice()) {

					coupons2.add(coupon2);
                
				}

			}
			if(coupons2.isEmpty()) { 
				
				JFrame frame = new JFrame("JOptionPane showMessageDialog example");
				JOptionPane.showMessageDialog(frame, "To comapny , " + company.getCompName() + " hasn't coupons that cost " + priceLimt ); 
				Logger.log(Log.info("To comapny , " + company.getCompName() + " hasn't coupons that cost " + priceLimt ));
				return null;
		}

		return coupons2;
		
	}	
	
	/** Get Coupons by Expired Date 
	 * This method return set collection type coupon, contain coupons that they don't valid 
	 * @throws DBException
	 */
	public synchronized Set<Coupon> getCouponsByExpiredDate(java.util.Date date) throws Exception {
		
		Set<Coupon> coupons2 = new HashSet<Coupon>();
		Set<Coupon> coupons = new HashSet<Coupon>();
		coupons = getCompanyCoupons(company);
		Iterator<Coupon> itr = coupons.iterator(); 

			while (itr.hasNext()) {
				Coupon coupon2 = new  Coupon(); 
				coupon2 = itr.next();
				// Check the type of the Coupon
				if (date.compareTo(coupon2.getEndDate())>0) {

					coupons2.add(coupon2);
                
				}

			}
			if(coupons2.isEmpty()) { 
				
				JFrame frame = new JFrame("JOptionPane showMessageDialog example");
				JOptionPane.showMessageDialog(frame, "To comapny , " + company.getCompName() + " hasn't coupons before date" + date ); 
				Logger.log(Log.info("To comapny , " + company.getCompName() + " hasn't coupons before date" + date ));
				return null;
		}

		return coupons2;
	}
	
	


}
