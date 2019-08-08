package com.evicohen.BusinessDelegate;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.evicohen.JavaBeans.Income;

public class BusinessDelegate { 
	
	
	 
	 private Client client = ClientBuilder.newClient(); 
	 
	 
	public void test() { 
		
		String  storeIncome_URl = "https://www.ynet.co.il/home/0,7340,L-8,00.html" ; 
        String string = client
				.target(storeIncome_URl)
				.request(MediaType.APPLICATION_XML)
				.get(String.class); 
        
        System.out.println(string);
		
	}
	 
	
	
	public synchronized Response storeIncome(Income income )  {
			
		String  storeIncome_URl = "http://localhost:8081/income/storeIncome" ; 
		                      
		return client
				.target(storeIncome_URl)
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(income, MediaType.APPLICATION_JSON)); 
		
		
	}
	
	public synchronized List<Income> viewIncomeByCompany(String companyName) { 
		
		String  viewIncomeByCompany_Url = "http://localhost:8081/income/ViewIncomesByCompany" ;
		
		 List<Income> Incomes =
		            client.target(viewIncomeByCompany_Url)
		            .path("all")
		            .request(MediaType.APPLICATION_XML)
		            .get(new GenericType<List<Income>>() {
		            });
		
		
		return Incomes; 
	}
	
	public synchronized List<Income> viewAllIncome() { 
		
		System.out.println("Im here");
		String  viewAllIncome_Url = "http://localhost:8081/income/ViewAllIncomes" ;
		
		 List<Income> Incomes =
		            client.target(viewAllIncome_Url)
		            .request(MediaType.APPLICATION_JSON_TYPE)
		            .get(new GenericType<List<Income>>() {});
		
		return Incomes; 
		
	}
	
	public synchronized List<Income> viewIncomeByCustomer(String customerName) { 
		
		String  viewIncomeByCustomer_Url = "http://localhost:8081/income/ViewIncomesByCustomer" ;
		
		 List<Income> Incomes =
		            client.target(viewIncomeByCustomer_Url)
		            .path("all")
		            .request(MediaType.APPLICATION_XML)
		            .get(new GenericType<List<Income>>() {
		            });
		
		
		return Incomes;
		
		
	}

}
