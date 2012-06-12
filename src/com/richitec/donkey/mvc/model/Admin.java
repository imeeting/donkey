package com.richitec.donkey.mvc.model;

public class Admin {
	
	public static String SYSTEM_ADMIN = "system_admin";
	
	private String name;
	
	private String password;

	public Admin(){
		//
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public Boolean isValid(String name, String password){
		return this.name.equals(name) && this.password.equals(password);
	}
	
	
}
