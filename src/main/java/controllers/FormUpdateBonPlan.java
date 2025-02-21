package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.bonplan;
import services.BonPlanServices;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class FormUpdateBonPlan implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private TextField DescriptionBP;
    @FXML
    private TextField LocalistionBP;
    @FXML
    private SplitMenuButton type;
    @FXML
    private Button bttnBP;
    @FXML
    private ImageView imgPB;
    @FXML
    private Button addImageButton;

    private final BonPlanServices bonPlanServices = new BonPlanServices(){};
    private bonplan selectedBonPlan;
    private File selectedImageFile; // Pour stocker l'image choisie

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation des événements pour la sélection du type de lieu
        for (MenuItem item : type.getItems()) {
            item.setOnAction(event -> type.setText(item.getText()));
        }
    }

    public void initData(bonplan bp) {
        this.selectedBonPlan = bp;
        name.setText(bp.getNomplace());
        DescriptionBP.setText(bp.getDescription());
        LocalistionBP.setText(bp.getLocalisation());
        type.setText(bp.getTypePlace());

        // Charger l'image existante si elle est présente
        if (bp.getImageBP() != null && !bp.getImageBP().isEmpty()) {
            String imagePath = bp.getImageBP();
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File("C:/xampp/htdocs/ImageBonPlan/" + bp.getImageBP());
                if (file.exists()) {
                    imgPB.setImage(new Image(file.toURI().toString())); // ✅ Correction pour un chemin local
                } else {
                    System.out.println("Image introuvable : " + imagePath); // Debugging
                }
            }

        }
    }

    @FXML
    private void updateBonPlan() {
        if (selectedBonPlan != null) {
            selectedBonPlan.setNomplace(name.getText());
            selectedBonPlan.setDescription(DescriptionBP.getText());
            selectedBonPlan.setLocalisation(LocalistionBP.getText());
            selectedBonPlan.setTypePlace(type.getText());

            // Mise à jour du chemin de l'image si une nouvelle image a été sélectionnée
            if (selectedImageFile != null) {
                selectedBonPlan.setImageBP(selectedImageFile.getName()); // Stocker seulement le nom de fichier
            }

            bonPlanServices.update(selectedBonPlan);

            // Fermer la fenêtre après mise à jour
            Stage stage = (Stage) bttnBP.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Erreur", "Aucun bon plan sélectionné pour la mise à jour !");
        }
    }


    @FXML
    private void addImageBP() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            imgPB.setImage(new Image(selectedImageFile.toURI().toString()));
        } else {
            showAlert("Aucune image sélectionnée", "Veuillez sélectionner une image.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
