package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.bonplan;
import models.ReviewBonplan;
import services.BonPlanServices;
import services.ReviewBonPlanServices;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AfficherBonPlan {

    @FXML
    private FlowPane flowPaneBonPlans;

    private final BonPlanServices bonPlanServices = new BonPlanServices(){};
    private final ReviewBonPlanServices reviewService = new ReviewBonPlanServices(){};

    @FXML
    public void initialize() {
        loadBonPlans();
    }

    private void loadBonPlans() {
        List<bonplan> bonPlansList = bonPlanServices.getAll();
        flowPaneBonPlans.getChildren().clear();

        for (bonplan bp : bonPlansList) {
            VBox card = createBonPlanCard(bp);
            flowPaneBonPlans.getChildren().add(card);
        }

        flowPaneBonPlans.setPrefWrapLength(900);
    }

    private VBox createBonPlanCard(bonplan bp) {
        VBox card = new VBox(10);
        card.getStyleClass().add("bonplan-card");
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);

        // Image du Bon Plan
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        if (bp.getImageBP() != null) {
            File file = new File("C:/xampp/htdocs/ImageBonPlan/" + bp.getImageBP());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
        makeImageRounded(imageView);

        // Labels
        Label nomLabel = new Label("Nom: " + bp.getNomplace());
        Label descLabel = new Label("Description: " + bp.getDescription());
        Label locLabel = new Label("Localisation: " + bp.getLocalisation());
        Label typeLabel = new Label("Type: " + bp.getTypePlace());

        Separator separator = new Separator();

        // Menu Bouton pour Modifier/Supprimer
        MenuButton menuButton = new MenuButton("⋮");
        menuButton.getStyleClass().add("menu-button");
        MenuItem editItem = new MenuItem("Modifier");
        MenuItem deleteItem = new MenuItem("Supprimer");

        editItem.setOnAction(event -> openUpdateForm(bp));
        deleteItem.setOnAction(event -> deleteBonPlan(bp));

        menuButton.getItems().addAll(editItem, deleteItem);

        HBox menuBox = new HBox(menuButton);
        menuBox.setAlignment(Pos.TOP_RIGHT);

        // Commentaires
        VBox commentsSection = new VBox(5);
        commentsSection.getStyleClass().add("comments-section");
        loadComments(bp.getIdP(), commentsSection);

        TextArea commentField = new TextArea();
        commentField.setPromptText("Ajouter un commentaire...");
        commentField.setPrefRowCount(2);
        commentField.getStyleClass().add("comment-field");

        ChoiceBox<Integer> ratingBox = new ChoiceBox<>();
        ratingBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingBox.setValue(5);

        Button addCommentButton = new Button("Envoyer");
        addCommentButton.getStyleClass().add("add-comment-button");
        addCommentButton.setOnAction(event -> {
            String commentText = commentField.getText().trim();
            Integer rating = ratingBox.getValue();

            if (!commentText.isEmpty() && rating != null) {
                ReviewBonplan newReview = new ReviewBonplan(1, bp.getIdP(), rating, commentText, java.time.LocalDateTime.now());
                reviewService.add(newReview);
                loadComments(bp.getIdP(), commentsSection);
                commentField.clear();
            } else {
                showAlert("Erreur", "Veuillez entrer un commentaire avant d’envoyer.");
            }
        });

        HBox commentBox = new HBox(10, commentField, ratingBox, addCommentButton);
        commentBox.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(menuBox, imageView, nomLabel, descLabel, locLabel, typeLabel, separator, commentsSection, commentBox);
        return card;
    }

    // 🔹 Fonction mise à jour pour charger les commentaires
    private void loadComments(int bonPlanId, VBox commentsSection) {
        commentsSection.getChildren().clear();
        List<ReviewBonplan> comments = reviewService.getCommentsByBonPlan(bonPlanId);

        for (ReviewBonplan review : comments) {
            HBox commentContainer = new HBox(10);
            commentContainer.setAlignment(Pos.CENTER_LEFT);

            Label commentLabel = new Label("⭐ " + review.getRating() + " - " + review.getCommente());
            commentLabel.setWrapText(true);
            commentLabel.getStyleClass().add("comment-text");

            MenuButton menuButton = new MenuButton("⋮");
            menuButton.getStyleClass().add("menu-button");
            MenuItem editItem = new MenuItem("Modifier");
            MenuItem deleteItem = new MenuItem("Supprimer");

            editItem.setOnAction(event -> openEditCommentDialog(review, commentsSection));
            deleteItem.setOnAction(event -> deleteComment(review, commentsSection));

            menuButton.getItems().addAll(editItem, deleteItem);

            commentContainer.getChildren().addAll(commentLabel, menuButton);
            commentsSection.getChildren().add(commentContainer);
        }
    }

    private void deleteComment(ReviewBonplan review, VBox commentsSection) {
        reviewService.delete(review);
        loadComments(review.getIdP(), commentsSection);
    }

    private void openEditCommentDialog(ReviewBonplan review, VBox commentsSection) {
        TextField editField = new TextField(review.getCommente());
        Button saveButton = new Button("Enregistrer");

        saveButton.setOnAction(e -> {
            review.setCommente(editField.getText());
            reviewService.update(review);
            loadComments(review.getIdP(), commentsSection);
        });

        VBox editBox = new VBox(5, editField, saveButton);
        commentsSection.getChildren().add(editBox);
    }

    private void deleteBonPlan(bonplan bp) {
        bonPlanServices.delete(bp);
        refreshBonPlanList();
    }

    private void openUpdateForm(bonplan bp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormUpdateBonPlan.fxml"));
            Parent root = loader.load();

            FormUpdateBonPlan controller = loader.getController();
            controller.initData(bp);

            Stage stage = new Stage();
            stage.setTitle("Modifier Bon Plan");
            stage.setScene(new Scene(root));
            stage.setOnHidden(event -> refreshBonPlanList());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshBonPlanList() {
        flowPaneBonPlans.getChildren().clear();
        List<bonplan> bonPlansList = bonPlanServices.getAll();
        for (bonplan bp : bonPlansList) {
            flowPaneBonPlans.getChildren().add(createBonPlanCard(bp));
        }
    }

    private void makeImageRounded(ImageView imageView) {
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        imageView.setClip(clip);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void showAddBonPlan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddBonPlan.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Bon Plan");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}