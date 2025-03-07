package controllers.user;

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
    @FXML
    private TextField tax_id; // New field for tax ID

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
        type_user.setOnAction(event -> handleUserTypeChange());
    }

    private void handleUserTypeChange() {
        if ("Entreprise".equals(type_user.getValue())) {
            dateN_user.setPromptText("Foundation Date");
            tax_id.setVisible(true);
        } else {
            dateN_user.setPromptText("Date de Naissance");
            tax_id.setVisible(false);
        }
    }

    @FXML
    void Ajouterunephotodep(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Fichiers image (*.png, *.jpg, *.jpeg)",
                "*.png", "*.jpg", "*.jpeg"
        );
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.setTitle("Sélectionner une photo de profil");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Pictures"));

        File file = fileChooser.showOpenDialog(user_photo.getScene().getWindow());
        if (file == null) {
            showAlert("Aucun fichier sélectionné", "Veuillez sélectionner une image.");
            return;
        }

        String destinationDirectory = "C:/xampp/htdocs/imguser/";
        File destDir = new File(destinationDirectory);
        try {
            if (!destDir.exists()) Files.createDirectories(destDir.toPath());
            File destinationFile = new File(destinationDirectory + file.getName());
            Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            imagePath = destinationFile.getAbsolutePath();
            user_photo.setImage(new Image(destinationFile.toURI().toString()));
        } catch (IOException e) {
            showAlert("Erreur de traitement", "Erreur lors de la copie du fichier : " + e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
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

        isValid &= validateField(nom_user, validationnom, "Nom requis");
        isValid &= validateField(prenom_user, validationprenom, "Prénom requis");
        isValid &= validateEmail();
        isValid &= validatePhone();
        isValid &= validatePassword();
        isValid &= validateBirthDate();
        isValid &= validateUserType();
        isValid &= validateImage();

        if ("Entreprise".equals(type_user.getValue())) {
            isValid &= validateField(tax_id, validationroleuser, "Tax ID requis");
        }

        return isValid;
    }

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
        return password; // Replace with actual hashing
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/user/login.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void back(ActionEvent event) throws IOException {
        navigateToLogin(event);
    }

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