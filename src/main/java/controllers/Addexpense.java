package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.Expense;
import services.ExpenseService;

public class Addexpense {

    @FXML
    private TextField amount;

    @FXML
    private ComboBox<String> categorie;

    @FXML
    private DatePicker date;

    @FXML
    private TextField descript;

    @FXML
    private TextField idu;

    @FXML
    private TextField name;


    public void initialize() {
        if (categorie != null) {
            categorie.getItems().addAll("transport", "restauration", "Hébergements","autre");
        }
    }
            ExpenseService expenseService = new ExpenseService();


    @FXML
    void addexpens(ActionEvent event) {

 try {
            // Create new Expense object
            Expense expense = new Expense();
            expense.setId_U(Integer.parseInt(idu.getText()));
            expense.setNameEX(name.getText());
            expense.setAmount(Double.parseDouble(amount.getText()));
            expense.setDescription(descript.getText());
            expense.setCategory(categorie.getValue());
            expense.setDateE(date.getValue());

            // Create ExpenseService and add the expense
            expenseService.add(expense);

            // Clear the fields after successful addition
       
            
        } catch (NumberFormatException e) {
            System.err.println("Please enter valid numbers for ID and amount");
        } catch (Exception e) {
            System.err.println("Error adding expense: " + e.getMessage());
        }

        clearFields();


    }



    private void clearFields() {
       idu.clear();
            name.clear();
            amount.clear();
            descript.clear();
            categorie.setValue(null);
            date.setValue(null);
    }



}
