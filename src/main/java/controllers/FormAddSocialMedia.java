package controllers;

import javafx.scene.image.ImageView;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import services.AiServices;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;

import java.util.Arrays;
import java.util.List;



public class FormAddSocialMedia {

    @FXML
    private TextField contenu;

    @FXML
    private TextField Lieu;

    @FXML
    private TextField Titre;


    @FXML
    private Text text1;

    @FXML
    private ImageView image;
    private String imageName;



    private int id_u;

    private final SocialMediaServices socialMediaServices = new SocialMediaServices() {};
    private final AiServices aiServices = new AiServices();

    @FXML
    public void initialize() {

    }




    @FXML
    void ajoutimage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg","*.gif", "*.avif"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imageName = System.currentTimeMillis() + "_" + file.getName();

            String destinationPath = "C:/xampp/htdocs/ImageSocialMedia/" + imageName;
            File destinationFile = new File(destinationPath);

            try {
                Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Image img = new Image(destinationFile.toURI().toString());
                image.setImage(img);


                System.out.println("Image enregistrée avec succès : " + imageName);
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'enregistrer l'image.", AlertType.ERROR);
                System.out.println("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void bttnsc() {
        SocialMedia socialMedia = new SocialMedia();



        if (Titre.getText().isEmpty() || contenu.getText().isEmpty() || Lieu.getText().isEmpty() ) {
            showAlert("Erreur", "Veuillez remplir tous les champs !", AlertType.ERROR);
            return;
        }

        if (!Lieu.getText().matches("[a-zA-Z0-9\\s]+")) {
            showAlert("Erreur", "Le lieu ne doit contenir que des lettres, des chiffres et des espaces !", AlertType.ERROR);
            return;
        }

        if (!validateContent(Titre.getText()) || !validateContent(contenu.getText()) || !validateContent(Lieu.getText())) {
            return;
        }

        try {
            System.out.println("Modération du contenu : " + contenu.getText());  // Logging
            String filteredContent = aiServices.moderateContent(contenu.getText());
            System.out.println("Contenu modéré : " + filteredContent);  // Affiche le contenu modéré
            contenu.setText(filteredContent); // Met à jour le champ de contenu avec le texte modéré
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de modérer le contenu.", AlertType.ERROR);
            return;
        }

        socialMedia.setTitre(Titre.getText());
        socialMedia.setContenu(contenu.getText());
        socialMedia.setLieu(Lieu.getText());
        socialMedia.setPublicationDate(Date.valueOf(LocalDate.now()));

        socialMedia.setId_U(2);


        if (imageName != null) {
            socialMedia.setImagemedia(imageName);
        } else {

            socialMedia.setImagemedia(null);
        }
        socialMediaServices.add(socialMedia);
        showAlert("Succès", "Publication publiée avec succès !", AlertType.INFORMATION);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SocialMediaview.fxml"));
            AnchorPane root = loader.load();
            SocialMediaview socialMediaviewController = loader.getController();
            socialMediaviewController.refreshPosts();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearFields();
    }

    public void setSocialMediaViewController(SocialMediaview controller) {
    }
    List<String> forbiddenWords = Arrays.asList("ccc", "bbb");

    public  boolean validateContent(String content) {
        if (content.isEmpty() || content.trim().isEmpty()) {
            showAlert("Erreur", "Le contenu ne peut pas être vide ou composé uniquement d'espaces.", Alert.AlertType.ERROR);
            return false;
        }
        for (String word : forbiddenWords) {
            if (content.toLowerCase().contains(word)) {
                showAlert("Erreur", "Le contenu contient des mots interdits.", Alert.AlertType.ERROR);
                return false;
            }
        }



        return true;
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
        image.setImage(null);
        text1.setText("Publication ajoutée avec succès!");
    }


}