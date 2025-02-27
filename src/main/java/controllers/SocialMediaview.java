package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javafx.scene.Node;
import javafx.stage.Modality;
import test.Session;

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

    private final CommentaireServices CommentaireServices = new CommentaireServices() {};
    private Session session = Session.getInstance();


    private final AiServices aiServices = new AiServices();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            loadPosts();
            loadUserData();
        });
    }

    public void loadUserData() {
        int userId = session.getId_U();
        Users user = usersService.getById(userId);

        if (user != null) {
            username.setText(user.getName());
        } else {
            username.setText("Unknown User");
            showAlert("Error", "Could not load User Data", Alert.AlertType.ERROR);
        }
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

            ImageView userImageView = new ImageView();
            userImageView.setFitWidth(30);
            userImageView.setFitHeight(30);
            userImageView.setPreserveRatio(true);

            String defaultImagePath = "src/main/resources/image/imagepardefaut.jpg";

            if (user != null && user.getImagesU() != null && !user.getImagesU().isEmpty()) {
                try {
                    Image profileImage = new Image("file:/C:/xampp/htdocs/imguser/" + user.getImagesU());
                    userImageView.setImage(profileImage);
                } catch (Exception e) {
                    System.err.println("Error loading profile image: " + e.getMessage());
                    if (getClass().getResourceAsStream(defaultImagePath) != null) {
                        Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
                        userImageView.setImage(defaultImage);
                    } else {
                        System.err.println("Default profile image not found in resources!");
                    }
                }
            } else {
                if (getClass().getResourceAsStream(defaultImagePath) != null) {
                    Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
                    userImageView.setImage(defaultImage);
                } else {
                    System.err.println("Default profile image not found in resources!");
                }
            }

            Label usernameLabel = new Label(user != null ? "Posté par: " + user.getName() : "Posté par: Utilisateur inconnu");
            usernameLabel.getStyleClass().add("username-label");

            HBox userHeader = new HBox(10);
            userHeader.setAlignment(Pos.CENTER_LEFT);
            userHeader.getChildren().addAll(userImageView, usernameLabel);

            postBox.getChildren().addAll(titleLabel, userHeader, contentLabel, dateLabel, locationLabel);

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

            if (post.getId_U() == session.getId_U()) {
                Button updateButton = new Button("Update");
                updateButton.getStyleClass().add("update-button");
                updateButton.setUserData(post);
                updateButton.setOnAction(this::handleUpdate);

                Button deleteButton = new Button("Delete");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setUserData(post);
                deleteButton.setOnAction(this::handleDelete);

                buttonBox.getChildren().addAll(likeButton, updateButton, deleteButton);
            } else {
                buttonBox.getChildren().addAll(likeButton);
            }
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
            formAddController.setUserId(session.getId_U());

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
            if (postToDelete.getId_U() != session.getId_U()) {
                showAlert("Permission Denied", "You do not have permission to delete this post.", Alert.AlertType.ERROR);
                return;
            }

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
        Button sourceButton = (Button) event.getSource();
        SocialMedia postToUpdate = (SocialMedia) sourceButton.getUserData();

        if (postToUpdate != null) {
            if (postToUpdate.getId_U() != session.getId_U()) {
                showAlert("Permission Denied", "You do not have permission to update this post.", Alert.AlertType.ERROR);
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormUpdateSocialMedia.fxml"));
                AnchorPane updatePane = loader.load();

                FormUpdateSocialMedia controller = loader.getController();
                controller.setPostData(postToUpdate);
                //Ensure that you call this!
                controller.setUserId(session.getId_U());

                Scene scene = new Scene(updatePane);
                Stage stage = new Stage();
                stage.setTitle("Modifier la publication");
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load update form.", Alert.AlertType.ERROR);
            }
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

        for (javafx.scene.Node node : commentSection.getChildren()) {
            if (node instanceof TextArea) {
                commentText = (TextArea) node;
            } else if (node instanceof VBox) {
                commentList = (VBox) node;
            }
        }

        if (commentText == null || commentList == null) {
            System.out.println("Erreur : Impossible de trouver les champs de commentaire.");
            return;
        }

        SocialMedia post = (SocialMedia) btn.getUserData();
        if (post == null) {
            System.out.println("Erreur : Le post associé est introuvable.");
            return;
        }

        String originalCommentText = commentText.getText().trim();
        String finalCommentText = originalCommentText;

        if (originalCommentText.isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide.", Alert.AlertType.ERROR);
            return;
        }

        if (originalCommentText.length() > 500) {
            showAlert("Erreur", "Le commentaire ne peut pas dépasser 500 caractères.", Alert.AlertType.ERROR);
            return;
        }

        if (!validateContent(originalCommentText)) {
            return;
        }

        try {
            String proposedComment = aiServices.moderateContent(originalCommentText);

            if(proposedComment == null){
                proposedComment = originalCommentText;
            }

            if (proposedComment != null && !proposedComment.equals(originalCommentText)) {
                boolean approved = showApprovalDialog(originalCommentText, proposedComment);

                if (!approved) {
                    showAlert("Comment Rejected", "The comment was rejected and will not be added.", Alert.AlertType.INFORMATION);
                    commentText.clear();
                    return;
                }

                originalCommentText = proposedComment;
            }
        } catch (Exception e) {
            System.out.println("⚠ Erreur lors de la modération du commentaire : " + e.getMessage());
            showAlert("Erreur", "Impossible de vérifier le commentaire pour le moment.", Alert.AlertType.ERROR);
            return;
        }

        Commentaire commentaire = new Commentaire();
        commentaire.setIdEB(post.getIdEB());
        commentaire.setId_U(session.getId_U());
        commentaire.setDescription(originalCommentText);
        commentaire.setProposedDescription(originalCommentText);
        commentaire.setNumberLike(0);
        commentaire.setNumberDislike(0);
        commentaire.setApproved(true);

        CommentaireServices.add(commentaire);
        refreshCommentList(commentList, post.getIdEB());
        commentText.clear();
    }

    private boolean showApprovalDialog(String original, String proposed) {
        try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentApproval.fxml"));
            Parent root = loader.load();
            CommentApprovalController controller = loader.getController();
            String explanation = "This comment was flagged for potentially containing offensive language.  The AI suggests the above change.";
            controller.setCommentData(original, proposed, explanation);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Comment Approval");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            return controller.isApproved();
        }  catch (IOException e) {
        System.err.println("Error loading CommentApproval.fxml: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Failed to display comment approval dialog.", Alert.AlertType.ERROR);
        return false;
    }
}

    public void refreshCommentList(VBox commentList, int postId) {
        commentList.getChildren().clear();

        List<Commentaire> commentaires = CommentaireServices.getAllWithPostDetails(postId);

        for (Commentaire commentaire : commentaires) {
            Users user = usersService.getById(commentaire.getId_U());
            if (commentaire.getIdEB() == postId && commentaire.isApproved()) {
                HBox commentBox = new HBox(10);
                commentBox.setAlignment(Pos.CENTER_LEFT);
                commentBox.setUserData(commentaire);

                ImageView userImageView = new ImageView();
                userImageView.setFitWidth(20);
                userImageView.setFitHeight(20);
                userImageView.setPreserveRatio(true);

                if (user != null && user.getImagesU() != null && !user.getImagesU().isEmpty()) {
                    Image profileImage = new Image("file:/C:/xampp/htdocs/ImageSocialMedia/" + user.getImagesU());
                    userImageView.setImage(profileImage);
                } else {
                    Image defaultImage = new Image("/path/to/default/profile/image.png");
                    userImageView.setImage(defaultImage);
                }

                VBox commentContent = new VBox();
                Label usernameLabel = new Label(user != null ? user.getName() + ":" : "Unknown User:");
                usernameLabel.setStyle("-fx-font-weight: bold;");
                Label commentLabel = new Label(commentaire.getDescription());

                commentContent.getChildren().addAll(usernameLabel, commentLabel);

                HBox commentHeader = new HBox(5);
                commentHeader.getChildren().addAll(userImageView, commentContent);



                if (commentaire.getId_U() == session.getId_U()) {
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

                    commentBox.getChildren().addAll(commentHeader, updateButton, deleteButton);

                } else {
                    commentBox.getChildren().addAll(commentHeader);
                }

                commentList.getChildren().add(commentBox);
            }
        }
    }
    @FXML
    private void handleUpdateComment(ActionEvent event) {
        HBox commentBox = (HBox) ((Button) event.getSource()).getParent();
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


    public void handleSeeAllComments(ActionEvent event) {
    }
}