package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import models.SocialMedia;
import services.SocialMediaServices;

import java.io.File;
import java.net.URL;
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
        }
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
