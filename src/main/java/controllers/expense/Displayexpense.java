package controllers.expense;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import models.Expense;
import util.MyDatabase;
import test.Session;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

        // Buttons (Delete and Edit)
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteExpense(expense));

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("update-button");
        editButton.setOnAction(e -> editExpense(expense));

        HBox buttonBox = new HBox(10, editButton, deleteButton);
        card.getChildren().addAll(imageView, nameLabel, amountLabel, dateLabel, buttonBox);

        return card;
    }

    private void deleteExpense(Expense expense) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this expense?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = "DELETE FROM expense WHERE idE = ?";
            try (Connection conn = MyDatabase.getInstance().getCon();
                 PreparedStatement pst = conn.prepareStatement(query)) {

                pst.setInt(1, expense.getIdE());
                pst.executeUpdate();
                loadExpenses(); // Refresh the grid after deletion
            } catch (SQLException e) {
                System.err.println("Error deleting expense: " + e.getMessage());
            }
        }
    }

    private void editExpense(Expense expense) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/expense/updateexpense.fxml"));
            Parent updateExpensePage = loader.load();

            Updateexpense controller = loader.getController();
            controller.setExpenseToEdit(expense);

            if (centralAnocherPane != null) {
                centralAnocherPane.getChildren().clear(); // Clear previous content
                centralAnocherPane.getChildren().add(updateExpensePage);
                System.out.println("Updateexpense.fxml loaded inside centralAnchorPane.");
            } else {
                System.err.println("Error: centralAnchorPane is null.");
            }
        } catch (IOException e) {
            System.err.println("Error loading Updateexpense.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCentralAnocherPane(AnchorPane centralAnocherPane) {
        this.centralAnocherPane = centralAnocherPane;
    }

    @FXML
    void addeqpense(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/expense/addexpense.fxml"));
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