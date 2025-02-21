package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Users;
import services.UsersService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.time.LocalDate;

import java.util.regex.Pattern;

public class Signup {

    // Champs FXML
    @FXML
    private TextField Email_user;
    @FXML
    private DatePicker dateN_user;
    @FXML
    private TextField nom_user;
    @FXML
    private TextField password_user;
    @FXML
    private TextField prenom_user;
    @FXML
    private TextField tel_user;
    @FXML
    private ComboBox<String> type_user;
    @FXML
    private ImageView user_photo;

    // Labels de validation
    @FXML
    private Label validationemail;
    @FXML
    private Label validationmotdepass;
    @FXML
    private Label validationnaiss;
    @FXML
    private Label validationnom;
    @FXML
    private Label validationnumero;
    @FXML
    private Label validationprenom;
    @FXML
    private Label validationroleuser;

    private UsersService userService = new UsersService();
    private String imagePath;

    @FXML
    public void initialize() {
        type_user.getItems().addAll("Voyageurs", "Entreprise", "Créateur de contenu");
    }

    @FXML
    void Ajouterunephotodep(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".png", ".jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file == null) {
            System.out.println("No file selected!");
            return;
        }

        System.out.println("Selected file: " + file.getAbsolutePath());

        String destinationDirectory = "C:/xampp/htdocs/imguser/";
        File dir = new File(destinationDirectory);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String destinationPath = destinationDirectory + file.getName();
        File destinationFile = new File(destinationPath);

        try {
            Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (destinationFile.exists()) {
                // ✅ Set imagePath correctly
                imagePath = destinationFile.getAbsolutePath();

                Platform.runLater(() -> {
                    Image image = new Image(destinationFile.toURI().toString());
                    user_photo.setImage(image);
                });
                System.out.println("Image saved and displayed: " + imagePath);
            } else {
                System.out.println("Error: File was not copied successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error saving image: " + e.getMessage());
        }
    }


    @FXML
    void enregistrer(ActionEvent event) {
        if (!validateForm()) return;

        Users user = createUserFromForm();

        try {
            if (userService.emailExists(user.getEmail())) {
                showAlert("Erreur", "Cet email est déjà utilisé !");
                return;
            }

            userService.add(user);
            showAlert("Succès", "Inscription réussie !");
            navigateToLogin(event);
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'inscription : " + e.getMessage());
        }
    }

    private Users createUserFromForm() {
        Users user = new Users();
        user.setName(nom_user.getText());
        user.setPrenom(prenom_user.getText());
        user.setEmail(Email_user.getText());
        user.setPassword(hashPassword(password_user.getText()));
        user.setRoleUser(type_user.getValue());
        user.setDateNaiss(dateN_user.getValue());
        user.setPhoneNumber(tel_user.getText());
        user.setImagesU(imagePath);
        return user;
    }

    private boolean validateForm() {
        boolean isValid = true;
        resetValidationLabels();

        // Validation des champs
        isValid &= validateField(nom_user, validationnom, "Nom requis");
        isValid &= validateField(prenom_user, validationprenom, "Prénom requis");
        isValid &= validateEmail();
        isValid &= validatePhone();
        isValid &= validatePassword();
        isValid &= validateBirthDate();
        isValid &= validateUserType();
        isValid &= validateImage();

        return isValid;
    }

    // Méthodes de validation
    private boolean validateField(TextField field, Label errorLabel, String message) {
        if (field.getText().trim().isEmpty()) {
            errorLabel.setText(message);
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        if (!isValidEmail(Email_user.getText())) {
            validationemail.setText("Email invalide");
            return false;
        }
        return true;
    }

    private boolean validatePhone() {
        if (!isValidPhoneNumber(tel_user.getText())) {
            validationnumero.setText("Numéro invalide (8 chiffres)");
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (!isValidPassword(password_user.getText())) {
            validationmotdepass.setText("8 caractères minimum");
            return false;
        }
        return true;
    }

    private boolean validateBirthDate() {
        if (dateN_user.getValue() == null || !isOldEnough(dateN_user.getValue())) {
            validationnaiss.setText("Âge minimum : 15 ans");
            return false;
        }
        return true;
    }

    private boolean validateUserType() {
        if (type_user.getValue() == null) {
            validationroleuser.setText("Sélectionnez un type");
            return false;
        }
        return true;
    }

    private boolean validateImage() {
        if (imagePath == null || imagePath.isEmpty()) {
            validationroleuser.setText("Veuillez sélectionner une photo.");
            return false;
        }
        return true;
    }


    // Utilitaires
    private void resetValidationLabels() {
        validationnom.setText("");
        validationprenom.setText("");
        validationemail.setText("");
        validationnumero.setText("");
        validationmotdepass.setText("");
        validationnaiss.setText("");
        validationroleuser.setText("");
    }

    private String hashPassword(String password) {
        // Implémentez le hachage réel ici (BCrypt, etc.)
        return password; // À remplacer par un vrai hachage
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void back(ActionEvent event) throws IOException {
        navigateToLogin(event);
    }

    // Méthodes de validation de base
    private boolean isValidEmail(String email) {
        return Pattern.matches("^[\\w.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", email);
    }

    private boolean isValidPhoneNumber(String phone) {
        return Pattern.matches("^\\d{8}$", phone);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    private boolean isOldEnough(LocalDate birthDate) {
        return birthDate.plusYears(15).isBefore(LocalDate.now());
    }
}
