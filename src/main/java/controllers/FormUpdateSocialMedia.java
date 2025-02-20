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
import services.SocialMediaServices;

import java.io.File;
import java.net.URL;
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

    private final SocialMediaServices socialMediaServices = new SocialMediaServices(){};
    private SocialMedia currentPost;
    private File selectedImageFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

            currentPost.setTitre(Titre.getText());
            currentPost.setContenu(contenu.getText());
            currentPost.setLieu(Lieu.getText());

            if (selectedImageFile != null) {
                currentPost.setImagemedia(selectedImageFile.getName());
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


    private void clearFields() {
        Titre.clear();
        contenu.clear();
        Lieu.clear();
        image.setImage(null);
        text1.setText("Publication ajoutée avec succès!");
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
