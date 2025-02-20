package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.web.WebView;
import models.station;
import netscape.javascript.JSObject;
import services.StationService;

import javafx.scene.web.WebEngine;

import java.net.URL;public class FormAddTransport {

    @FXML
    private TextField Capacite;

    @FXML
    private TextField NbVelo;

    @FXML
    private TextField Nom;

    @FXML
    private TextField PrixHeure;

    @FXML
    private ComboBox<String> TypeVelo;

    @FXML
    private TextField Latitude;

    @FXML
    private TextField Longitude;

    @FXML
    private WebView mapView;

    private double lat = 51.1;
    private double lng = -0.3;

    public void initialize() {
        if (TypeVelo != null) {
            TypeVelo.getItems().addAll("Vélo urbain", "Vélo de route", "Vélo électrique");
        }

        WebEngine webEngine = mapView.getEngine();
        URL url = getClass().getResource("/html/map.html");
        if (url != null) {
            webEngine.load(url.toExternalForm());

            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaConnector", new JavaConnector());
                }
            });
        }
    }

    public class JavaConnector {
        public void receiveCoordinates(double latitude, double longitude) {
            Platform.runLater(() -> {
                Latitude.setText(String.valueOf(latitude));
                Longitude.setText(String.valueOf(longitude));
            });
        }
    }


    public void btnAjoutTranport(ActionEvent actionEvent) {
        try {
            String nom = Nom.getText();
            int capacite = Integer.parseInt(Capacite.getText());
            int nbVelo = Integer.parseInt(NbVelo.getText());
            String typeVelo = TypeVelo.getValue();
            double prixHeure = Double.parseDouble(PrixHeure.getText());

            lat = Double.parseDouble(Latitude.getText());
            lng = Double.parseDouble(Longitude.getText());

            if (lat == 0 || lng == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner un emplacement sur la carte.");
                alert.showAndWait();
                return;
            }

            System.out.println("test") ;
            station newStation = new station(2, nom, lat, lng, prixHeure,  nbVelo,capacite, typeVelo);

            StationService stService = new StationService(){};
            stService.add(newStation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("La station a été ajoutée avec succès !");
            alert.showAndWait();
            System.out.println("test2") ;
            Nom.clear();
            Capacite.clear();
            NbVelo.clear();
            PrixHeure.clear();
            TypeVelo.getSelectionModel().clearSelection();
            Latitude.clear();
            Longitude.clear();
            lat = 0;
            lng = 0;
            System.out.println("test 3") ;

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer des valeurs valides pour les champs numériques.");
            alert.showAndWait();
        }
    }
}
