package com.contactbook;

public class Contact {
	private String name;
	private String phoneNumber;
	private String email;
	private String photoFileName;
	
	public Contact(String name, String phoneNumber, String email, String photoFileName) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.photoFileName = photoFileName;
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
	
	public String getPhotoFileName() {
		return photoFileName;
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
	
	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}
	
	@Override
	public String toString() {
		return "Name: " + name +
				"\nPhone: " + phoneNumber +
				"\nEmail: " + email +
				"\nPhoto: " + photoFileName;
	}
}