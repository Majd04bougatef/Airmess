package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import models.bonplan;
import services.BonPlanServices;

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
        for (bonplan bp : bonPlansList) {
            VBox card = createBonPlanCard(bp);
            flowPaneBonPlans.getChildren().add(card);
        }

        // Ajuster dynamiquement la hauteur du FlowPane en fonction du nombre de cartes
        flowPaneBonPlans.setPrefHeight(bonPlansList.size() * 150); // Ajustez 150 selon la taille de vos cartes
    }

    private VBox createBonPlanCard(bonplan bp) {
        // Création de l'image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(100);
        if (bp.getImageBP() != null) {
            File file = new File("C:/xampp/htdocs/ImageBonPlan/" + bp.getImageBP());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        // Création des labels avec mise en gras
        Label nomLabel = new Label("Nom: " + bp.getNomplace());
        nomLabel.getStyleClass().add("label-bold");

        Label descLabel = new Label("Description: " + bp.getDescription());
        descLabel.getStyleClass().add("label-bold");

        Label locLabel = new Label("Localisation: " + bp.getLocalisation());
        locLabel.getStyleClass().add("label-bold");

        Label typeLabel = new Label("Type: " + bp.getTypePlace());
        typeLabel.getStyleClass().add("label-bold");

        // Mise en forme de la carte
        VBox card = new VBox(5, imageView, nomLabel, descLabel, locLabel, typeLabel);
        card.getStyleClass().add("bonplan-card"); // Ajout de la classe CSS

        return card;
    }
}
