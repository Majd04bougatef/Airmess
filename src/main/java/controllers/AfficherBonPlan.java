package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import services.BonPlanServices;
import models.ReviewBonplan;
import services.ReviewBonPlanServices;
import javafx.geometry.Pos;
import java.io.IOException;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.List;

public class AfficherBonPlan {

    @FXML
    private FlowPane flowPaneBonPlans;

    private final BonPlanServices bonPlanServices = new BonPlanServices(){};

    @FXML
    public void initialize() {
        loadBonPlans();
    }

    private void loadBonPlans() {
        List<bonplan> bonPlansList = bonPlanServices.getAll();
        flowPaneBonPlans.getChildren().clear(); // Vider la liste avant de la recharger

        for (bonplan bp : bonPlansList) {
            VBox card = createBonPlanCard(bp);
            flowPaneBonPlans.getChildren().add(card);
        }

        flowPaneBonPlans.setPrefWrapLength(900); // Ajuster la largeur pour aligner les cartes
    }

    private VBox createBonPlanCard(bonplan bp) {
        VBox card = new VBox();
        card.getStyleClass().add("bonplan-card");

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

        // Labels pour les détails du bon plan
        Label nomLabel = new Label("Nom: " + bp.getNomplace());
        Label descLabel = new Label("Description: " + bp.getDescription());
        Label locLabel = new Label("Localisation: " + bp.getLocalisation());
        Label typeLabel = new Label("Type: " + bp.getTypePlace());

        // Boutons Modifier et Supprimer
        Button updateButton = new Button("Modifier");
        updateButton.getStyleClass().add("update-button");
        updateButton.setOnAction(event -> openUpdateForm(bp));

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> deleteBonPlan(bp, card));

        HBox buttonBox = new HBox(10, updateButton, deleteButton);
        buttonBox.getStyleClass().add("hbox-buttons");

        // Séparateur visuel avant les commentaires
        Separator separator = new Separator();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #dddddd; -fx-padding: 5px;");

        // Section Commentaires
        VBox commentsSection = new VBox();
        commentsSection.getStyleClass().add("comments-section");
        loadComments(bp.getIdP(), commentsSection);

        // Ajout de commentaire
        TextArea commentField = new TextArea();
        commentField.setPromptText("Ajouter un commentaire...");
        commentField.setPrefRowCount(2);
        commentField.getStyleClass().add("comment-field");

        // Sélection de la note
        ChoiceBox<Integer> ratingBox = new ChoiceBox<>();
        ratingBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingBox.setValue(5); // Valeur par défaut

        Button addCommentButton = new Button("Envoyer");
        addCommentButton.getStyleClass().add("add-comment-button");
        addCommentButton.setOnAction(event -> {
            String commentText = commentField.getText().trim();
            Integer rating = ratingBox.getValue();

            if (!commentText.isEmpty() && rating != null) {
                ReviewBonplan newReview = new ReviewBonplan(1, bp.getIdP(), rating, commentText, java.time.LocalDateTime.now());
                ReviewBonPlanServices reviewService = new ReviewBonPlanServices(){};
                reviewService.add(newReview);
                loadComments(bp.getIdP(), commentsSection);
                commentField.clear();
            }
        });

        HBox commentBox = new HBox(10);
        commentBox.getChildren().addAll(commentField, ratingBox, addCommentButton);
        commentBox.setAlignment(Pos.CENTER_LEFT);

        // Ajout des éléments dans la carte
        card.getChildren().addAll(imageView, nomLabel, descLabel, locLabel, typeLabel, buttonBox, separator, commentsSection, commentBox);
        return card;
    }


    private void openUpdateForm(bonplan bp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormUpdateBonPlan.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur et passer les données du bon plan sélectionné
            FormUpdateBonPlan controller = loader.getController();
            controller.initData(bp);

            Stage stage = new Stage();
            stage.setTitle("Modifier Bon Plan");
            stage.setScene(new Scene(root));

            // Fermer la fenêtre et rafraîchir les cartes après mise à jour
            stage.setOnHidden(event -> refreshBonPlanList());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteBonPlan(bonplan bp, VBox card) {
        // Confirmer la suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce bon plan ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            // Supprimer de la base de données
            bonPlanServices.delete(bp);

            // Supprimer la carte de l'interface
            flowPaneBonPlans.getChildren().remove(card);

            // Afficher un message de confirmation
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Suppression réussie");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Le bon plan a été supprimé avec succès !");
            successAlert.show();
        });
    }
    public void refreshBonPlanList() {
        flowPaneBonPlans.getChildren().clear(); // Supprime les anciennes cartes

        List<bonplan> bonPlansList = bonPlanServices.getAll(); // Récupère les nouvelles données
        for (bonplan bp : bonPlansList) {
            flowPaneBonPlans.getChildren().add(createBonPlanCard(bp)); // Recharge les cartes
        }
    }
    private void loadComments(int idP, VBox commentsSection) {
        commentsSection.getChildren().clear(); // Nettoyer avant de recharger

        ReviewBonPlanServices reviewService = new ReviewBonPlanServices() {};
        List<ReviewBonplan> comments = reviewService.getCommentsByBonPlan(idP);

        for (ReviewBonplan review : comments) {
            HBox commentBox = new HBox(10);
            commentBox.setAlignment(Pos.CENTER_LEFT);

            Label commentLabel = new Label("⭐ " + review.getRating() + " - " + review.getCommente());
            commentLabel.setWrapText(true);

            Button editButton = new Button("Modifier");
            Button deleteButton = new Button("Supprimer");

            // Action de suppression
            deleteButton.setOnAction(event -> {
                reviewService.delete(review);
                commentsSection.getChildren().remove(commentBox);
            });

            // Action de modification
            editButton.setOnAction(event -> {
                TextField editField = new TextField(review.getCommente());
                Button saveButton = new Button("Enregistrer");

                saveButton.setOnAction(e -> {
                    review.setCommente(editField.getText());
                    reviewService.update(review);
                    commentLabel.setText("⭐ " + review.getRating() + " - " + review.getCommente());

                    commentBox.getChildren().setAll(commentLabel, editButton, deleteButton);
                });

                commentBox.getChildren().setAll(editField, saveButton);
            });

            commentBox.getChildren().addAll(commentLabel, editButton, deleteButton);
            commentsSection.getChildren().add(commentBox);
        }
    }
    @FXML
    void showAddBonPlan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddBonPlan.fxml"));
            Parent addBonPlanPage = loader.load();

            // Remplacer l'affichage dans la scène actuelle
            ((Node) event.getSource()).getScene().setRoot(addBonPlanPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
