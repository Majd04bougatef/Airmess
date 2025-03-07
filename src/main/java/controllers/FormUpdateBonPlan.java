package controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.bonplan;
import netscape.javascript.JSObject;
import services.BonPlanServices;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
    @FXML
    private WebView mapView; // Ajout de la carte

    private AnchorPane centralAnocherPane;
    public void setCentralAnchorPane(AnchorPane centralAnocherPane) {
        this.centralAnocherPane = centralAnocherPane;
    }

    private final BonPlanServices bonPlanServices = new BonPlanServices(){};
    private bonplan selectedBonPlan;
    private File selectedImageFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        if (bp.getImageBP() != null && !bp.getImageBP().isEmpty()) {
            File file = new File("C:/xampp/htdocs/ImageBonPlan/" + bp.getImageBP());
            if (file.exists()) {
                imgPB.setImage(new Image(file.toURI().toString()));
            }
        }

        // Chargement de la carte dans le WebView
        WebEngine webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/html/mapBonPlan.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javafx", this);
            }
        });
    }

    // Méthode appelée par JavaScript pour récupérer les nouvelles coordonnées
    public void sendCoordinates(String lat, String lng) {
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lng);

        String locationName = GeocodingService.getLocationName(latitude, longitude);
        if (locationName != null) {
            LocalistionBP.setText(locationName);
        } else {
            LocalistionBP.setText("Localisation inconnue");
        }
    }

    @FXML
    private void updateBonPlan() {
        if (selectedBonPlan != null) {
            selectedBonPlan.setNomplace(name.getText());
            selectedBonPlan.setDescription(DescriptionBP.getText());
            selectedBonPlan.setLocalisation(LocalistionBP.getText());
            selectedBonPlan.setTypePlace(type.getText());

            if (selectedImageFile != null) {
                selectedBonPlan.setImageBP(selectedImageFile.getName());
            }

            bonPlanServices.update(selectedBonPlan);

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

    public static class GeocodingService {
        public static String getLocationName(double latitude, double longitude) {
            try {
                String url = "https://nominatim.openstreetmap.org/reverse?lat=" + latitude + "&lon=" + longitude + "&format=json&lang=fr";
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("display_name");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}