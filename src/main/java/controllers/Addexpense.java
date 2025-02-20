package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import models.Expense;
import services.ExpenseService;
import test.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Addexpense {

    @FXML
    private ImageView fotodepence;
    @FXML
    private TextField amount;

    @FXML
    private ComboBox<String> categorie;

    @FXML
    private DatePicker date;

    @FXML
    private TextArea descript;
    @FXML
    private TextField name;

    @FXML
    void depaff(ActionEvent event) {

    }


    private Session session = Session.getInstance();
    private ExpenseService expenseService = new ExpenseService();

    public void initialize() {
        if (categorie != null) {
            categorie.getItems().addAll("transport", "restauration", "Hébergements", "autre");
        }

        // Check if user is logged in when initializing
        try {
            session.checkLogin();
        } catch (SecurityException e) {
            showAlert("Error", "Authentication Required", "You must be logged in to add expenses.");
            // You might want to disable the form or redirect to login
        }
    }

    @FXML
    void addexpens(ActionEvent event) {
        try {
            // Check if user is logged in before adding expense
            session.checkLogin();

            // Validate input fields
            if (validateInputs()) {
                // Create new Expense object
                Expense expense = new Expense();
                expense.setNameEX(name.getText());
                expense.setAmount(Double.parseDouble(amount.getText()));
                expense.setDescription(descript.getText());
                expense.setCategory(categorie.getValue());
                expense.setDateE(date.getValue());
                expense.setImagedepense(fotodepence.getImage().getUrl());

                // Set the user ID from the session
                expense.setId_U(session.getId_U());

                // Add the expense
                expenseService.add(expense);

                // Show success message
                showAlert("Success", "Expense Added", "The expense has been successfully added.");

                // Clear the fields after successful addition
                clearFields();
            }
        } catch (SecurityException e) {
            showAlert("Error", "Authentication Required", "You must be logged in to add expenses.");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Input", "Please enter a valid number for amount.");
        } catch (Exception e) {
            showAlert("Error", "Error Adding Expense", "An error occurred: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (name.getText().isEmpty() || amount.getText().isEmpty() ||
                descript.getText().isEmpty() || categorie.getValue() == null ||
                date.getValue() == null) {
            showAlert("Error", "Missing Information", "Please fill in all fields.");
            return false;
        }
        try {
            Double.parseDouble(amount.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Amount", "Please enter a valid number for amount.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        name.clear();
        amount.clear();
        descript.clear();
        categorie.setValue(null);
        date.setValue(null);
        fotodepence.setImage(null);
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void Ajouterunephotodep(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            String destinationPath = "C:/xampp/htdocs/imguser/" + file.getName();
            File destinationFile = new File(destinationPath);

            try {
                Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Image image = new Image(destinationFile.toURI().toString());
                fotodepence.setImage(image);
                System.out.println("Image path in project: " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error saving image to project: " + e.getMessage());
            }
        }
    }

}