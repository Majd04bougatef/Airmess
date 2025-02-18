package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import services.UsersService;


import models.Users;


import javafx.stage.FileChooser;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class Signup {

    @FXML
    private TextField Email_user;

    @FXML
    private DatePicker dateN_user;

    @FXML
    private TextField nom_uesr;

    @FXML
    private TextField password_user;

    @FXML
    private TextField prenom_user;

    @FXML
    private TextField tel_user;

    @FXML
    private ComboBox<String> type_user;

    public void initialize() {
        if (type_user != null) {
            type_user.getItems().addAll("Voyageurs", "Entreprise", "Créateur de contenu");
        }
    }

    private UsersService ps = new UsersService();

    @FXML
    private ImageView user_photo;

    @FXML
    void Ajouterunephoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            String destinationPath = "C:/xampp/htdocs/imguser/" + file.getName();
            File destinationFile = new File(destinationPath);

            try {
                Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Image image = new Image(destinationFile.toURI().toString());
                user_photo.setImage(image);
                System.out.println("Image path in project: " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error saving image to project: " + e.getMessage());
            }
        }
    }

    @FXML
    void enregistrer(ActionEvent event) {
        Users user = new Users();
        user.setName(nom_uesr.getText());
        user.setPrenom(prenom_user.getText());
        user.setEmail(Email_user.getText());
        user.setPassword(password_user.getText());
        user.setRoleUser(type_user.getValue());
        user.setDateNaiss(dateN_user.getValue());
        user.setPhoneNumber(tel_user.getText());
        user.setImagesU(user_photo.getImage() != null ? user_photo.getImage().getUrl() : null);

        if (nom_uesr.getText().isEmpty() || Email_user.getText().isEmpty() || password_user.getText().isEmpty() || type_user.getValue() == null || dateN_user.getValue() == null || tel_user.getText().isEmpty()) {
            System.out.println("Error: All fields must be filled!");
            return;
        }

        ps.add(user);
        System.out.println("User added successfully!");
        clearFields();

    }

    private void clearFields() {
        nom_uesr.clear();
        prenom_user.clear();
        Email_user.clear();
        password_user.clear();
        tel_user.clear();
        dateN_user.setValue(null);
        type_user.setValue(null);
        user_photo.setImage(null);
    }

}
