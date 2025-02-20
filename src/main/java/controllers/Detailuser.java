package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import test.Session;
import models.Users;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import util.MyDatabase;

import java.util.Optional;


public class Detailuser implements Initializable {


    @FXML
    private Text datenaiss, email, name, phonenumber, prenom, roleuser;

    @FXML
    private ImageView imguser;

    private AnchorPane centralAnocherPane;

    public void setCentralAnocherPane(AnchorPane centralAnocherPane) {
        this.centralAnocherPane = centralAnocherPane;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load user data into UI components
        Session session = Session.getInstance();
        Users currentUser = session.getCurrentUser();

        if (currentUser != null) {
            name.setText(currentUser.getName());
            prenom.setText(currentUser.getPrenom());
            email.setText(currentUser.getEmail());
            roleuser.setText(currentUser.getRoleUser());
            phonenumber.setText(currentUser.getPhoneNumber());

            if (currentUser.getDateNaiss() != null) {
                datenaiss.setText(currentUser.getDateNaiss().toString());
            }

            if (currentUser.getImagesU() != null && !currentUser.getImagesU().isEmpty()) {
                try {
                    String imagePath = currentUser.getImagesU();
                    System.out.println("User Image Path: " + imagePath);

                    Image userImage;
                    if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                        // Load from a URL
                        userImage = new Image(imagePath, true);
                    } else {
                        // Load from a local file
                        File file = new File(imagePath);
                        if (file.exists()) {
                            userImage = new Image(file.toURI().toString());
                        } else {
                            System.err.println("Image file not found: " + imagePath);
                            return; // Exit if image not found
                        }
                    }

                    imguser.setImage(userImage);
                } catch (Exception e) {
                    System.err.println("Error loading user image: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("No image path provided for user.");
            }
        } else {
            System.err.println("No user found in session");
        }
    }


    @FXML
    void modifierpr(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Updateuser.fxml"));
            Parent updateUserPage = loader.load();

            // Ensure centralAnocherPane is not null
            if (centralAnocherPane != null) {
                centralAnocherPane.getChildren().clear(); // Clear previous content
                centralAnocherPane.getChildren().add(updateUserPage);
                System.out.println("Updateuser.fxml loaded inside centralAnocherPane.");
            } else {
                System.err.println("Error: centralAnocherPane is null.");
            }
        } catch (IOException e) {
            System.err.println("Error loading Updateuser.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void supprimerpr(ActionEvent event) {
        // Create confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Account Deactivation");
        alert.setHeaderText("Are you sure you want to delete your account?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int userId = Session.getInstance().getId_U();
            if (userId == 0) {
                showError("Error", "No user is currently logged in.");
                return;
            }

            String query = "DELETE FROM users WHERE id_U = ?";
            try (Connection con = MyDatabase.getInstance().getCon(); // Get connection from pool
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setInt(1, userId);
                int rowsDeleted = ps.executeUpdate();

                if (rowsDeleted > 0) {
                    showSuccess("Account deleted", "Your account has been successfully deleted.");
                    Session.getInstance().logout();
                    redirectToLogin(event);
                } else {
                    showError("Error", "User not found.");
                }
            } catch (SQLException e) {
                showError("Database Error", "Error deleting account: " + e.getMessage());
                e.printStackTrace(); // Print the stack trace for debugging
            }
        }
    }

    // Redirect to login page
    private void redirectToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Navigation Error", "Error loading login page: " + e.getMessage());
        }
    }

    // Helper method to show error alerts
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Helper method to show success alerts
    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void modifiermotpass(ActionEvent event) {


    }

}