package com.evicohen.Services;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.evicohen.BusinessDelegate.BusinessDelegate;
import com.evicohen.Facade.AdminFacade;
import com.evicohen.Facade.CompanyFacade;
import com.evicohen.JavaBeans.Company;
import com.evicohen.JavaBeans.Coupon;
import com.evicohen.JavaBeans.Income;
import com.evicohen.JavaBeans.IncomeType;
import com.evicohen.Main.CouponSystem;
import com.evicohen.Main.CouponSystem.clientType;
import com.evicohen.Main.Utils;
import com.google.gson.Gson;

@Path("company")
public class CompanyService {

	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse response;
	
	private BusinessDelegate businessDelegate = new BusinessDelegate() ; 
	Date date = new Date();
	Gson gson = new Gson();	
	private CompanyFacade getFacade() throws Exception {

		CompanyFacade company = null;
		company = (CompanyFacade) request.getSession().getAttribute("companyFacade");
		System.out.println(company);
		return company;
	}

	@GET
	@Path("getCompany/{compName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCompany(@PathParam("compName") String compName) throws Exception {

		CompanyFacade companyFacade = getFacade();

		try {

			Company company = (Company) companyFacade.getCompany(compName);
			System.out.println(companyFacade);
			System.out.println(company.toString());
			if (company != null) {
				return new Gson().toJson(company);
			}

		} catch (Exception e) {

		}

		return null;

	}

	@POST
	@Path("createCoupon")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCoupon(String jsonCouponString) throws Exception {

		CompanyFacade companyFacade = getFacade();
		
		
		Coupon couponJason = gson.fromJson(jsonCouponString, Coupon.class);
		System.out.println(couponJason);
		Coupon coupon = new Coupon();

		try {

			if (couponJason != null) {

				if (couponJason.getTitle() != null && couponJason.getTitle() != "") {
					coupon.setTitle(couponJason.getTitle());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid coupon title")
							.build();
				}
				if (couponJason.getAmount() > 0) {
					coupon.setAmount(couponJason.getAmount());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid coupon amount")
							.build();
				}

				if (couponJason.getMessage() != null && couponJason.getMessage() != "") {
					coupon.setMessage(couponJason.getMessage());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid coupon message")
							.build();
				}
				if (couponJason.getPrice() > 0) {
					coupon.setPrice(couponJason.getPrice());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid coupon price")
							.build();
				}

				if (couponJason.getImage() != null && couponJason.getImage() != "") {
					coupon.setImage(couponJason.getImage());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid coupon Image")
							.build();
				}

				if (couponJason.getType() != null) {
					coupon.setType(couponJason.getType());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid coupon Type")
							.build();
				}

				if (couponJason.getActive() != null) {
					if (couponJason.getActive() == true) {
						coupon.setActive(true);
					} else
						coupon.setActive(false);

				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid Active atribute ")
							.build();
				}

				coupon.setStartDate(Utils.getDate());
				coupon.setEndDate(Utils.endDate(30));
				

			} else

			{
				return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
						.entity("Failed to Create Coupon" + couponJason.getTitle()).build();
			}

			if (companyFacade.createCoupon(coupon)) {
				
		        //Update the Income of the e-Coupon system, use Proxy BusinessDelegate//  
		            	
		        	try {
		        		
		                Income income = new Income(0, companyFacade.getCompany().getCompName(),date.toString(),IncomeType.COMPANY_NEW_COUPON, 100); 
		              	System.out.println(businessDelegate.storeIncome(income)); 
						
					} catch (Exception e) {
						e.getMessage();
					}
		        	
		        	
				String res  = "Created Company Coupon " + coupon.getTitle(); 
				String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();
                
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
				.entity("Failed to Create Coupon" + coupon.getTitle()).build();

	}

	@GET
	@Path("getAllCompanyCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCompanyCoupons() throws Exception {

		CompanyFacade companyFacade = getFacade();

		try {
			Company company = companyFacade.getCompany();
			Collection<Coupon> coupons = companyFacade.getCompanyCoupons(company);

			if (coupons != null) {

				String resJson = new Gson().toJson(coupons);
				return Response.status(Response.Status.OK).entity(resJson).build();
			} else {
				String res = "There are no Company Coupons";
				String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		String res = "FAILED TO get the Company Coupons";
		String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();

	}
	
	@GET
	@Path("viewIncomeByCompeny")
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewIncomeByCompeny() throws Exception  { 
		
		CompanyFacade companyFacade = getFacade();
		
		try {
			Company company = companyFacade.getCompany();
			
			List<Income> income = businessDelegate.viewIncomeByCompany(company.getCompName());
			
			if(income != null ) { 
				String resJson = new Gson().toJson(income); 
				return Response.status(Response.Status.OK).entity(resJson).build(); 
			}else { 
				String res = "There are no Company OutCome";
				String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		String res = "FAILED TO get the Company OutCome";
		String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
		
	}

	@POST
	@Path("removeCoupon")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeCoupon(String jsonCouponString) throws Exception {

		Coupon couponFromJson = gson.fromJson(jsonCouponString, Coupon.class);

		CompanyFacade companyFacade = getFacade();
		Coupon coupon = companyFacade.getCoupon(couponFromJson.getId());

		try {

			if (coupon != null) {
				companyFacade.removeCoupon(coupon);
				String res = "SUCCEED TO REMOVE A COUPON: name = " + coupon.getTitle() + ", id = " + coupon.getId();
				String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();
			}

			String res = "FAILED TO REMOVE A COMPANY :  please try again ";
			String resJson = new Gson().toJson(res);
			return Response.status(Response.Status.OK).entity(resJson).build();
		} catch (Exception e) {
			// TODO: handle exception
		}

		String res = "FAILD TO REMOVE A COMPANY, THE COMPANY :" + coupon.getTitle();
		String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.OK).entity(resJson).build();
	}

	@POST
	@Path("updateCoupon")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCoupon(String jsonComString) throws Exception {
		
		
		CompanyFacade companyFacade = getFacade();
		
		Coupon couponJason = gson.fromJson(jsonComString, Coupon.class);
		Coupon coupon = new Coupon();
		try {

			if (couponJason != null) {
				if (couponJason.getTitle() != null && couponJason.getTitle() != "") {
					coupon.setTitle(couponJason.getTitle());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid coupon title")
							.build();
				}
				if (couponJason.getAmount() > 0) {
					coupon.setAmount(couponJason.getAmount());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid coupon amount")
							.build();
				}

				if (couponJason.getActive() != null) {
					coupon.setActive(couponJason.getActive());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid Active atribute ")
							.build();
				}

			}
			
			coupon.setStartDate(Utils.getDate());
			coupon.setEndDate(Utils.endDate(30));
			
			if (coupon != null) {
				
		        //Update the Income of the e-Coupon system, use Proxy BusinessDelegate//  
	
	        		
	            Income income = new Income(0, companyFacade.getCompany().getCompName(),date.toString(),IncomeType.COMPANY_UPDATE_COUPON, 10); 
	            System.out.println(businessDelegate.storeIncome(income)); 
					
				companyFacade.updateCoupon(coupon);
				String res = "Update coupon" + coupon.getTitle();
				String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.OK).entity(resJson).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Faild to remove the coupon").build();

	}

}
