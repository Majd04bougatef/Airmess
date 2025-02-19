package controllers;

import javafx.event.ActionEvent;
import java.sql.Date;
import java.time.LocalDate;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import models.SocialMedia;
import models.Users;
import services.UsersService;
import services.SocialMediaServices;
import java.util.List;
import java.text.SimpleDateFormat;
import javafx.scene.layout.HBox;


public class SocialMediaview {

    @FXML
    private VBox postContainer;
    @FXML
    private Button btnAjout;
    @FXML
    private ImageView profileImage;
    @FXML
    private Label username;
    @FXML
    private Label postDate;
    @FXML
    private Label postContent;
    @FXML
    private Button btnlike;

    @FXML
    private Button btncmntr;
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnUpdate;

    private final SocialMediaServices socialMediaServices = new SocialMediaServices() {};
    private final UsersService usersService = new UsersService(){};

    @FXML
    public void initialize() {
        loadPosts();
    }

    @FXML
    public void loadPosts() {
        postContainer.getChildren().clear();
        List<SocialMedia> posts = socialMediaServices.getAll();

        for (SocialMedia post : posts) {
            VBox postBox = new VBox(10);
            postBox.getStyleClass().add("post-box");

            Label titleLabel = new Label(post.getTitre());
            titleLabel.getStyleClass().add("title-label");

            Label contentLabel = new Label(post.getContenu());
            contentLabel.setWrapText(true);
            contentLabel.getStyleClass().add("content-label");

            Users user = usersService.getById(post.getId_U());
            Label usernameLabel = new Label(user != null ? "Posté par: " + user.getName() : "Posté par: Utilisateur inconnu");
            usernameLabel.getStyleClass().add("username-label");

            postBox.getChildren().addAll(titleLabel, usernameLabel, contentLabel);

            if (post.getImagemedia() != null && !post.getImagemedia().isEmpty()) {
                Image postImage = new Image("file:/C:/xampp/htdocs/ImageSocialMedia/" + post.getImagemedia());
                ImageView imageView = new ImageView(postImage);
                imageView.setFitWidth(200);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true);
                postBox.getChildren().add(imageView);
            }

            // Ajouter un HBox pour les boutons
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_LEFT);
            Button likeButton = new Button("Like (" + post.getLikee() + ")");
            likeButton.getStyleClass().add("like-button");
            likeButton.setUserData(post);
            likeButton.setOnAction(event -> handleLike(event));

            Button updateButton = new Button("Update");
            updateButton.getStyleClass().add("update-button");
            updateButton.setUserData(post);
            updateButton.setOnAction(this::handleUpdate);

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setUserData(post);
            deleteButton.setOnAction(this::handleDelete);

            buttonBox.getChildren().addAll(likeButton, updateButton, deleteButton);
            postBox.getChildren().add(buttonBox);


            postContainer.getChildren().add(postBox);
        }
    }


    @FXML
    void Ajouterpost(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddSocialMedia.fxml"));
            AnchorPane formAddPane = loader.load();

            Scene scene = new Scene(formAddPane);
            Stage stage = new Stage();
            stage.setTitle("Ajouter un post");
            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleLike(ActionEvent event) {
        Button likeButton = (Button) event.getSource();
        SocialMedia post = (SocialMedia) likeButton.getUserData();
        System.out.println("Likes avant incrémentation : " + post.getLikee());

        post.setLikee(post.getLikee() + 1);
        System.out.println("Likes après incrémentation : " + post.getLikee());

        socialMediaServices.updateLikes(post);

        System.out.println("Likes après mise à jour dans la base (avant rechargement) : " + post.getLikee());

        likeButton.setText("👍 Like (" + post.getLikee() + ")");
    }




    @FXML
    public void handleComment(SocialMedia post) {
    }
    public void refreshPosts() {
        postContainer.getChildren().clear();
        loadPosts();
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        SocialMedia postToDelete = (SocialMedia) sourceButton.getUserData();

        if (postToDelete != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Suppression de Publication");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette publication ?");
            alert.setContentText("Une fois supprimée, la publication ne pourra pas être récupérée.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    socialMediaServices.delete(postToDelete);
                    refreshPosts();
                    System.out.println("Post supprimé");
                }
            });
        }
    }




    @FXML
    void handleUpdate(ActionEvent event) {

    }


}