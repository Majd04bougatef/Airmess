package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
    @FXML
    private AnchorPane centralAnchorPane; // Assurez-vous que cette variable est bien déclarée


    public void setCentralAnchorPane(AnchorPane anchorPane) {
        this.centralAnchorPane = anchorPane;
    }

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

        // 🔹 Vérifier si l'utilisateur a déjà noté
        int userId = 1; // Remplace avec l'ID de l'utilisateur connecté
        boolean alreadyRated = hasUserAlreadyRated(userId, bp.getIdP());

        // 🔹 Champ de commentaire
        TextArea commentField = new TextArea();
        commentField.setPromptText("Ajouter un commentaire...");
        commentField.setPrefRowCount(2);
        commentField.getStyleClass().add("comment-field");

        // 🔹 Système de notation dynamique avec étoiles
        ChoiceBox<Integer> ratingBox = new ChoiceBox<>();
        HBox interactiveStars = createInteractiveStars(ratingBox);
        ratingBox.setDisable(alreadyRated); // Désactiver les étoiles après un premier vote

        // 🔹 Section des commentaires (DÉCLARATION AVANT UTILISATION)
        VBox commentsSection = new VBox(5);
        commentsSection.getStyleClass().add("comments-section");
        loadComments(bp.getIdP(), commentsSection); // Charger les commentaires ici

        // 🔹 Bouton d'ajout de commentaire (toujours actif)
        Button addCommentButton = new Button("Envoyer");
        addCommentButton.getStyleClass().add("add-comment-button");

        addCommentButton.setOnAction(event -> {
            String commentText = commentField.getText().trim();
            Integer rating = ratingBox.getValue();

            if (!commentText.isEmpty()) {
                if (!alreadyRated && rating != null) {
                    // Premier rating + commentaire
                    ReviewBonplan newReview = new ReviewBonplan(userId, bp.getIdP(), rating, commentText, java.time.LocalDateTime.now());
                    reviewService.add(newReview);
                    ratingBox.setDisable(true); // Désactiver les étoiles après le premier rating
                } else {
                    // Ajouter un commentaire sans rating
                    ReviewBonplan newComment = new ReviewBonplan(userId, bp.getIdP(), 0, commentText, java.time.LocalDateTime.now());
                    reviewService.add(newComment);
                }

                loadComments(bp.getIdP(), commentsSection);
                commentField.clear();
            } else {
                showAlert("Erreur", "Veuillez entrer un commentaire.");
            }
        });

        // 🔹 Afficher la moyenne des ratings avec des étoiles
        int averageRating = reviewService.getAverageRating(bp.getIdP());
        HBox starRating = createStarRating(averageRating);

        // 🔹 Organisation des éléments dans la carte
        HBox commentBox = new HBox(10, commentField, addCommentButton);
        commentBox.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(
                menuBox, imageView, nomLabel, descLabel, locLabel, typeLabel,
                interactiveStars, separator, commentsSection, commentBox
        );

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
            Parent addBonPlan = loader.load();

            // Effacer le contenu actuel et afficher le formulaire dans centralAnchorPane
            centralAnchorPane.getChildren().clear();
            centralAnchorPane.getChildren().add(addBonPlan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private HBox createStarRating(int rating) {
        HBox starBox = new HBox(2); // Espacement entre les étoiles
        starBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 1; i <= 5; i++) {
            Label star = new Label(i <= rating ? "★" : "☆"); // Remplit selon le rating
            star.setStyle("-fx-font-size: 18px; -fx-text-fill: gold;");
            starBox.getChildren().add(star);
        }

        return starBox;
    }
    private HBox createInteractiveStars(ChoiceBox<Integer> ratingBox) {
        HBox starBox = new HBox(5); // Espacement entre étoiles
        starBox.setAlignment(Pos.CENTER_LEFT);

        Label[] stars = new Label[5];

        for (int i = 0; i < 5; i++) {
            final int starValue = i + 1; // Note associée à l'étoile

            stars[i] = new Label("☆"); // Étoile vide au départ
            stars[i].setStyle("-fx-font-size: 22px; -fx-text-fill: gray; -fx-cursor: hand;");

            // Ajouter un événement de clic sur l'étoile
            stars[i].setOnMouseClicked(event -> {
                // Mettre à jour l'affichage des étoiles
                for (int j = 0; j < 5; j++) {
                    stars[j].setText(j < starValue ? "★" : "☆");
                    stars[j].setStyle(j < starValue ? "-fx-font-size: 22px; -fx-text-fill: gold;" : "-fx-font-size: 22px; -fx-text-fill: gray;");
                }
                ratingBox.setValue(starValue); // Mettre à jour la valeur sélectionnée
            });

            starBox.getChildren().add(stars[i]);
        }

        return starBox;
    }
    private boolean hasUserAlreadyRated(int userId, int bonPlanId) {
        List<ReviewBonplan> reviews = reviewService.getCommentsByBonPlan(bonPlanId);
        for (ReviewBonplan review : reviews) {
            if (review.getIdU() == userId) {
                return true; // L'utilisateur a déjà noté ce bon plan
            }
        }
        return false;
    }

}