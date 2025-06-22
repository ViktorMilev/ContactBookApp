package com.contactbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;


public class ContactManager {
	private List<Contact> contacts;
	private static final String FILE_PATH = "data/contacts.txt";
	
	public ContactManager() {
		contacts = new ArrayList<>();
		loadContactsFromFile();
	}
	
	public void addContact(Contact contact) {
		contacts.add(contact);
		saveContactsToFile();
	}
	
	public boolean removeContact(String name) {
		boolean removed = contacts.removeIf(c -> c.getName().equalsIgnoreCase(name));
		if (removed) {
			saveContactsToFile();
		}
		return removed;
	}
	
	public Contact searchContact(String name) {
		for (Contact contact : contacts) {
			if (contact.getName().equalsIgnoreCase(name)) {
				return contact;
			}
		}
		
		return null;
	}
	
	public List<Contact> getAllContacts() {
		return new ArrayList<>(contacts);
	}
	
	public List<Contact> filterContacts(String keyword) {
		String lowerKeyword = keyword.toLowerCase();
		return contacts.stream()
			.filter(c -> c.getName().toLowerCase().contains(lowerKeyword) ||
					     c.getPhoneNumber().toLowerCase().contains(lowerKeyword) ||
					     c.getEmail().toLowerCase().contains(lowerKeyword))
			.collect(Collectors.toList());
	}
	
	public List<Contact> getSortedContacts(String sortBy) {
		return contacts.stream()
			.sorted(getComparator(sortBy))
			.collect(Collectors.toList());
	}
	
	private Comparator<Contact> getComparator(String sortBy) {
		return switch (sortBy.toLowerCase()) {
		case "name" -> Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER);
		case "phone" -> Comparator.comparing(Contact::getPhoneNumber, String.CASE_INSENSITIVE_ORDER);
		case "email" -> Comparator.comparing(Contact::getEmail, String.CASE_INSENSITIVE_ORDER);
		default -> Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER);
		};	
	}

	private void saveContactsToFile() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
			writer.println("Name,Phone,Email,PhotoFileName");
			for (Contact c : contacts) {
				writer.println(escape(c.getName()) + "," +
							   escape(c.getPhoneNumber()) + "," +
							   escape(c.getEmail()) + "," +
							   escape(c.getPhotoFileName()));
			}
		} catch (IOException e) {
	        System.err.println("Error saving contacts: " + e.getMessage());
	    }
	}
	
	private String escape(String value) {
		if (value == null) {
			return "";
		}
		
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
			value = value.replace("\"", "\"\"");
			return "\"" + value + "\"";
		}
		
		return value;
	}
	
	private void loadContactsFromFile() {
		File file = new File(FILE_PATH);
		if (!file.exists()) return;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
			String line = reader.readLine();
			
			while((line = reader.readLine()) != null) {
				String[] parts = parseCSVLine(line);
				if (parts.length >= 3) {
					String name = parts[0];
					String phone = parts[1];
					String email = parts[2];
					String photoFile = parts.length >= 4 ? parts[3] : null;	
					contacts.add(new Contact(name, phone, email, photoFile));
				}
			}
		} catch (IOException e) {
			System.err.println("Error loading contacts: " + e.getMessage());
		}
	}
	
	private String[] parseCSVLine(String line) {
		List<String> values = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		boolean insideQuote = false;
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			
			if (c == '"') {
				if (insideQuote && i + 1 < line.length() && line.charAt(i + 1) == '"') {
					sb.append('"');
					i++;			// skip escaped quote
				} else {
					insideQuote = !insideQuote;
				}
			} else if (c == ',' && !insideQuote) {
				values.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		
		values.add(sb.toString());
		return values.toArray(new String[0]);
	}
}