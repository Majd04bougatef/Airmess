package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import services.UsersService;

import java.io.IOException;

public class Updatepasswordfromforget {

    @FXML
    private PasswordField newpass;

    @FXML
    private PasswordField verifier;

    private String userEmail; // Email passed from previous stage

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    void Changerpasse(ActionEvent event) {
        String newPassword = newpass.getText();
        String confirmPassword = verifier.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            UsersService usersService = new UsersService();
            boolean updated = usersService.updatePassword(userEmail, newPassword);

            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe mis à jour avec succès !");
                navigateToLogin(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du mot de passe.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/login.fxml"));
            Parent loginPage = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loginPage);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'écran de connexion.");
        }
    }
}