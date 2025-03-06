package controllers.Offre;

import controllers.transport.FormAddTransport;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import models.Offre;
import netscape.javascript.JSObject;
import services.OffreService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class AddOffreController {

    public Label warningLabel;
    public WebView mapView;
    public AnchorPane rootAnchorPane;
    private JavaConnector javaConnector;

    @FXML
    private TextField description;

    @FXML
    private DatePicker endDate;

    @FXML
    private Pane form;

    @FXML
    private TextField nouveauPrix;

    @FXML
    private TextField numberLimit;

    @FXML
    private TextField place;

    @FXML
    private TextField prixInitial;

    @FXML
    private DatePicker startDate;
    private String imagePath;

    private double lat = 51.1;
    private double lng = -0.3;

    public void handleUploadButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(form.getScene().getWindow());
        if (file != null) {
            imagePath = file.getAbsolutePath();
            System.out.println(file.getAbsolutePath());
        }
    }

    public class JavaConnector {
        public void receiveCoordinates(double latitude, double longitude) {
            System.out.println("Coordonnées reçues : " + latitude + ", " + longitude);
            lat = latitude;
            lng = longitude;
            Platform.runLater(() -> {
                place.setText(latitude + ", " + longitude);

            });
        }
    }
    public void initialize() {
        mapView.getEngine().setJavaScriptEnabled(true);
        mapView.getEngine().setConfirmHandler(param -> true);


        WebEngine webEngine = mapView.getEngine();
        URL url = getClass().getResource("/html/map.html");
        if (url != null) {
            webEngine.load(url.toExternalForm());
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    javaConnector = new JavaConnector();
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaConnector", javaConnector);

                    // Débogage : vérifiez si le membre est défini
                    webEngine.executeScript("console.log('javaConnector défini ?', typeof window.javaConnector);");
                }
            });
        }
    }


    public void handleAjoutOffre() {
        if (!validateForm()) {
            return;
        }
        warningLabel.setVisible(false);
        OffreService offreService = new OffreService();
        Offre offre = new Offre();
        offre.setId_U(1);
        offre.setPriceInit(Double.parseDouble(prixInitial.getText()));
        offre.setPriceAfter(Double.parseDouble(nouveauPrix.getText()));
        offre.setStartDate(startDate.getValue().toString());
        offre.setEndDate(endDate.getValue().toString());
        offre.setNumberLimit(Integer.parseInt(numberLimit.getText()));
        offre.setDescription(description.getText());
        offre.setPlace(place.getText());
        offre.setImage(imagePath);
        offreService.add(offre);
        //close the form
        try {
            // Load the FXML file for the "Add Offer" dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/offreAdmin.fxml"));
            Parent root = loader.load();
            // Clear the current content and set the new content
            rootAnchorPane.getChildren().clear();
            rootAnchorPane.getChildren().add(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean validateForm() {
        if( prixInitial.getText().isEmpty()
                || nouveauPrix.getText().isEmpty()
                || startDate.getValue() == null
                || endDate.getValue() == null
                || numberLimit.getText().isEmpty()
                || description.getText().isEmpty()
                || place.getText().isEmpty()
                || imagePath == null) {
            warningLabel.setVisible(true);
            warningLabel.setText("Veuillez remplir tous les champs.");
            return false;
        }
        try {
            Double.parseDouble(prixInitial.getText());
            Double.parseDouble(nouveauPrix.getText());
            Integer.parseInt(numberLimit.getText());
        } catch (NumberFormatException e) {
            warningLabel.setVisible(true);
            warningLabel.setText("Les champs prix et nombre de places doivent être des nombres.");
            return false;
        }
        if (Double.parseDouble(prixInitial.getText()) < 0 || Double.parseDouble(nouveauPrix.getText()) < 0) {
            warningLabel.setVisible(true);
            warningLabel.setText("Les prix doivent être positifs.");
            return false;
        }
        if (Double.parseDouble(prixInitial.getText())<Double.parseDouble(nouveauPrix.getText())) {
            warningLabel.setVisible(true);
            warningLabel.setText("Les prix Initial doit etre supérieur.");
            return false;
        }
        if (startDate.getValue().isAfter(endDate.getValue())) {
            warningLabel.setVisible(true);
            warningLabel.setText("La date de début doit être avant la date de fin.");
            return false;
        }
        if (endDate.getValue().isBefore(LocalDate.now())) {
            warningLabel.setVisible(true);
            warningLabel.setText("La date de fin doit être après la date d'aujourd'hui.");
            return false;
        }
        if (Integer.parseInt(numberLimit.getText()) < 0) {
            warningLabel.setVisible(true);
            warningLabel.setText("Le nombre de places doit être positif.");
            return false;
        }
        return true;

    }
}
