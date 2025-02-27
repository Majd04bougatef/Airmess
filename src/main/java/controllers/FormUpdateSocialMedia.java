package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javafx.stage.FileChooser;
import models.SocialMedia;
import services.AiServices;
import services.SocialMediaServices;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import java.util.ResourceBundle;

public class FormUpdateSocialMedia implements Initializable {

    @FXML
    private TextField Titre;
    @FXML
    private TextField contenu;
    @FXML
    private TextField Lieu;
    @FXML
    private Button bttnUpdate;
    @FXML
    private ImageView image;

    @FXML
    private Text text1;

    private int userId;
    private final SocialMediaServices socialMediaServices = new SocialMediaServices(){};
    private SocialMedia currentPost;
    private File selectedImageFile;
    private final AiServices aiServices = new AiServices();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPostData(SocialMedia post) {
        this.currentPost = post;
        if (post != null) {
            Titre.setText(post.getTitre());
            contenu.setText(post.getContenu());
            Lieu.setText(post.getLieu());

            if (post.getImagemedia() != null && !post.getImagemedia().isEmpty()) {
                File imageFile = new File("C:/xampp/htdocs/ImageSocialMedia/" + post.getImagemedia());
                if (imageFile.exists()) {
                    image.setImage(new Image(imageFile.toURI().toString()));
                }
            }
        }
    }

    @FXML
    void bttnUpdate(ActionEvent event) {
        if (currentPost != null) {

            if (currentPost.getId_U() != userId) {
                showAlert("Permission Denied", "You do not have permission to update this post.", Alert.AlertType.ERROR);
                return;
            }

            if (Titre.getText().isEmpty() || contenu.getText().isEmpty() || Lieu.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs !", Alert.AlertType.ERROR);
                return;
            }

            if (!Lieu.getText().matches("[a-zA-Z0-9\\s]+")) {
                showAlert("Erreur", "Le lieu ne doit contenir que des lettres, des chiffres et des espaces !", Alert.AlertType.ERROR);
                return;
            }

            if (!validateContent(Titre.getText()) || !validateContent(contenu.getText()) || !validateContent(Lieu.getText())) {
                return;
            }
            try {
                String moderatedContent = aiServices.moderateContent(contenu.getText());
                currentPost.setContenu(moderatedContent);
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de modérer le contenu.", Alert.AlertType.ERROR);
                return;
            }

            currentPost.setTitre(Titre.getText());

            currentPost.setLieu(Lieu.getText());

            String newImageName = null;
            if (selectedImageFile != null) {
                try {
                    newImageName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
                    Path destinationPath = Paths.get("C:/xampp/htdocs/ImageSocialMedia/", newImageName);

                    Files.copy(selectedImageFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                    currentPost.setImagemedia(newImageName);
                } catch (IOException e) {
                    showAlert("Error", "Failed to save new image: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                    return;
                }
            }

            socialMediaServices.update(currentPost);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mise à jour réussie");
            alert.setHeaderText(null);
            alert.setContentText("La publication a été mise à jour avec succès !");
            alert.showAndWait();

            bttnUpdate.getScene().getWindow().hide();
            clearFields();

        }
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearFields() {
        Titre.clear();
        contenu.clear();
        Lieu.clear();
        image.setImage(null);
        text1.setText("Publication updated successfully!");
    }

    @FXML
    void ajoutimage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            image.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }
}