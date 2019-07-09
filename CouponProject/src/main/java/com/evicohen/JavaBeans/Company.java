package com.evicohen.JavaBeans;
import java.util.ArrayList;

public class Company {

	
	
	// Attributes 
	
	private long id;
	private String compName;
	private String password;
	private String email;
    ArrayList<Coupon> coupons = new ArrayList<Coupon>();

    
    // Ctors

    public Company() {
    }
    
    public Company(long id, String compName, String password, String email) {
    	this.compName = compName;
    	this.password = password;
    	this.email = email;
    	this.id = id; 
    }
    
    
    
    // Methods
    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCompName() {
		return compName;
	}
	public void setCompName(String compName) {
		this.compName = compName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Company [id=" + id + ", compName=" + compName + ", password=" + password + ", email=" + email
				+ ", coupons=" + coupons + "]";
	}
}