package com.evicohen.JavaBeans;

public enum IncomeType {
	
	CUSTOMER_PURCHASE("CUSTOMER_PURCHASE"),
	COMPANY_NEW_COUPON("COMPANY_NEW_COUPON"),
	COMPANY_UPDATE_COUPON("COMPANY_UPDATE_COUPON");  
	
	
	private String description; 
	
	private IncomeType(String description ) { 
		
		this.description = description; 
			
	}
	private String getDescription() { 
		
		return description ; 
	}
	
    @Override
    public String toString() {
        return this.getDescription();
    }
} 
