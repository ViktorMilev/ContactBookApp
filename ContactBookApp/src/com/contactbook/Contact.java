package com.contactbook;

public class Contact {
	private String name;
	private String phoneNumber;
	private String email;
	
	public Contact(String name, String phoneNumber, String email) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	// Getters
	public String getName() {
		return name;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	
	public String getEmail() {
		return email;
	}
	
	// Setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return "Name" + name +
				"\nPhone:" + phoneNumber +
				"\nEmail:" + email;
	}
}