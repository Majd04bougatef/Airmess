package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import services.UsersService;
import test.Session;
import models.Users;

public class Modifiermotpass {

    @FXML
    private TextField actuel; // Current password field

    @FXML
    private TextField newpass; // New password field

    @FXML
    private TextField verifier; // Password confirmation field

    private UsersService userService = new UsersService();

    @FXML
    void Changerpasse(ActionEvent event) {
        // Get current user from session
        Session session = Session.getInstance();
        Users currentUser = session.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            showAlert(AlertType.ERROR, "Erreur", "Aucun utilisateur trouvé dans la session.");
            return;
        }

        // Get user input
        String currentPasswordInput = actuel.getText();
        String newPasswordInput = newpass.getText();
        String confirmPasswordInput = verifier.getText();

        // Validate input
        if (currentPasswordInput.isEmpty() || newPasswordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            showAlert(AlertType.WARNING, "Erreur", "Tous les champs sont requis.");
            return;
        }

        if (!newPasswordInput.equals(confirmPasswordInput)) {
            showAlert(AlertType.WARNING, "Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        // Verify current password
        if (!userService.verifyPassword(currentUser, currentPasswordInput)) {
            showAlert(AlertType.ERROR, "Erreur", "Le mot de passe actuel est incorrect.");
            return;
        }

        // Update the password
        currentUser.setPassword(newPasswordInput); // Set new password
        userService.updatepassword(currentUser); // Update in the database

        showAlert(AlertType.INFORMATION, "Succès", "Mot de passe mis à jour avec succès !");
        clearFields();
    }

   @FXML
    void back(ActionEvent event) {
        // Logic to navigate back to the previous page
    }

    @FXML
    void oubliemotpasseinacount(ActionEvent event) {
        // Logic for "forgot password" functionality
    }

    private void clearFields() {
        actuel.clear();
        newpass.clear();
        verifier.clear();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}