package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import models.Commentaire;
import models.SocialMedia;
import models.Users;
import services.AiServices;
import services.CommentaireServices;
import services.SocialMediaServices;
import services.UsersService;
import java.io.IOException;
import java.util.*;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import test.Session;
import java.io.File;


public class SocialMediaview {

    @FXML
    private TextArea commentText;
    @FXML
    private VBox commentSection;
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
    private Session session = Session.getInstance();


    private final AiServices aiServices = new AiServices();

    private Set<Integer> likedPosts = new HashSet<>();

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
            VBox postBox = createPostBox(post);
            postContainer.getChildren().add(postBox);
        }
    }


    private VBox createPostBox(SocialMedia post) {
        VBox postBox = new VBox(10);
        postBox.getStyleClass().add("post-box");

        Label titleLabel = createLabel(post.getTitre(), "title-label");
        Label contentLabel = createLabel(post.getContenu(), "content-label");
        contentLabel.setWrapText(true);

        String locationText = "\uD83D\uDCCD Lieu : " + (post.getLieu() != null ? post.getLieu() : "Lieu inconnu");
        Label locationLabel = createLabel(locationText, "location-label");

        String publicationDate = post.getPublicationDate() != null ? post.getPublicationDate().toString() : "Date inconnue";
        Label dateLabel = createLabel("Publié le : " + publicationDate, "date-label");

        HBox userHeader = createUserHeader(post.getId_U());

        postBox.getChildren().addAll(titleLabel, userHeader, contentLabel, dateLabel, locationLabel);

        if (post.getImagemedia() != null && !post.getImagemedia().isEmpty()) {
            String imagePath = "file:/C:/xampp/htdocs/ImageSocialMedia/" + post.getImagemedia();
            File imageFile = new File(imagePath.substring(5));
            if (imageFile.exists()) {
                try {
                    Image postImage = new Image(imagePath);
                    ImageView imageView = createImageView(postImage, 400, 400);
                    postBox.getChildren().add(imageView);
                } catch (Exception e) {
                    System.err.println("Error loading post image: " + e.getMessage());
                    displayDefaultPostImage(postBox);
                }
            } else {
                System.out.println("Image file not found: " + imagePath);
                displayDefaultPostImage(postBox);
            }
        }

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_LEFT);

        HBox actionsBox = createActionsBox(post);
        VBox commentSectionLocal = createCommentSection(post);
        Button commentButton = createCommentButton(commentSectionLocal, post);

        buttonBar.getChildren().addAll(actionsBox, commentButton);

        postBox.getChildren().addAll(buttonBar, commentSectionLocal);
        commentSectionLocal.managedProperty().bind(commentSectionLocal.visibleProperty());
        return postBox;
    }

    private void displayDefaultPostImage(VBox postBox) {
        String defaultImagePath = "/image/default_post_image.png";
        Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
        ImageView defaultImageView = createImageView(defaultImage, 200, 200);
        postBox.getChildren().add(defaultImageView);
    }

    private Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private ImageView createImageView(Image image, double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private HBox createUserHeader(int userId) {
        UsersService usersService = new UsersService();
        Users user = usersService.getById(userId);

        ImageView userImageView = new ImageView();
        userImageView.setFitWidth(30);
        userImageView.setFitHeight(30);
        userImageView.setPreserveRatio(true);

        String defaultImagePath = "/image/imagepardefaut.jpg";
        Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));

        if (user != null && user.getImagesU() != null && !user.getImagesU().isEmpty()) {
            try {
                Image profileImage = new Image("file:/C:/xampp/htdocs/imguser/" + user.getImagesU());
                userImageView.setImage(profileImage);
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
                userImageView.setImage(defaultImage);
            }
        } else {
            userImageView.setImage(defaultImage);
        }

        Label usernameLabel = new Label(user != null ? "Posté par: " + user.getName() : "Posté par: Utilisateur inconnu");
        usernameLabel.getStyleClass().add("username-label");

        HBox userHeader = new HBox(10);
        userHeader.setAlignment(Pos.CENTER_LEFT);
        userHeader.getChildren().addAll(userImageView, usernameLabel);

        return userHeader;
    }

    private HBox createActionsBox(SocialMedia post) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button likeButton = new Button("👍");
        likeButton.setAccessibleText("Like this post");
        likeButton.setStyle("-fx-font-size: 20px;");
        likeButton.setPadding(new Insets(5,10,5,10));
        likeButton.getStyleClass().add("like-button");
        likeButton.setUserData(post);
        likeButton.setOnAction(event -> handleLike(event));

        if (post.getId_U() == session.getId_U()) {
            Button updateButton = new Button("✏");
            updateButton.setAccessibleText("Edit this post");
            updateButton.setStyle("-fx-font-size: 20px;");
            updateButton.setPadding(new Insets(5,10,5,10));
            updateButton.getStyleClass().add("update-button");
            updateButton.setUserData(post);
            updateButton.setOnAction(this::handleUpdate);

            Button deleteButton = new Button("🗑");
            deleteButton.setAccessibleText("Delete this post");
            deleteButton.setStyle("-fx-font-size: 20px;");
            deleteButton.setPadding(new Insets(5,10,5,10));
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setUserData(post);
            deleteButton.setOnAction(this::handleDelete);

            buttonBox.getChildren().addAll(likeButton, updateButton, deleteButton);
        } else {
            buttonBox.getChildren().addAll(likeButton);
        }

        return buttonBox;
    }
    private VBox createCommentSection(SocialMedia post) {
        final VBox commentSectionLocal = new VBox(10);
        commentSectionLocal.setVisible(false);
        commentSectionLocal.setId("commentSection");
        commentSectionLocal.managedProperty().bind(commentSectionLocal.visibleProperty());
        return commentSectionLocal;
    }


    private Button createCommentButton(VBox commentSectionLocal, SocialMedia post) {
        Button commentButton = new Button("💬 ");
        commentButton.setUserData(post);
        commentButton.getStyleClass().add("comment-button");
        commentButton.setOnAction(event -> {
            System.out.println("Comment button clicked");
            boolean isVisible = !commentSectionLocal.isVisible();
            commentSectionLocal.setVisible(isVisible);
            System.out.println("Comment Section Visible: " + isVisible);

            if (isVisible) {
                commentSectionLocal.getChildren().clear();

                TextArea commentTextLocal = new TextArea();
                commentTextLocal.setPromptText("Écrire un commentaire...");
                commentTextLocal.setPrefHeight(50);
                commentTextLocal.setId("commentText");

                Button submitCommentButton = new Button("📩");
                submitCommentButton.getStyleClass().add("submit-comment-button");
                submitCommentButton.setOnAction(e -> handleAddCommentAction(e, post, commentSectionLocal, commentTextLocal));
                submitCommentButton.setUserData(post);

                commentSectionLocal.getChildren().addAll(commentTextLocal, submitCommentButton);

                List<Commentaire> commentaires = CommentaireServices.getAllWithPostDetails(post.getIdEB());

                for (Commentaire commentaire : commentaires) {
                    Users user = usersService.getById(commentaire.getId_U());
                    if (commentaire.getIdEB() == post.getIdEB() && commentaire.isApproved()) {
                        HBox commentBox = createCommentWithProfilePicture(commentaire, user);
                        commentSectionLocal.getChildren().add(commentBox);
                    }
                }

                System.out.println("Comment Section Children: " + commentSectionLocal.getChildren());
            }
        });
        return commentButton;
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
            int postId = post.getIdEB();

            if (likedPosts.contains(postId)) {
                showAlert("Info", "Vous avez déjà aimé cette publication.", Alert.AlertType.INFORMATION);
                return;
            }

            System.out.println("Likes avant incrémentation : " + post.getLikee());

            post.setLikee(post.getLikee() + 1);
            socialMediaServices.updateLikes(post);

            SocialMedia updatedPost = socialMediaServices.getById(postId);
            if (updatedPost != null) {
                System.out.println("Likes après mise à jour en BD : " + updatedPost.getLikee());
                likeButton.setText("👍  (" + updatedPost.getLikee() + ")");
            } else {
                System.out.println("⚠ Erreur: la mise à jour en BD a échoué.");
            }

            likedPosts.add(postId);
        }
    }


    public void resetLikedPosts() {
        likedPosts.clear();
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
            alert.setContentText("Cette action est irréversible. Le commentaire sera définitivement supprimé.");

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
        SocialMedia post = (SocialMedia) btn.getUserData();
        VBox postBox = (VBox) btn.getParent().getParent();
        VBox commentSectionLocal = null;

        for (javafx.scene.Node node : postBox.getChildren()) {
            if (node instanceof VBox && "commentSection".equals(node.getId())) {
                commentSectionLocal = (VBox) node;
                break;
            }
        }


        if (commentSectionLocal != null) {
            boolean isVisible = !commentSectionLocal.isVisible();
            commentSectionLocal.setVisible(isVisible);

            if (isVisible) {
                refreshCommentList(commentSectionLocal, post);
            }
        } else {
            System.out.println("Erreur : La section des commentaires n'a pas été trouvée !");
        }
    }


    private String getApprovedComment(String originalComment) {
        try {
            String proposedComment = aiServices.moderateContent(originalComment);

            if (proposedComment == null) {
                showAlert("Comment Rejected", "Your comment violates content guidelines and cannot be posted.", Alert.AlertType.INFORMATION);
                return null;
            }

            String finalComment = showApprovalDialog(originalComment, proposedComment);

            if (finalComment == null) {
                showAlert("Comment Rejected", "The comment was rejected and will not be added.", Alert.AlertType.INFORMATION);
                return null;
            }
            if (!validateContent(finalComment)) {
                return null;
            }
            String reModeratedComment = aiServices.moderateContent(finalComment);

            if (reModeratedComment == null) {
                showAlert("Comment Rejected", "The comment was rejected and will not be added.", Alert.AlertType.INFORMATION);
                return null;
            }

            return reModeratedComment;
        } catch (Exception e) {
            System.err.println("Error during AI moderation: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to moderate comment.", Alert.AlertType.ERROR);
            return null;
        }
    }

    @FXML
    public void handleAddComment(ActionEvent event) {
        Button btn = (Button) event.getSource();
        SocialMedia post = (SocialMedia) btn.getUserData();

        VBox postBox = (VBox) btn.getParent().getParent().getParent().getParent().getParent();
        VBox commentSectionLocal = null;
        TextArea commentTextLocal = null;

        for (javafx.scene.Node node : postBox.getChildren()) {
            if (node instanceof VBox && "commentSection".equals(((VBox) node).getId())) {
                commentSectionLocal = (VBox) node;
                for (javafx.scene.Node commentNode : ((VBox) node).getChildren()) {
                    if (commentNode instanceof TextArea && "commentText".equals(((TextArea) commentNode).getId())) {
                        commentTextLocal = (TextArea) commentNode;
                        break;
                    }
                }
                break;
            }
        }

        if (commentSectionLocal != null && commentTextLocal != null && post!=null) {
            handleAddCommentAction(event, post, commentSectionLocal, commentTextLocal);
        } else {
            System.out.println("Error: Could not find commentSection VBox or commentText TextArea");
        }
    }

    @FXML
    public void handleAddCommentAction(ActionEvent event, SocialMedia post, VBox commentSection, TextArea commentText) {

        if (post == null) {
            System.out.println("Erreur: Le post associé est introuvable.");
            return;
        }

        if (commentSection == null) {
            System.out.println("Error: Could not find commentSection VBox");
            return;
        }
        if (commentText == null) {
            System.out.println("Erreur: Le TextArea pour le commentaire est introuvable.");
            return;
        }

        String originalCommentText = commentText.getText().trim();

        if (originalCommentText.isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide.", Alert.AlertType.ERROR);
            return;
        }

        if (originalCommentText.length() > 5000) {
            showAlert("Erreur", "Le commentaire ne peut pas dépasser 5000 caractères.", Alert.AlertType.ERROR);
            return;
        }

        if (!validateContent(originalCommentText)) {
            return;
        }

        String approvedComment = getApprovedComment(originalCommentText);
        if (approvedComment == null) {
            commentText.clear();
            return;
        }

        Commentaire commentaire = new Commentaire();
        commentaire.setIdEB(post.getIdEB());
        commentaire.setId_U(session.getId_U());
        commentaire.setDescription(approvedComment);
        commentaire.setProposedDescription(originalCommentText);
        commentaire.setNumberLike(0);
        commentaire.setNumberDislike(0);
        commentaire.setApproved(true);

        CommentaireServices.add(commentaire);

        refreshCommentList(commentSection,post);
        commentText.clear();
    }

    public void refreshCommentList(VBox commentSection, SocialMedia post) {
        commentSection.getChildren().clear();

        TextArea commentTextLocal = new TextArea();
        commentTextLocal.setPromptText("Écrire un commentaire...");
        commentTextLocal.setPrefHeight(50);
        commentTextLocal.setId("commentText");

        Button submitCommentButton = new Button("📩");
        submitCommentButton.getStyleClass().add("submit-comment-button");
        submitCommentButton.setOnAction(e -> handleAddCommentWrapper(e, post, commentSection, commentTextLocal));
        submitCommentButton.setUserData(post);

        commentSection.getChildren().addAll(commentTextLocal, submitCommentButton);

        List<Commentaire> commentaires = CommentaireServices.getAllWithPostDetails(post.getIdEB());

        for (Commentaire commentaire : commentaires) {
            Users user = usersService.getById(commentaire.getId_U());
            if (commentaire.getIdEB() == post.getIdEB() && commentaire.isApproved()) {
                HBox commentBox = createCommentWithProfilePicture(commentaire, user);
                commentSection.getChildren().add(commentBox);
            }
        }
    }

    private void handleAddCommentWrapper(ActionEvent e, SocialMedia post, VBox commentSection, TextArea commentTextLocal) {
        Button button = (Button) e.getSource();
        button.setUserData(post);
        handleAddComment(e);
    }
    private VBox getCommentList(VBox commentSection) {
        for (javafx.scene.Node node : commentSection.getChildren()) {
            if (node instanceof VBox && "commentList".equals(node.getId())) {
                return (VBox) node;
            }
        }
        return null;
    }


    private String showApprovalDialog(String original, String proposed) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentApproval.fxml"));
            Parent root = loader.load();

            CommentApprovalController controller = loader.getController();
            controller.setCommentData(original, proposed);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Comment Approval");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isApproved()) {
                return controller.getFinalComment();
            }
            else
                return null;
        } catch (IOException e) {
            System.err.println("Error loading CommentApproval.fxml: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private HBox createCommentWithProfilePicture(Commentaire comment, Users user) {
        System.out.println("createCommentWithProfilePicture: comment=" + comment + ", getId_U()=" + comment.getId_U() + ", session.getId_U()=" + session.getId_U());
        ImageView userImageView = new ImageView();
        userImageView.setFitWidth(20);
        userImageView.setFitHeight(20);
        userImageView.setPreserveRatio(true);

        String defaultImagePath = "/image/imagepardefaut.jpg";
        Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
        if (user != null && user.getImagesU() != null && !user.getImagesU().isEmpty()) {
            try {
                Image profileImage = new Image("file:/C:/xampp/htdocs/ImageSocialMedia/" + user.getImagesU());
                userImageView.setImage(profileImage);
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
                userImageView.setImage(defaultImage);
            }
        } else {
            userImageView.setImage(defaultImage);
        }

        VBox commentContent = new VBox();
        Label usernameLabel = new Label(user != null ? user.getName() + ":" : "Unknown User:");
        usernameLabel.setStyle("-fx-font-weight: bold;");
        Label commentLabel = new Label(comment.getDescription());

        commentContent.getChildren().addAll(usernameLabel, commentLabel);

        HBox commentBox = new HBox(5);
        commentBox.setAlignment(Pos.CENTER_LEFT);
        commentBox.getChildren().addAll(userImageView, commentContent);

        Button updateButton = new Button("✏");
        Button deleteButton = new Button("🗑");

        updateButton.setOnAction(event -> handleUpdateComment(event));
        deleteButton.setOnAction(event -> handleDeleteComment(event));
        System.out.println("Setting userdata for updateButton and deleteButton with comment: " + comment);
        updateButton.setUserData(comment);
        deleteButton.setUserData(comment);

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(updateButton, deleteButton);

        if (comment.getId_U() == session.getId_U()) {
            commentBox.getChildren().add(buttonBox);
        }

        return commentBox;
    }

    @FXML
    private void handleUpdateComment(ActionEvent event) {
        Button button = (Button) event.getSource();
        Commentaire commentaire = (Commentaire) button.getUserData();

        if (commentaire == null) {
            System.out.println("Erreur : Impossible de récupérer le commentaire.");
            return;
        }

        if (commentaire.getId_U() != session.getId_U()) {
            showAlert("Permission Denied", "You cannot edit this comment.", Alert.AlertType.ERROR);
            return;
        }

        HBox buttonBox = (HBox) button.getParent();
        HBox commentBox = (HBox) buttonBox.getParent();
        VBox commentContent = (VBox) commentBox.getChildren().get(1);
        Label commentLabel = (Label) commentContent.getChildren().get(1);

        String oldCommentText = commentLabel.getText();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/update-comment-dialog.fxml"));
            Parent root = loader.load();

            UpdateCommentDialogController controller = loader.getController();
            controller.setCommentText(oldCommentText);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier le commentaire");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.showAndWait();

            if (controller.isUpdated()) {
                String newTextTrimmed = controller.getNewCommentText().trim();

                if (newTextTrimmed.isEmpty()) {
                    showAlert("Erreur", "Le commentaire ne peut pas être vide.", Alert.AlertType.ERROR);
                    return;
                }

                if (newTextTrimmed.length() > 5000) {
                    showAlert("Erreur", "Le commentaire ne peut pas dépasser 5000 caractères.", Alert.AlertType.ERROR);
                    return;
                }

                if (!validateContent(newTextTrimmed)) {
                    return;
                }

                String approvedComment = getApprovedComment(newTextTrimmed);
                if (approvedComment == null) {
                    return;
                }

                commentaire.setDescription(approvedComment);
                CommentaireServices.update(commentaire);
                commentLabel.setText(approvedComment);
            }
        } catch (Exception e) {
            System.err.println("Error loading update comment dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load comment update dialog.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleDeleteComment(ActionEvent event) {
        Button button = (Button) event.getSource();
        System.out.println("handleDeleteComment: button = " + button);
        Commentaire commentaire = (Commentaire) button.getUserData();
        System.out.println("handleDeleteComment: comment = " + commentaire);

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
            HBox buttonBox = (HBox) button.getParent();
            HBox commentBox = (HBox) buttonBox.getParent();
            VBox commentSection = (VBox) commentBox.getParent();

            commentSection.getChildren().remove(commentBox);
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