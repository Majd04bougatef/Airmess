package controllers.expense;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import models.Expense;
import util.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Updateexpense {

    @FXML
    private Label Amountupdate;

    @FXML
    private Label Categoryupdate;

    @FXML
    private Label Descriptionupdate;

    @FXML
    private Label Expensenameupdate;

    @FXML
    private TextField nameField;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> categoryField;

    private Expense expenseToEdit;

    public void initialize() {
        // Initialize category options
        categoryField.getItems().addAll("transport", "restauration", "Hébergements", "autre");

        setupValidationListeners();
    }

    private void setupValidationListeners() {
        // Name validation
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                Expensenameupdate.setText("Le nom est requis");
                Expensenameupdate.setStyle("-fx-text-fill: red;");
            } else if (newValue.length() < 3) {
                Expensenameupdate.setText("Minimum 3 caractères");
                Expensenameupdate.setStyle("-fx-text-fill: red;");
            } else if (newValue.length() > 50) {
                Expensenameupdate.setText("Maximum 50 caractères");
                Expensenameupdate.setStyle("-fx-text-fill: red;");
            } else {
                Expensenameupdate.setText("");
            }
        });

        // Amount validation
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                Amountupdate.setText("Montant requis");
                Amountupdate.setStyle("-fx-text-fill: red;");
            } else {
                try {
                    String cleanValue = newValue.replace(",", ".");
                    if (!cleanValue.matches("^\\d*\\.?\\d*$")) {
                        amountField.setText(oldValue);
                        Amountupdate.setText("Format invalide");
                        Amountupdate.setStyle("-fx-text-fill: red;");
                        return;
                    }

                    if (!cleanValue.isEmpty()) {
                        double value = Double.parseDouble(cleanValue);
                        if (value <= 0) {
                            Amountupdate.setText("Montant doit être > 0");
                            Amountupdate.setStyle("-fx-text-fill: red;");
                        } else if (value > 1000000) {
                            Amountupdate.setText("Montant trop élevé");
                            Amountupdate.setStyle("-fx-text-fill: red;");
                        } else {
                            Amountupdate.setText("");
                            if (cleanValue.contains(".")) {
                                String[] parts = cleanValue.split("\\.");
                                if (parts[1].length() > 2) {
                                    amountField.setText(String.format("%.2f", value));
                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    Amountupdate.setText("Montant invalide");
                    Amountupdate.setStyle("-fx-text-fill: red;");
                }
            }
        });

        // Description validation
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                Descriptionupdate.setText("Description requise");
                Descriptionupdate.setStyle("-fx-text-fill: red;");
            } else if (newValue.length() < 10) {
                Descriptionupdate.setText("Minimum 10 caractères");
                Descriptionupdate.setStyle("-fx-text-fill: red;");
            } else if (newValue.length() > 500) {
                Descriptionupdate.setText("Maximum 500 caractères");
                Descriptionupdate.setStyle("-fx-text-fill: red;");
            } else {
                Descriptionupdate.setText("");
            }
        });

        // Category validation
        categoryField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                Categoryupdate.setText("Catégorie requise");
                Categoryupdate.setStyle("-fx-text-fill: red;");
            } else {
                Categoryupdate.setText("");
            }
        });
    }

    public void setExpenseToEdit(Expense expense) {
        this.expenseToEdit = expense;
        populateFields();
    }

    private void populateFields() {
        if (expenseToEdit != null) {
            nameField.setText(expenseToEdit.getNameEX());
            amountField.setText(String.format("%.2f", expenseToEdit.getAmount()));
            descriptionField.setText(expenseToEdit.getDescription());
            categoryField.setValue(expenseToEdit.getCategory());
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Name validation
        if (nameField.getText().trim().isEmpty()) {
            Expensenameupdate.setText("Le nom est requis");
            Expensenameupdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (nameField.getText().length() < 3 || nameField.getText().length() > 50) {
            Expensenameupdate.setText("Nom: 3-50 caractères");
            Expensenameupdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Amount validation
        String amountText = amountField.getText().trim().replace(",", ".");
        if (amountText.isEmpty()) {
            Amountupdate.setText("Montant requis");
            Amountupdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    Amountupdate.setText("Montant doit être > 0");
                    Amountupdate.setStyle("-fx-text-fill: red;");
                    isValid = false;
                } else if (amount > 1000000) {
                    Amountupdate.setText("Montant trop élevé");
                    Amountupdate.setStyle("-fx-text-fill: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                Amountupdate.setText("Montant invalide");
                Amountupdate.setStyle("-fx-text-fill: red;");
                isValid = false;
            }
        }

        // Description validation
        if (descriptionField.getText().trim().isEmpty()) {
            Descriptionupdate.setText("Description requise");
            Descriptionupdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (descriptionField.getText().length() < 10 || descriptionField.getText().length() > 500) {
            Descriptionupdate.setText("Description: 10-500 caractères");
            Descriptionupdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Category validation
        if (categoryField.getValue() == null) {
            Categoryupdate.setText("Catégorie requise");
            Categoryupdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        return isValid;
    }

    @FXML
    void saveChanges(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        String query = "UPDATE expense SET nameEX = ?, amount = ?, description = ?, category = ? WHERE idE = ?";
        try (Connection conn = MyDatabase.getInstance().getCon();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, nameField.getText().trim());
            double amount = Double.parseDouble(amountField.getText().trim().replace(",", "."));
            pst.setDouble(2, amount);
            pst.setString(3, descriptionField.getText().trim());
            pst.setString(4, categoryField.getValue());
            pst.setInt(5, expenseToEdit.getIdE());

            int result = pst.executeUpdate();
            if (result > 0) {
                Expensenameupdate.setText("Mise à jour réussie");
                Expensenameupdate.setStyle("-fx-text-fill: green;");

                // Update the expense object
                expenseToEdit.setNameEX(nameField.getText().trim());
                expenseToEdit.setAmount(amount);
                expenseToEdit.setDescription(descriptionField.getText().trim());
                expenseToEdit.setCategory(categoryField.getValue());
            } else {
                Expensenameupdate.setText("Aucune modification effectuée");
                Expensenameupdate.setStyle("-fx-text-fill: orange;");
            }
        } catch (SQLException e) {
            Expensenameupdate.setText("Erreur: " + e.getMessage());
            Expensenameupdate.setStyle("-fx-text-fill: red;");
            System.err.println("Error updating expense: " + e.getMessage());
        }
    }

    private void clearErrorMessages() {
        Expensenameupdate.setText("");
        Amountupdate.setText("");
        Descriptionupdate.setText("");
        Categoryupdate.setText("");
    }
}