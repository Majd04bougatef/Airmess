package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.bonplan;
import services.BonPlanServices;

import java.io.File;
import java.io.IOException;
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
        // Créer la carte
        VBox card = new VBox();
        card.getStyleClass().add("bonplan-card");

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);

        if (bp.getImageBP() != null) {
            File file = new File("C:/xampp/htdocs/ImageBonPlan/" + bp.getImageBP());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        // Labels
        Label nomLabel = new Label("Nom: " + bp.getNomplace());
        nomLabel.getStyleClass().add("label-bold");

        Label descLabel = new Label("Description: " + bp.getDescription());
        descLabel.getStyleClass().add("label-bold");

        Label locLabel = new Label("Localisation: " + bp.getLocalisation());
        locLabel.getStyleClass().add("label-bold");

        Label typeLabel = new Label("Type: " + bp.getTypePlace());
        typeLabel.getStyleClass().add("label-bold");

        // Boutons
        Button updateButton = new Button("Modifier");
        updateButton.getStyleClass().add("update-button");
        updateButton.setOnAction(event -> openUpdateForm(bp)); // Ouvre la page de modification

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> deleteBonPlan(bp, card));

        // HBox pour les boutons
        HBox buttonBox = new HBox(10, updateButton, deleteButton);
        buttonBox.getStyleClass().add("hbox-buttons");

        // Ajouter les éléments à la carte
        card.getChildren().addAll(imageView, nomLabel, descLabel, locLabel, typeLabel, buttonBox);

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
}
