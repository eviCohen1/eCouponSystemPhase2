package com.evicohen.Services;


import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.evicohen.Exceptions.CreateException;
import com.evicohen.Exceptions.DBException;
import com.evicohen.Facade.AdminFacade;
import com.evicohen.JavaBeans.Company;
import com.evicohen.JavaBeans.Customer;
import com.evicohen.Main.CouponSystem;
import com.evicohen.Main.CouponSystem.clientType;
import com.google.gson.Gson;


@Path("admin")
public class AdminService {


	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse response;

	private AdminFacade getFacade() {

		AdminFacade admin = null;
		admin = (AdminFacade) request.getSession(false).getAttribute("adminFacade");
		if(admin == null) { 
			System.out.println("I'm here");
		}
		System.out.println(admin);
		return admin;

	}
	
	
//	private AdminFacade getFacade() throws Exception {
//
//		AdminFacade admin = null;
//		admin = (AdminFacade) CouponSystem.getCouponSystem().login("admin", "1234", clientType.Admin);
//		System.out.println(admin);
//		return admin;
//
//	}
	
	@Path("getAllCompanies")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCompnies () throws Exception {
		
		AdminFacade admin = getFacade();
		try {
			Collection<Company> companies = admin.getAllCompanies(); 		

            String resJson = new Gson().toJson(companies);
			return Response.status(Response.Status.OK).entity(resJson).build();
		} catch (Exception e) {
			// TODO: handle exception
		}

		String res = "FAILED TO get the Companies";
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();

	
		
	}
	
	@GET 
	@Path ("getCompany/{companyName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCompany (@PathParam("companyName") String companyName) throws Exception { 
		
		AdminFacade admin = getFacade() ; 
		
		try { 
			
			Company company = admin.getCompany(companyName);
			if ( company != null) { 
				String resJson = new Gson().toJson(company);
				return Response.status(Response.Status.OK).entity(resJson).build();
			}
			
			
		}catch ( Exception e ) { 
			
		}
		
		String res = "FAILED TO get the Company with company ID" + companyName;
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
		
	}
	
//	@POST 
//	@Path("CreateCompany")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response createComapny ( String jsonComString) throws Exception { 
//		
//		AdminFacade admin = getFacade(); 
//		Gson gson = new Gson(); 
//		
//
//		
//		Company companyJason = gson.fromJson(jsonComString,Company.class);  
//		
//		if ( companyJason != null && admin.createCompany(companyJason) ) { 
//			
//			return Response.ok(200).entity("Created Company" + companyJason.getCompName()).build(); 
//		}else { 
//			return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Failed to Create Company" + companyJason.getCompName()).build(); 
//		}
//
//	}

	// Create new Company, used AdminFacade //
	
	@Path("createCompany")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response createComapny(@QueryParam("name") String compName, @QueryParam("pass") String password,
			@QueryParam("email") String email) throws Exception {

	
		AdminFacade admin = getFacade();
		Company company = new Company(1111, compName, password, email);
		

		try {

			if (admin.createCompany(company)) {
				
				String res = "SUCCEED TO ADD A NEW COMPANY: name = " + compName + ", id = " + company.getId();
                String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();

			}

		} catch (DBException | CreateException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		String res ="FAILED TO CREATE A COMPANY:  " + compName;
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
	}
	
	@POST
	@Path("removeCompany")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeCompany(String jsonComString) throws Exception { 

		Gson gson = new Gson() ; 
		Company compFromJson = gson.fromJson(jsonComString, Company.class); 
		String compName = compFromJson.getCompName() ; 
		
		AdminFacade admin = getFacade(); 
		Company company = admin.getCompany(compName);
		try {
			
            if (company != null ) { 
            	admin.removeCompany(company); 
            
				String res =  "SUCCEED TO REMOVE A COMPANY: name = " + company.getCompName() ;
                String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();
            	
            }

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String res = "FAILD TO REMOVE A COMPANY, THE COMPANY :" +  company.getCompName() + "no such a company " ;
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
		
				
	} 
	
	@POST 
	@Path ("updateCompany") 
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	
	public Response updateComapny ( String jsonComString) throws Exception  {
		
		AdminFacade admin = getFacade() ; 
		Gson gson = new Gson(); 
		
		Company compFromJson = gson.fromJson(jsonComString, Company.class); 
		Company company = admin.getCompany(compFromJson.getCompName()); 
		System.out.println(company);
		try {
			
			if ( company != null ) { 
				admin.updateCompany(company,compFromJson.getPassword(),compFromJson.getEmail()); 	
				
				String res = "SUCCEED TO UPDATE A COMPANY + " +  company.getCompName() +  "  : Password = " + compFromJson.getPassword() + " Email = " + compFromJson.getEmail() ;
                String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();
			}
					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		String res = "FAILED TO UPDATE A COMPANY:" + company.getCompName();
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
		
		
		
	}
	
	@Path("createCustomer")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response createCustomer(@QueryParam("name") String custName, @QueryParam("pass") String password) throws Exception {
		
		AdminFacade admin = getFacade(); 
		Customer customer = new Customer(1111, custName, password); 
		try {
			
			if (admin.createCustomer(customer)) {
				

				String res ="SUCCEED TO ADD A NEW CUSTMER: name = " + custName ;	
                String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();
			}		
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		
		
		String res = "FAILED TO CREATE A CUSTOMER: " + custName;
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
	
	}

	@POST
	@Path("removeCustomer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	
	public Response removeCustomer(String jsonCustString) throws Exception { 

		AdminFacade admin = getFacade(); 
		Gson gson = new Gson();
		
		Customer custFromJson = gson.fromJson(jsonCustString, Customer.class);
		Customer customer = admin.getCustomer(custFromJson.getCustomerName()); 

		
		try {
			
            if (customer != null ) { 
            	admin.removeCustomer(customer); 
				String res ="SUCCEED TO REMOVE A CUSTOMER: name = " + customer.getCustomerName() ;		
                String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();
            }
			 
			String res = "FAILED TO REMOVE A CUSTOMER:  please try again " ; 
	        String resJson = new Gson().toJson(res);
			return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		String res = "FAILD TO REMOVE A CUSTOMER, THE  CUSTOMER :" +  customer.getCustomerName()+ "no such a customer "  ; 
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
				
	} 
	
	@POST 
	@Path ("updateCustomer") 
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	
	public Response updateCustomer ( String jsonCustString) throws Exception  {
		
		AdminFacade admin = getFacade() ; 
		Gson gson = new Gson(); 
		
		Customer custFromJson = gson.fromJson(jsonCustString, Customer.class); 
		Customer customer = admin.getCustomer(custFromJson.getCustomerName());
		
		try {
			
			if ( customer.getCustomerName() != null ) { 
				admin.updateCustomer(customer,custFromJson.getPassword()); 	
				String res = "SUCCEED TO UPDATE A CUSTOMER " +  customer.getCustomerName() +  "  : Password = " + custFromJson.getPassword() ; 		
                String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();
			}
					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		String res =  "FAILED TO UPDATE A CUSTOMER:" + customer.getCustomerName() ; 
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
		
		
		
	}
	
	@GET 
	@Path ("getCustomer/{custName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomer (@PathParam("custName") String custName) throws Exception { 
		
		AdminFacade admin = getFacade() ; 
		
		try { 
			
			Customer customer = admin.getCustomer(custName);
			if ( customer != null) { 
				return new Gson().toJson(customer); 
			}
			
			
		}catch ( Exception e ) { 
			
		}
		
		return null ; 
		
	}

	@Path("getAllCustomers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCustomers() throws Exception {
		
		AdminFacade admin = getFacade();
		try {
			Collection<Customer> customers = admin.getAllCustomers(); 		
            String resJson = new Gson().toJson(customers); 
			return Response.status(Response.Status.OK).entity(resJson).build();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String res =  "FAILED TO RETURN ALL CUSTOMERS " ; 
        String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
	
		
	}
}




