package com.evicohen.Services;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.evicohen.Facade.AdminFacade;
import com.evicohen.Facade.CompanyFacade;
import com.evicohen.Facade.CouponClientFacade;
import com.evicohen.Facade.CustomerFacade;
import com.evicohen.JavaBeans.User;
import com.evicohen.Main.CouponSystem;
import com.evicohen.Main.CouponSystem.clientType;
import com.google.gson.Gson;

@Path("LoginService")
public class LoginService {

	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse response;

	@POST
	@Path("login")
	public Response login(String jsonUserString) throws Exception {
        
		// check whether there is a open session
		HttpSession session = request.getSession(false);

		if (session != null) {

			session.invalidate(); // killing the session if exist
		}

		session = request.getSession(true); // create a new session for a new
											// client
		System.out.println(session.getId() + " * " + session.getMaxInactiveInterval());

		Gson gson = new Gson();
		User userFromJson = gson.fromJson(jsonUserString, User.class);
		

		CouponSystem system = CouponSystem.getCouponSystem();

		String userName = userFromJson.getUserName();
		String pass = userFromJson.getPassword();
		String ClientType = userFromJson.getClientType();
		clientType type = clientType.valueOf(ClientType); // convert String to ENUM

		try {

			CouponClientFacade facade = system.login(userName, pass, type);
			System.out.println("loginServlet: request = " + request); // for  debug
			System.out.println("loginServlet: response = " + response); // for debug

			if (facade != null) {
				
				// updating the session with the login facade
				//session.setAttribute("facade", facade);

				Cookie cookie = new Cookie("Set-Cookie", "JSESSIONID=" + request.getSession().getId()
						+ ";path=/CouponProject/; HttpOnly; domain=/localhost; secure=false;");
				cookie.setComment(ClientType);
				String ServerResponse = new Gson().toJson(cookie);

				switch (type) {
				case Admin:
					AdminFacade adminFacade = new AdminFacade();
					if(adminFacade.login(userName, pass, type)) { 
						AdminFacade adminFacade2 = (AdminFacade) CouponSystem.getCouponSystem().login(userName,
								pass, type);
						session.setAttribute("adminFacade", adminFacade2);
						return Response.status(Response.Status.OK).entity(ServerResponse).build();
					} 
					break ; 

				case Company:
					// updating the session with the logged in company
					CompanyFacade companyFacade = new CompanyFacade();
					if (companyFacade.login(userName, pass, type)) {
						CompanyFacade companyFacade1 = (CompanyFacade) CouponSystem.getCouponSystem().login(userName,
								pass, type);
						session.setAttribute("companyFacade", companyFacade1);
						return Response.status(Response.Status.OK).entity(ServerResponse ).build();
					}

					break;

				case Customer:
					CustomerFacade customerFacade = new CustomerFacade();
					if (customerFacade.login(userName, pass, type)) {
						CustomerFacade customerFacade1 = (CustomerFacade) CouponSystem.getCouponSystem().login(userName,
								pass, type);
						session.setAttribute("customerFacade", customerFacade1);
						return Response.status(Response.Status.OK).entity(ServerResponse).build();
					}

					break;

				default:
					break;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		String responseToJson = "Failed to logIn";
		String failedServerResponse = new Gson().toJson(responseToJson);
		return Response.status(Response.Status.UNAUTHORIZED).entity(failedServerResponse).build();

	}

}
