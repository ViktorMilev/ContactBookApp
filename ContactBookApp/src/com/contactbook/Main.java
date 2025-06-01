package com.contactbook;


import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
	private final ContactManager manager = new ContactManager();
	private final ObservableList<Contact> displayedContacts = FXCollections.observableArrayList();
	private ListView<Contact> contactListView;
	
	private double xOffset = 0;
	private double yOffset = 0;
	
	public static void main (String[] args) {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.initStyle(StageStyle.UNDECORATED);
		
		// Top bar
		HBox topBar = new HBox();
		topBar.setPrefWidth(Double.MAX_VALUE);
		topBar.getStyleClass().add("top-bar");
		
		Image iconImage = new Image(getClass().getResourceAsStream("/com/contactbook/icons/app_icon.png"));
		ImageView iconView = new ImageView(iconImage);
		iconView.setFitHeight(23);
		iconView.setFitWidth(23);
		
		Label title = new Label("Contact Book");
		title.getStyleClass().add("title-label");
		
		Button buttonMinimize = new Button("_");
		buttonMinimize.getStyleClass().add("window-button");
		buttonMinimize.setOnAction(e -> primaryStage.setIconified(true));
		
		Button buttonClose = new Button("X");
		buttonClose.getStyleClass().add("window-button");
		buttonClose.setOnAction(e -> primaryStage.close());
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		topBar.getChildren().addAll(iconView, title, spacer, buttonMinimize, buttonClose);
		
		topBar.setOnMousePressed(e -> {
			xOffset = primaryStage.getX() - e.getScreenX();
			yOffset = primaryStage.getY() - e.getScreenY();
		});
		
		topBar.setOnMouseDragged(e -> {
			primaryStage.setX(e.getScreenX() + xOffset);
			primaryStage.setY(e.getScreenY() + yOffset);
		});
		
		
		// Contact list
		contactListView = new ListView<>(displayedContacts);
		contactListView.setPlaceholder(new Label("Welcome to Contact Book app!"));
		((Label) contactListView.getPlaceholder()).getStyleClass().add("contact-list-view-placeholder"); 
		VBox contactListWrapper = new VBox(contactListView);
		contactListWrapper.setPadding(new Insets(10));
		
		// Buttons
		Button addButton = new Button("Add Contact");
		Button editButton = new Button("Edit Contact");
		Button searchButton = new Button("Search");
		Button removeButton = new Button("Remove");
		Button showAllButton = new Button("Show All");
		Button filterButton = new Button("Filter");
		Button sortButton = new Button("Sort");
		
		// Add button styling
		addButton.getStyleClass().add("button");
		editButton.getStyleClass().add("button");
		searchButton.getStyleClass().add("button");
		removeButton.getStyleClass().add("button");
		showAllButton.getStyleClass().add("button");
		filterButton.getStyleClass().add("button");
		sortButton.getStyleClass().add("button");
		
		HBox buttonRow = new HBox(10, addButton, editButton, searchButton, removeButton, showAllButton, filterButton, sortButton);
		buttonRow.setPadding(new Insets(10));
		
		VBox layout = new VBox(topBar, buttonRow, contactListWrapper);
		layout.setPrefWidth(600);
		layout.setSpacing(10);
	 
		
		// Button actions
		addButton.setOnAction(e -> showAddDialog());
		//editButton.setOnAction();
		searchButton.setOnAction(e -> showSearchDialog());
		removeButton.setOnAction(e -> showRemoveDialog());
		showAllButton.setOnAction(e -> showAllContacts());
		filterButton.setOnAction(e -> showFilterDialog());
		sortButton.setOnAction(e -> showSortDialog());
		
		// Create Scene and add stylesheet
		Scene scene = new Scene(layout, 600, 400);
		scene.getStylesheets().add(getClass().getResource("/com/contactbook/styles/theme.css").toExternalForm());
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void showAddDialog() {
		Dialog<Contact> dialog = new Dialog<>();
		dialog.setTitle("Add Contact");
		
		TextField nameField = new TextField();
		nameField.setPromptText("Name");
		
		TextField phoneField = new TextField();
		phoneField.setPromptText("Phone");
		
		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		
		VBox content = new VBox(10, nameField, phoneField, emailField);
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		dialog.setResultConverter(btn -> {
			if (btn == ButtonType.OK) {
				return new Contact(nameField.getText(), phoneField.getText(), emailField.getText());
			}
			
			return null;
		});
		
		dialog.showAndWait().ifPresent(contact -> {
			manager.addContact(contact);
			showAllContacts();
		});
	}
	
	private void showSearchDialog() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Search Contact");
		dialog.setHeaderText("Enter name to search:");
		dialog.showAndWait().ifPresent(name -> {
			Contact found = manager.searchContact(name);
			displayedContacts.setAll(found != null ? List.of(found) : List.of());
		});
	}
	
	private void showRemoveDialog() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Remove Contact");
		dialog.setHeaderText("Enter name to remove:");
		dialog.showAndWait().ifPresent(name -> {
			boolean removed = manager.removeContact(name);
			showAllContacts();
			if (!removed) {
				showAlert("Contact not found.");
			}
		});
	}
	
	private void showFilterDialog() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Filter Contacts");
		dialog.setHeaderText("Enter keyword to filter:");
		dialog.showAndWait().ifPresent(keyword -> {
			displayedContacts.setAll(manager.filterContacts(keyword));
		});
	}
	
	private void showSortDialog() {
		ChoiceDialog<String> dialog = new ChoiceDialog<>("name", "name", "phone", "email");
		dialog.setTitle("Sort Contacts");
		dialog.setHeaderText("Sort by:");
		dialog.showAndWait().ifPresent(criterion -> {
			displayedContacts.setAll(manager.getSortedContacts(criterion));
		});
	}
	
	private void showAllContacts() {
		displayedContacts.setAll(manager.getAllContacts());
	}
	
	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}