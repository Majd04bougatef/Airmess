package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Commentaire;
import models.SocialMedia;
import models.Users;
import services.AiServices;
import services.CommentaireServices;
import services.UsersService;
import services.SocialMediaServices;



import java.util.Arrays;
import java.util.List;
import java.util.Optional;



public class SocialMediaview {

    @FXML
    private VBox commentSection;
    @FXML
    private VBox commentList;
    @FXML
    private VBox postContainer;
    @FXML
    private Button btnAddComment;

    @FXML
    private Button btnComment;
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

    private final SocialMediaServices socialMediaServices = new SocialMediaServices() {
    };
    private final UsersService usersService = new UsersService() {
    };
    private final CommentaireServices CommentaireServices = new CommentaireServices() {
    };
    private final AiServices aiServices = new AiServices();

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

            Label locationLabel = new Label("\uD83D\uDCCD Lieu : " + (post.getLieu() != null ? post.getLieu() : "Lieu inconnu"));
            locationLabel.getStyleClass().add("location-label");

            String publicationDate = post.getPublicationDate() != null ? post.getPublicationDate().toString() : "Date inconnue";
            Label dateLabel = new Label("Publié le : " + publicationDate);
            dateLabel.getStyleClass().add("date-label");


            Users user = usersService.getById(post.getId_U());
            Label usernameLabel = new Label(user != null ? "Posté par: " + user.getName() : "Posté par: Utilisateur inconnu");
            usernameLabel.getStyleClass().add("username-label");

            postBox.getChildren().addAll(titleLabel, usernameLabel, contentLabel,dateLabel,locationLabel);




            if (post.getImagemedia() != null && !post.getImagemedia().isEmpty()) {
                Image postImage = new Image("file:/C:/xampp/htdocs/ImageSocialMedia/" + post.getImagemedia());
                ImageView imageView = new ImageView(postImage);
                imageView.setFitWidth(200);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true);
                postBox.getChildren().add(imageView);
            }

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

            Button commentButton = new Button("💬 Commenter");
            commentButton.getStyleClass().add("comment-button");
            commentButton.setOnAction(this::handleComment);

            VBox commentSection = new VBox(10);
            commentSection.setVisible(false);
            commentSection.setId("commentSection");

            TextArea commentText = new TextArea();
            commentText.setPromptText("Écrire un commentaire...");
            commentText.setPrefHeight(50);
            commentText.setId("commentText");

            Button submitCommentButton = new Button("📩 Publier");
            submitCommentButton.getStyleClass().add("submit-comment-button");
            submitCommentButton.setOnAction(this::handleAddComment);

            VBox commentList = new VBox(5);
            commentList.setId("commentList");
            submitCommentButton.setUserData(post);


            commentSection.getChildren().addAll(commentText, submitCommentButton, commentList);
            postBox.getChildren().addAll(commentButton, commentSection);


            postContainer.getChildren().add(postBox);
        }
    }


    @FXML
    void Ajouterpost(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddSocialMedia.fxml"));
            AnchorPane formAddPane = loader.load();

            FormAddSocialMedia formAddController = loader.getController();
            formAddController.setSocialMediaViewController(this);

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

        if (post != null) {
            System.out.println("Likes avant incrémentation : " + post.getLikee());

            post.setLikee(post.getLikee() + 1);
            socialMediaServices.updateLikes(post);

            SocialMedia updatedPost = socialMediaServices.getById(post.getIdEB());
            if (updatedPost != null) {
                System.out.println("Likes après mise à jour en BD : " + updatedPost.getLikee());
                likeButton.setText("👍 Like (" + updatedPost.getLikee() + ")");
            } else {
                System.out.println("⚠ Erreur: la mise à jour en BD a échoué.");
            }

            refreshPosts();
        }
    }


    @FXML

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
                    try {

                        Object parent = sourceButton.getParent();
                        if (parent instanceof VBox) {
                            VBox postBox = (VBox) parent;
                            postBox.setVisible(false);
                            postBox.setManaged(false);
                        } else {
                            System.out.println("impossible de supprimé la publication.");
                        }

                        List<Commentaire> commentaires = CommentaireServices.getAllWithPostDetails(postToDelete.getIdEB());
                        for (Commentaire commentaire : commentaires) {
                            CommentaireServices.delete(commentaire);
                        }

                        socialMediaServices.delete(postToDelete);

                       refreshPosts();


                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Succès");
                        successAlert.setHeaderText("La publication a été supprimée avec succès.");
                        successAlert.showAndWait();

                        System.out.println("Post supprimé avec succès");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Erreur");
                        errorAlert.setHeaderText("Une erreur est survenue lors de la suppression.");
                        errorAlert.setContentText("Veuillez réessayer plus tard.");
                        errorAlert.showAndWait();
                    }
                }
            });
        }
    }


    @FXML
    void handleUpdate(ActionEvent event) {
        try {
            Button sourceButton = (Button) event.getSource();
            SocialMedia postToUpdate = (SocialMedia) sourceButton.getUserData();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormUpdateSocialMedia.fxml"));
            AnchorPane updatePane = loader.load();

            FormUpdateSocialMedia controller = loader.getController();
            controller.setPostData(postToUpdate);

            Scene scene = new Scene(updatePane);
            Stage stage = new Stage();
            stage.setTitle("Modifier la publication");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleComment(ActionEvent event) {
        Button btn = (Button) event.getSource();
        VBox postBox = (VBox) btn.getParent();
        VBox commentSection = null;

        for (javafx.scene.Node node : postBox.getChildren()) {
            if (node instanceof VBox && "commentSection".equals(node.getId())) {
                commentSection = (VBox) node;
                break;
            }
        }

        if (commentSection != null) {
            commentSection.setVisible(!commentSection.isVisible());
        } else {
            System.out.println("Erreur : La section des commentaires n'a pas été trouvée !");
        }
    }


    @FXML
    public void handleAddComment(ActionEvent event) {
        Button btn = (Button) event.getSource();
        VBox commentSection = (VBox) btn.getParent();

        TextArea commentText = null;
        VBox commentList = null;

        // Trouver les composants de la section commentaire
        for (javafx.scene.Node node : commentSection.getChildren()) {
            if (node instanceof TextArea) {
                commentText = (TextArea) node;
            } else if (node instanceof VBox) {
                commentList = (VBox) node;
            }
        }

        // Vérification des composants
        if (commentText == null || commentList == null) {
            System.out.println("Erreur : Impossible de trouver les champs de commentaire.");
            return;
        }

        SocialMedia post = (SocialMedia) btn.getUserData();
        if (post == null) {
            System.out.println("Erreur : Le post associé est introuvable.");
            return;
        }

        String newComment = commentText.getText().trim();

        // Vérifier si le commentaire est vide
        if (newComment.isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier la longueur du commentaire
        if (newComment.length() > 500) {
            showAlert("Erreur", "Le commentaire ne peut pas dépasser 500 caractères.", Alert.AlertType.ERROR);
            return;
        }

        // Validation du contenu avant l'appel de l'IA
        if (!validateContent(newComment)) {
            return;
        }

        // Modération du commentaire avec l'IA
        AiServices aiServices = new AiServices();
        String filteredText;
        try {
            filteredText = aiServices.moderateContent(newComment);
        } catch (Exception e) {
            System.out.println("⚠ Erreur lors de la modération du commentaire : " + e.getMessage());
            showAlert("Erreur", "Impossible de vérifier le commentaire pour le moment.", Alert.AlertType.ERROR);
            return;
        }

        Commentaire commentaire = new Commentaire(post.getIdEB(), 1, filteredText, 0, 0);
        CommentaireServices.add(commentaire);

// Vérifier si le commentaire est bien inséré (ex: récupérer par ID)
        if (commentaire.getIdC() > 0) {
            System.out.println("✅ Commentaire ajouté : " + filteredText);
            refreshCommentList(commentList, post.getIdEB());
            commentText.clear();
        } else {
            System.out.println("⚠ Erreur lors de l'ajout du commentaire.");
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du commentaire.", Alert.AlertType.ERROR);
        }

    }



    public void refreshCommentList(VBox commentList, int postId) {
        commentList.getChildren().clear();

        List<Commentaire> commentaires = CommentaireServices.getAllWithPostDetails(postId);

        for (Commentaire commentaire : commentaires) {
            if (commentaire.getIdEB() == postId) {
                HBox commentBox = new HBox(10);
                commentBox.setAlignment(Pos.CENTER_LEFT);
                commentBox.setUserData(commentaire);

                Label commentLabel = new Label(commentaire.getDescription());
                commentLabel.getStyleClass().add("comment-label");

                Button updateButton = new Button("Modifier");
                updateButton.getStyleClass().add("update-comment-button");
                updateButton.setOnAction(this::handleUpdateComment);

                Button deleteButton = new Button("Supprimer");
                deleteButton.getStyleClass().add("delete-comment-button");
                deleteButton.setOnAction(this::handleDeleteComment);

                updateButton.setPrefSize(60, 25);
                deleteButton.setPrefSize(60, 25);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);


                commentBox.getChildren().addAll(commentLabel, updateButton, deleteButton);

                commentList.getChildren().add(commentBox);
            }
        }
    }



    @FXML
    private void handleUpdateComment(ActionEvent event) {
        HBox commentBox = (HBox) ((javafx.scene.control.Button) event.getSource()).getParent();
        Label commentLabel = (Label) commentBox.getChildren().get(0);
        Commentaire commentaire = (Commentaire) commentBox.getUserData();

        TextInputDialog dialog = new TextInputDialog(commentLabel.getText());
        dialog.setTitle("Modifier le commentaire");
        dialog.setHeaderText("Modification du commentaire");
        dialog.setContentText("Entrez le nouveau texte :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newText -> {

            if (newText.trim().isEmpty()) {
                showAlert("Erreur", "Le commentaire ne peut pas être vide.", Alert.AlertType.ERROR);
                return;
            }

            if (newText.length() > 500) {
                showAlert("Erreur", "Le commentaire ne peut pas dépasser 500 caractères.", Alert.AlertType.ERROR);
                return;
            }

            if (!validateContent(newText)) {
                showAlert("Erreur", "Le commentaire contient des caractères non autorisés.", Alert.AlertType.ERROR);
                return;
            }

            commentaire.setDescription(newText);
            CommentaireServices.update(commentaire);
            commentLabel.setText(newText);
            System.out.println("Commentaire mis à jour !");
        });
    }

    @FXML
    private void handleDeleteComment(ActionEvent event) {
        HBox commentBox = (HBox) ((Button) event.getSource()).getParent();
        Commentaire commentaire = (Commentaire) commentBox.getUserData();

        if (commentaire == null) {
            System.out.println("Erreur : Impossible de récupérer le commentaire.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression de commentaire");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");
        alert.setContentText("Cette action est irréversible. Le commentaire sera définitivement supprimé.");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CommentaireServices.delete(commentaire);
            ((VBox) commentBox.getParent()).getChildren().remove(commentBox);
            System.out.println("Commentaire supprimé !");
        } else {
            System.out.println("Suppression annulée.");
        }
    }


    public void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
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




}

