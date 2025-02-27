package controllers.user;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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


import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import test.Session;
import models.EmailService;











import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Users;
import models.EmailService;
import services.UsersService;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Random;
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









    private String verificationCode;
    private Timeline codeExpirationTimer;
    private Stage verificationStage;
    private Users pendingUser;












    @FXML
    public void initialize() {
        type_user.getItems().addAll("Voyageurs", "Entreprise", "Créateur de contenu");
    }


    @FXML
    void Ajouterunephotodep(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        // Configuration du filtre d'extensions
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Fichiers image (*.png, *.jpg, *.jpeg)",
                "*.png",
                "*.jpg",
                "*.jpeg"
        );

        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.setTitle("Sélectionner une photo de profil");

        // Démarre dans le dossier Images par défaut
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Pictures"));

        File file = fileChooser.showOpenDialog(user_photo.getScene().getWindow());

        if (file == null) {
            showAlert("Aucun fichier sélectionné", "Veuillez sélectionner une image.");
            return;
        }

        // Configuration du dossier de destination
        String destinationDirectory = "C:/xampp/htdocs/imguser/";
        File destDir = new File(destinationDirectory);

        try {
            // Crée le dossier s'il n'existe pas
            if (!destDir.exists()) Files.createDirectories(destDir.toPath());

            // Copie le fichier
            File destinationFile = new File(destinationDirectory + file.getName());
            Files.copy(
                    file.toPath(),
                    destinationFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Met à jour l'interface
            imagePath = destinationFile.getAbsolutePath();
            Image image = new Image(destinationFile.toURI().toString());
            user_photo.setImage(image);

            System.out.println("Image enregistrée avec succès : " + imagePath);

        } catch (IOException e) {
            showAlert("Erreur de traitement", "Erreur lors de la copie du fichier : " + e.getMessage());
            e.printStackTrace();
        }
    }








    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendVerificationEmail(String email, String code) throws MessagingException {
        String subject = "Code de vérification";
        String messageBody = "Votre code de vérification est : " + code + "\n\n" +
                "Ce code expirera dans 2 minutes.\n\n" +
                "Si vous n'avez pas demandé ce code, veuillez ignorer cet email.";
        EmailService.sendConfirmationEmail(email, subject, messageBody);
    }

    private void showVerificationWindow(ActionEvent originalEvent) {
        try {
            verificationStage = new Stage();
            verificationStage.initModality(Modality.APPLICATION_MODAL);

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));
            layout.setAlignment(Pos.CENTER);

            Label timerLabel = new Label("Temps restant: 2:00");
            TextField codeField = new TextField();
            codeField.setPromptText("Entrez le code de vérification");

            Button verifyButton = new Button("Vérifier");
            Label messageLabel = new Label("");
            messageLabel.setTextFill(Color.RED);

            layout.getChildren().addAll(
                    timerLabel,
                    new Label("Un code a été envoyé à votre email."),
                    codeField,
                    verifyButton,
                    messageLabel
            );

            startVerificationTimer(timerLabel, messageLabel, verificationStage);

            verifyButton.setOnAction(e -> {
                if (codeField.getText().equals(verificationCode)) {
                    try {
                        if (codeExpirationTimer != null) {
                            codeExpirationTimer.stop();
                        }

                        // Add user using existing service method (no database changes)
                        userService.add(pendingUser);

                        verificationStage.close();
                        showAlert("Succès", "Inscription réussie !");
                        navigateToLogin(originalEvent);
                    } catch (Exception ex) {
                        showAlert("Erreur", "Échec de l'inscription : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    messageLabel.setText("Code incorrect. Veuillez réessayer.");
                }
            });

            Scene scene = new Scene(layout, 300, 250);
            verificationStage.setScene(scene);
            verificationStage.setTitle("Vérification Email");
            verificationStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de vérification");
        }
    }



    private void startVerificationTimer(Label timerLabel, Label messageLabel, Stage stage) {
        final int[] timeLeft = {120}; // 2 minutes in seconds

        codeExpirationTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    timeLeft[0]--;
                    int minutes = timeLeft[0] / 60;
                    int seconds = timeLeft[0] % 60;
                    timerLabel.setText(String.format("Temps restant: %d:%02d", minutes, seconds));

                    if (timeLeft[0] <= 0) {
                        codeExpirationTimer.stop();
                        verificationCode = null;
                        messageLabel.setText("Le code a expiré. Veuillez recommencer l'inscription.");

                        Timeline closeWindow = new Timeline(new KeyFrame(Duration.seconds(2), event -> stage.close()));
                        closeWindow.play();
                    }
                })
        );

        codeExpirationTimer.setCycleCount(Timeline.INDEFINITE);
        codeExpirationTimer.play();
    }




    @FXML
    void enregistrer(ActionEvent event) {
        if (!validateForm()) return;

        pendingUser = createUserFromForm();

        try {
            if (userService.emailExists(pendingUser.getEmail())) {
                showAlert("Erreur", "Cet email est déjà utilisé !");
                return;
            }

            // Generate and send verification code
            verificationCode = generateVerificationCode();
            sendVerificationEmail(pendingUser.getEmail(), verificationCode);

            // Show verification window
            showVerificationWindow(event);

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
        Parent root = FXMLLoader.load(getClass().getResource("/user/login.fxml"));
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

























