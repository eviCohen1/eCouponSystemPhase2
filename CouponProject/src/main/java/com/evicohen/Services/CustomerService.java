package com.evicohen.Services;

import java.util.Collection;

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

import com.evicohen.Facade.CompanyFacade;
import com.evicohen.Facade.CustomerFacade;
import com.evicohen.JavaBeans.Company;
import com.evicohen.JavaBeans.Coupon;
import com.evicohen.JavaBeans.CouponType;
import com.evicohen.JavaBeans.Customer;
import com.google.gson.Gson;

@Path("customer") 
public class CustomerService {
	
	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse response;

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
        
        if (customerJSON != null){
            customer.purchaseCoupon(customerJSON);

            return Response.ok(200).entity("Purchase coupon" + customerJSON.getTitle()).build();
        }
        return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Faild to purchase coupon" + customerJSON.getTitle() )
				.build();
    }
    
	@GET
	@Path("getAllPurchaseCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllPurchaseCoupons() throws Exception {

		CustomerFacade customerFacade = getFacade(); 

		Collection<Coupon> coupons  = customerFacade.getAllPurchasedCoupons(); 

		return new Gson().toJson(coupons);
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


