package com.evicohen.JavaBeans;

import java.util.Date;

public class Income {
	
	

	private long id; 
	private String name; 
	private String date; 
	private IncomeType description; 
	@Override
	public String toString() {
		return "Income [id=" + id + ", name=" + name + ", date=" + date + ", description=" + description + ", amount="
				+ amount + "]";
	}
	private double amount;
	
	
	public Income(long id, String name, String date, IncomeType description, double amount) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.description = description;
		this.amount = amount;
	}
	
	public Income() 
	{ 
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public IncomeType getDescription() {
		return description;
	}
	public void setDescription(IncomeType description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	} 
	 
	

}
