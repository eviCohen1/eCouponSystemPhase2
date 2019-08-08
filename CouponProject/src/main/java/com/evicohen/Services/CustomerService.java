package com.evicohen.Services;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.derby.tools.sysinfo;

import com.evicohen.BusinessDelegate.BusinessDelegate;
import com.evicohen.Facade.CompanyFacade;
import com.evicohen.Facade.CustomerFacade;
import com.evicohen.JavaBeans.Company;
import com.evicohen.JavaBeans.Coupon;
import com.evicohen.JavaBeans.CouponType;
import com.evicohen.JavaBeans.Customer;
import com.evicohen.JavaBeans.Income;
import com.evicohen.JavaBeans.IncomeType;
import com.google.gson.Gson;

@Path("customer") 
public class CustomerService {
	
	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse response;
	
	private BusinessDelegate businessDelegate = new BusinessDelegate() ; 

	private CustomerFacade getFacade () throws Exception { 
		
		CustomerFacade custmer = null; 
		custmer = (CustomerFacade) request.getSession().getAttribute("customerFacade"); 
		System.out.println(custmer);
		return custmer; 
		
	}
	
    @POST
    @Path("purchaseCoupon")
    @Produces(MediaType.APPLICATION_JSON)
    public Response purchaseCoupon(String jsonComString) throws Exception{
        
    	
    	CustomerFacade customer = getFacade();
        Gson gson = new Gson();
        Coupon customerJSON =  gson.fromJson(jsonComString, Coupon.class);
        
        Date date = new Date();
        
        //Update the Income of the e-Coupon system, use Proxy BusinessDelegate//  
        if (customer.purchaseCoupon(customerJSON) && customerJSON != null){
            
        	
        	try {
        		
                Income income = new Income(0, customer.getCustmer().getCustomerName(),date.toString(),IncomeType.CUSTOMER_PURCHASE, customerJSON.getAmount()); 
              	System.out.println(businessDelegate.storeIncome(income)); 
				
			} catch (Exception e) {
				e.getMessage();
			}
        		
   		    String res = "Purchase coupon" + customerJSON.getTitle();
			String resJson = new Gson().toJson(res);
			return Response.status(Response.Status.OK).entity(resJson).build();
        }
        
		String res = "Faild to purchase coupon" + customerJSON.getTitle() ;
		String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(resJson).build();
    }
    
	@GET
	@Path("getAllPurchaseCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllPurchaseCoupons() throws Exception {

		try {
			
			CustomerFacade customerFacade = getFacade(); 
			Collection<Coupon> coupons  = customerFacade.getAllPurchasedCoupons();
			
			if(coupons != null ) { 
				
				String resJson = new Gson().toJson(coupons);
				return Response.status(Response.Status.OK).entity(resJson).build();
			}else { 
				String res = "There are no Customer Coupons";
				String resJson = new Gson().toJson(res);
				return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		String res = "There are no Customer Coupons";
		String resJson = new Gson().toJson(res);
		return Response.status(Response.Status.BAD_REQUEST).entity(resJson).build();
	}
	
	@GET
	@Path("getAllPurchasedCouponsByType/{coupType}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllPurchasedCouponsByType(@PathParam("coupType") String coupType) throws Exception {

		CustomerFacade customerFacade = getFacade(); 
		CouponType coupType1 = CouponType.valueOf(coupType);
		Collection<Coupon> coupons  = customerFacade.getAllPurchasedCouponsByType(coupType1); 
        if ( coupons != null) { 
    		return new Gson().toJson(coupons);   
        }else  { 
        	return null; 
        }

	}
	
	@GET
	@Path("getAllPurchasedCouponsByPrice/{coupPrice}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllPurchasedCouponsByPrice(@PathParam("coupPrice") double price) throws Exception {

		CustomerFacade customerFacade = getFacade(); 
		Collection<Coupon> coupons  = customerFacade.getAllPurchasedCouponsByPrice(price);
        if ( coupons != null) { 
    		return new Gson().toJson(coupons);   
        }else  { 
        	return null; 
        }

	}
	

}


