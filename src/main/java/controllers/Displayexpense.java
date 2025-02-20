package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import test.Session;


import javafx.scene.image.ImageView;


import javafx.fxml.Initializable;
import javafx.scene.image.Image;


import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


import java.io.IOException;


import util.MyDatabase;


import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import models.Expense;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;


public class Displayexpense implements Initializable {


    private AnchorPane centralAnocherPane;

    @FXML
    private GridPane expenseGrid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadExpenses();
    }

    private void loadExpenses() {
        List<Expense> expenses = fetchExpensesFromDatabase();
        displayExpenseCards(expenses);
    }

    private List<Expense> fetchExpensesFromDatabase() {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM expense WHERE id_U = ?";

        try {
            Session.getInstance().checkLogin(); // Check if user is logged in
            Connection conn = MyDatabase.getInstance().getCon();

            if (conn == null || conn.isClosed()) {
                // Get a new connection if closed
                MyDatabase.getInstance().reconnect(); // Assuming there's a reconnect method
                conn = MyDatabase.getInstance().getCon();
            }

            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, Session.getInstance().getCurrentUser().getId_U());

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        Expense expense = new Expense(
                                rs.getInt("idE"),
                                rs.getInt("id_U"),
                                rs.getString("nameEX"),
                                rs.getDouble("amount"),
                                rs.getString("description"),
                                rs.getString("category"),
                                rs.getDate("dateE").toLocalDate(),
                                rs.getString("Imagedepense")
                        );
                        expenses.add(expense);
                    }
                }
            }
        } catch (SecurityException e) {
            System.err.println("User not logged in: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }


    private void displayExpenseCards(List<Expense> expenses) {
        expenseGrid.getChildren().clear();
        int column = 0;
        int row = 0;

        for (Expense expense : expenses) {
            VBox card = createExpenseCard(expense);

            expenseGrid.add(card, column, row);
            column++;
            if (column == 3) { // 3 cards per row
                column = 0;
                row++;
            }
        }
    }


    private VBox createExpenseCard(Expense expense) {
        VBox card = new VBox(10);
        card.getStyleClass().add("expense-card");
        card.setPadding(new Insets(10));

        // Image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(expense.getImagedepense());
            imageView.setImage(image);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            // Load default image if expense image fails
            imageView.setImage(new Image(getClass().getResourceAsStream("/image/default-expense.png")));
        }

        // Name
        Label nameLabel = new Label(expense.getNameEX());
        nameLabel.getStyleClass().add("expense-name");

        // Amount
        Label amountLabel = new Label(String.format("%.2f TND", expense.getAmount()));
        amountLabel.getStyleClass().add("expense-amount");

        // Date
        Label dateLabel = new Label(expense.getDateE().toString());
        dateLabel.getStyleClass().add("expense-date");

        card.getChildren().addAll(imageView, nameLabel, amountLabel, dateLabel);
        return card;
    }


    public void setCentralAnocherPane(AnchorPane centralAnocherPane) {
        this.centralAnocherPane = centralAnocherPane;
    }

    @FXML
    void addeqpense(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Addexpense.fxml"));
            Parent addExpensePage = loader.load();

            if (centralAnocherPane != null) {
                centralAnocherPane.getChildren().clear(); // Clear previous content
                centralAnocherPane.getChildren().add(addExpensePage);
                System.out.println("Addexpense.fxml loaded inside centralAnchorPane.");
            } else {
                System.err.println("Error: centralAnchorPane is null.");
            }
        } catch (IOException e) {
            System.err.println("Error loading Addexpense.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }


}









