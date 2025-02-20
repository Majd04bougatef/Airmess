package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import services.UsersService;
import test.Session;
import models.Users;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;


public class Updateuser implements Initializable {


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
                    Image userImage = new Image(currentUser.getImagesU());
                    modifierfoto.setImage(userImage);
                    modifierfoto.setFitWidth(260);
                    modifierfoto.setFitHeight(310);
                    modifierfoto.setPreserveRatio(true);
                } catch (Exception e) {
                    System.err.println("Error loading user image: " + e.getMessage());
                }
            }
        } else {
            System.err.println("No user found in session");
        }
    }

    @FXML
    private ImageView modifierfoto;

    @FXML
    void modifierfoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String destinationPath = "C:/xampp/htdocs/imguser/" + file.getName();
            File destinationFile = new File(destinationPath);

            try {
                Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Image image = new Image(destinationFile.toURI().toString());
                modifierfoto.setImage(image);
                System.out.println("Image path in project: " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error saving image to project: " + e.getMessage());
            }
        }
    }

    private UsersService ps = new UsersService();


    @FXML
    void modifierinfo(ActionEvent event) {
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
            UsersService userService = new UsersService();
            userService.update(currentUser);

            System.out.println("User information updated successfully!");
        } else {
            System.err.println("No user found in session");
        }


    }


}
