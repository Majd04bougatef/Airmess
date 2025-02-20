package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import services.UsersService;
import models.Users;


import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


import test.Session;


public class Login {


    @FXML
    private ImageView facebook;

    @FXML
    private ImageView googl;
    @FXML
    private TextField email_identifier;

    @FXML
    private TextField mot_isentifier;

    @FXML
    private TextField passsee;


    @FXML
    private CheckBox show;

    private UsersService usersService = new UsersService();


    public void initialize() {
        // Sync the password fields when text changes
        mot_isentifier.textProperty().addListener((observable, oldValue, newValue) -> {
            passsee.setText(newValue);
        });

        passsee.textProperty().addListener((observable, oldValue, newValue) -> {
            mot_isentifier.setText(newValue);
        });
    }


    @FXML
    void showpass(ActionEvent event) {
        if (show.isSelected()) {
            // Show password as plain text
            passsee.setText(mot_isentifier.getText());
            passsee.setVisible(true);
            mot_isentifier.setVisible(false);
        } else {
            // Hide password with asterisks
            mot_isentifier.setText(passsee.getText());
            mot_isentifier.setVisible(true);
            passsee.setVisible(false);
        }
    }


    @FXML
    void identifier(ActionEvent event) {
        String email = email_identifier.getText();
        String password = mot_isentifier.isVisible() ?
                mot_isentifier.getText() : passsee.getText();
        System.out.println(email);
        System.out.println(password);


        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Authentication Failed",
                    "Email and password cannot be empty!");
            return;
        }

        // Attempt login
        Users loggedInUser = usersService.login(email, password);

        if (loggedInUser != null) {
            // Initialize session
            Session.getInstance().login(loggedInUser);

            try {
                // Load appropriate view based on user role
                String viewPath = "/menu_voyageurs.fxml";
                if ("ADMIN".equals(loggedInUser.getRoleUser())) {
                    viewPath = "/admin_dashboard.fxml";
                }

                Parent mainView = FXMLLoader.load(getClass().getResource(viewPath));
                Scene mainScene = new Scene(mainView);

                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.setScene(mainScene);
                currentStage.setTitle(loggedInUser.getRoleUser().equals("ADMIN") ?
                        "Admin Dashboard" : "Menu Principal");
                currentStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "Navigation Error",
                        "Could not load the main menu: " + e.getMessage());
            }

        } else {
            showAlert(AlertType.ERROR, "Error", "Authentication Failed",
                    "Invalid email or password. Please try again.");
        }
    }


    @FXML
    void oubliemotpasse(ActionEvent event) {
    }

    @FXML
    void signup(ActionEvent event) {


        try {
            // Load the Signup FXML
            Parent signupView = FXMLLoader.load(getClass().getResource("/Signup.fxml"));
            Scene signupScene = new Scene(signupView);

            // Get the current stage from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the current stage
            currentStage.setScene(signupScene);
            currentStage.setTitle("Sign Up");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Navigation Error",
                    "Could not load the signup page: " + e.getMessage());
        }


    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}