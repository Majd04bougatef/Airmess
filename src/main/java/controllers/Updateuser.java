package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.regex.Pattern;

import services.UsersService;
import test.Session;
import models.Users;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class Updateuser implements Initializable {

    @FXML
    private Label naissancecontroller;
    @FXML
    private Label nimerocontroller;
    @FXML
    private Label Nomcontrole;
    @FXML
    private Label Prénomcontroller;
    @FXML
    private Label imagecpntroller;

    @FXML
    private DatePicker modifier_dateN_user;
    @FXML
    private TextField modifier_nom_uesr;
    @FXML
    private TextField modifier_prenom_user;
    @FXML
    private TextField modifier_tel_user;
    @FXML
    private ComboBox<String> modifier_type_user;
    @FXML
    private ImageView modifierfoto;

    private UsersService ps = new UsersService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBox items
        if (modifier_type_user != null) {
            modifier_type_user.getItems().addAll("Voyageurs", "Entreprise", "Créateur de contenu");
        }

        // Get current user from session
        Session session = Session.getInstance();
        Users currentUser = session.getCurrentUser();

        if (currentUser != null) {
            // Set text fields
            modifier_nom_uesr.setText(currentUser.getName());
            modifier_prenom_user.setText(currentUser.getPrenom());
            modifier_tel_user.setText(currentUser.getPhoneNumber());

            // Set date picker
            if (currentUser.getDateNaiss() != null) {
                modifier_dateN_user.setValue(currentUser.getDateNaiss());
            }

            // Set combobox selection
            if (currentUser.getRoleUser() != null) {
                modifier_type_user.setValue(currentUser.getRoleUser());
            }

            // Set profile image
            if (currentUser.getImagesU() != null && !currentUser.getImagesU().isEmpty()) {
                try {
                    File imageFile = new File(currentUser.getImagesU());
                    if (imageFile.exists()) {
                        Image userImage = new Image("file:" + imageFile.getAbsolutePath());
                        modifierfoto.setImage(userImage);
                        modifierfoto.setFitWidth(260);
                        modifierfoto.setFitHeight(310);
                        modifierfoto.setPreserveRatio(true);
                    } else {
                        System.err.println("Le fichier image n'existe pas à l'emplacement spécifié.");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                }
            }
        } else {
            System.err.println("Aucun utilisateur trouvé dans la session");
        }
    }

    @FXML
    void modifierfoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Image", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String destinationPath = "C:/xampp/htdocs/imguser/" + file.getName();
            File destinationFile = new File(destinationPath);

            try {
                Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Image image = new Image(destinationFile.toURI().toString());
                modifierfoto.setImage(image);
                imagecpntroller.setText(""); // Clear image error on successful upload
                System.out.println("Image enregistrée à : " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                imagecpntroller.setText("Erreur lors de l'enregistrement de l'image");
                System.out.println("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    void modifierinfo(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        // Get current user from session
        Session session = Session.getInstance();
        Users currentUser = session.getCurrentUser();

        if (currentUser != null) {
            // Update user object with new values
            currentUser.setName(modifier_nom_uesr.getText());
            currentUser.setPrenom(modifier_prenom_user.getText());
            currentUser.setPhoneNumber(modifier_tel_user.getText());
            currentUser.setDateNaiss(modifier_dateN_user.getValue());
            currentUser.setRoleUser(modifier_type_user.getValue());

            // Update profile picture path if changed
            if (modifierfoto.getImage() != null) {
                currentUser.setImagesU(modifierfoto.getImage().getUrl());
            }

            // Call update method from service
            ps.update(currentUser);

            showAlert(AlertType.INFORMATION, "Succès", "Informations utilisateur mises à jour avec succès !");
        } else {
            showAlert(AlertType.ERROR, "Erreur", "Aucun utilisateur trouvé dans la session");
        }
    }

    // Validation Methods
    private boolean validateForm() {
        resetValidationLabels();
        boolean isValid = true;

        isValid &= validateName();
        isValid &= validateSurname();
        isValid &= validatePhone();
        isValid &= validateBirthDate();
        isValid &= validateUserType();
        isValid &= validateImage();

        return isValid;
    }

    private boolean validateName() {
        if (modifier_nom_uesr.getText().trim().isEmpty()) {
            Nomcontrole.setText("Nom requis");
            return false;
        }
        Nomcontrole.setText("");
        return true;
    }

    private boolean validateSurname() {
        if (modifier_prenom_user.getText().trim().isEmpty()) {
            Prénomcontroller.setText("Prénom requis");
            return false;
        }
        Prénomcontroller.setText("");
        return true;
    }

    private boolean validatePhone() {
        String phone = modifier_tel_user.getText().trim();
        if (!Pattern.matches("^\\d{8}$", phone)) {
            nimerocontroller.setText("Numéro invalide (8 chiffres)");
            return false;
        }
        nimerocontroller.setText("");
        return true;
    }

    private boolean validateBirthDate() {
        LocalDate birthDate = modifier_dateN_user.getValue();
        if (birthDate == null) {
            naissancecontroller.setText("Date de naissance requise");
            return false;
        }
        if (birthDate.plusYears(15).isAfter(LocalDate.now())) {
            naissancecontroller.setText("Âge minimum : 15 ans");
            return false;
        }
        naissancecontroller.setText("");
        return true;
    }

    private boolean validateUserType() {
        if (modifier_type_user.getValue() == null) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez sélectionner un type d'utilisateur.");
            return false;
        }
        return true;
    }

    private boolean validateImage() {
        if (modifierfoto.getImage() == null) {
            imagecpntroller.setText("Veuillez sélectionner une photo.");
            return false;
        }
        imagecpntroller.setText("");
        return true;
    }

    private void resetValidationLabels() {
        Nomcontrole.setText("");
        Prénomcontroller.setText("");
        nimerocontroller.setText("");
        naissancecontroller.setText("");
        imagecpntroller.setText("");
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}