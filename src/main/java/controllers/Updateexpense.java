package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import models.Expense;
import util.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Updateexpense {

    @FXML
    private TextField nameField;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField categoryField;

    private Expense expenseToEdit;

    public void setExpenseToEdit(Expense expense) {
        this.expenseToEdit = expense;
        populateFields();
    }

    private void populateFields() {
        if (expenseToEdit != null) {
            nameField.setText(expenseToEdit.getNameEX());
            amountField.setText(String.valueOf(expenseToEdit.getAmount()));
            descriptionField.setText(expenseToEdit.getDescription());
            categoryField.setText(expenseToEdit.getCategory());
        }
    }

    @FXML
    void saveChanges(ActionEvent event) {
        String query = "UPDATE expense SET nameEX = ?, amount = ?, description = ?, category = ? WHERE idE = ?";
        try (Connection conn = MyDatabase.getInstance().getCon();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, nameField.getText());
            pst.setDouble(2, Double.parseDouble(amountField.getText()));
            pst.setString(3, descriptionField.getText());
            pst.setString(4, categoryField.getText());
            pst.setInt(5, expenseToEdit.getIdE());

            pst.executeUpdate();
            System.out.println("Expense updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
        }
    }
}