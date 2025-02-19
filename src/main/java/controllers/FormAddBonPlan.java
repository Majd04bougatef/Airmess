package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import models.bonplan;
import services.BonPlanServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FormAddBonPlan {

    @FXML
    private TextField DescriptionBP;

    @FXML
    private TextField LocalistionBP;

    @FXML
    private Button bttnBP;

    @FXML
    private Pane form;

    @FXML
    private ImageView imgPB;

    @FXML
    private TextField name;

    @FXML
    private Text text1;

    @FXML
    private SplitMenuButton type;

    @FXML
    private MenuItem resto, coworkingspace, cafe, musee;

    private String imageName;
    private int id_U = 1;

    private final BonPlanServices bonPlanServices = new BonPlanServices() {};



    @FXML
    public void initialize() {
        resto.setOnAction(this::handleTypeSelection);
        coworkingspace.setOnAction(this::handleTypeSelection);
        cafe.setOnAction(this::handleTypeSelection);
        musee.setOnAction(this::handleTypeSelection);
    }

    private void handleTypeSelection(ActionEvent event) {
        MenuItem selectedItem = (MenuItem) event.getSource();
        String selectedType = selectedItem.getText();
        type.setText(selectedType); // Mettre à jour le texte du SplitMenuButton
        System.out.println("Type sélectionné: " + selectedType); // Afficher le type sélectionné dans la console

    }

    @FXML
    void addImageBP(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imageName = System.currentTimeMillis() + "_" + file.getName();
            String destinationPath = "C:/xampp/htdocs/ImageBonPlan/" + imageName;
            File destinationFile = new File(destinationPath);
            try {
                Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Image img = new Image(destinationFile.toURI().toString());
                imgPB.setImage(img);
                System.out.println("Image enregistrée avec succès : " + imageName);
            } catch (IOException e) {
                showAlertBP("Erreur", "Impossible d'enregistrer l'image.", AlertType.ERROR);
                System.out.println("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    void ajouterBP(ActionEvent event) {
        bonplan bonPlan = new bonplan();

        // Vérifier si le nom, la description et la localisation sont remplis
        if (name.getText().isEmpty() || DescriptionBP.getText().isEmpty() || LocalistionBP.getText().isEmpty()) {
            showAlertBP("Erreur", "Veuillez remplir tous les champs !", AlertType.ERROR);
            return;
        }

        // Vérifier la valeur du type de lieu
        String selectedType = type.getText();
        if (selectedType.equals("Choisir un type")) {
            showAlertBP("Erreur", "Veuillez sélectionner un type de lieu !", AlertType.ERROR);
            return;
        }

        bonPlan.setNomplace(name.getText());
        bonPlan.setDescription(DescriptionBP.getText());
        bonPlan.setLocalisation(LocalistionBP.getText());
        bonPlan.setTypePlace(selectedType);
        bonPlan.setId_U(1);

        if (imageName != null) {
            bonPlan.setImageBP(imageName);
        } else {
            bonPlan.setImageBP(null);
        }

        bonPlanServices.add(bonPlan);

        showAlertBP("Succès", "Bon plan ajouté avec succès !", AlertType.INFORMATION);
        clearFieldsBP();
    }

    private void showAlertBP(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFieldsBP() {
        name.clear();
        DescriptionBP.clear();
        LocalistionBP.clear();
        imgPB.setImage(null);
        text1.setText("Bon plan ajouté avec succès!");
    }
}
