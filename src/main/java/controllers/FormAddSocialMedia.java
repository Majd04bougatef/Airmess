package controllers;

import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.SocialMediaServices;
import models.SocialMedia;
import services.UsersService;

public class FormAddSocialMedia {

    @FXML
    private TextField contenu;

    @FXML
    private TextField Lieu;

    @FXML
    private TextField Titre;

    @FXML
    private DatePicker publication_date_picker;

    @FXML
    private Text text1;

    private int id_u;  // Assurez-vous de définir ou récupérer l'ID de l'utilisateur ici

    private final SocialMediaServices socialMediaServices = new SocialMediaServices() {};

    @FXML
    public void initialize() {
        // L'initialisation est déjà configurée pour des actions spécifiques.
    }

    @FXML
    private void bttnsc() {

        SocialMedia socialMedia = new SocialMedia();

        // Contrôle de saisie : Vérifie si tous les champs sont remplis
        if (Titre.getText().isEmpty() || contenu.getText().isEmpty() || Lieu.getText().isEmpty() || publication_date_picker.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !", AlertType.ERROR);
            return;
        }

        // Récupère les données et les assigne à l'objet socialMedia
        socialMedia.setTitre(Titre.getText());
        socialMedia.setContenu(contenu.getText());
        socialMedia.setLieu(Lieu.getText());

        // Convertit la date sélectionnée et assigne la publication
        socialMedia.setPublicationDate(java.sql.Date.valueOf(publication_date_picker.getValue()));

        socialMedia.setId_U(1);

        socialMediaServices.add(socialMedia);

        showAlert("Succès", "Publication publiée avec succès !", AlertType.INFORMATION);
        clearFields();

    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void clearFields() {
        Titre.clear();
        contenu.clear();
        Lieu.clear();
        publication_date_picker.setValue(null);
        text1.setText("Publication ajoutée avec succès!");
    }


}
