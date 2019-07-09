package com.evicohen.Services;

import java.sql.Date;
import java.text.SimpleDateFormat;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.evicohen.Facade.AdminFacade;
import com.evicohen.Facade.CompanyFacade;
import com.evicohen.JavaBeans.Company;
import com.evicohen.JavaBeans.Coupon;
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
		Gson gson = new Gson();
		Coupon couponJason = gson.fromJson(jsonCouponString, Coupon.class);
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
					coupon.setActive(couponJason.getActive());
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
				return Response.ok(200).entity("Created Company" + coupon.getTitle()).build();
			} else {
				return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
						.entity("Failed to Create Coupon" + coupon.getTitle()).build();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;

	}

	@GET
	@Path("getAllCompanyCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCompanyCoupons() throws Exception {

		CompanyFacade companyFacade = getFacade();
        Company company = companyFacade.getCompany(); 
		Collection<Coupon> coupons = companyFacade.getCompanyCoupons(company);

		return new Gson().toJson(coupons);
	}

	@DELETE
	@Path("removeCoupon/{CoupId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String removeCoupon(@PathParam("CoupId") Long id) throws Exception {

		CompanyFacade companyFacade = getFacade();
		Coupon coupon = companyFacade.getCoupon(id);

		try {

			if (coupon != null) {
				companyFacade.removeCoupon(coupon);
				return "SUCCEED TO REMOVE A COUPON: name = " + coupon.getTitle() + ", id = " + id;

			}

			return "FAILED TO REMOVE A COMPANY :  please try again ";
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "FAILD TO REMOVE A COMPANY, THE COMPANY :" + coupon.getTitle();

	}

	@POST
	@Path("updateCoupon")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	public Response updateCoupon(String jsonComString) throws Exception {

		CompanyFacade companyFacade = getFacade();
		Gson gson = new Gson();
		Coupon couponJason = gson.fromJson(jsonComString, Coupon.class);
		Coupon coupon = companyFacade.getCoupon(couponJason.getId());

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
					coupon.setActive(couponJason.getActive());
				} else {
					return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid Active atribute ")
							.build();
				}

			}
			
			if (coupon != null ) {
				companyFacade.updateCoupon(coupon);
				System.out.println(coupon);
				return Response.ok(200).entity("Update coupon" + coupon.getTitle()).build();
			} 


		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Faild to remove the coupon")
				.build();

	}

}
