package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.station;
import services.StationService;


public class FormAddTransport {

    @FXML
    private TextField Capacite;

    @FXML
    private TextField Latitude;

    @FXML
    private TextField Longitude;

    @FXML
    private TextField NbVelo;

    @FXML
    private TextField Nom;

    @FXML
    private TextField PrixHeure;

    @FXML
    private ComboBox<String> TypeVelo;
    public void initialize() {
        if (TypeVelo != null) {
            TypeVelo.getItems().addAll("velo urbain", "velo de route", "velo electrique");
        }
    }

    @FXML
    private Pane form;

    @FXML
    private Text text1;

    @FXML
    private Text textCapacite;

    @FXML
    private Text textLatitude;

    @FXML
    private Text textLongitude;

    @FXML
    private Text textNbVelo;

    @FXML
    private Text textNom;

    @FXML
    private Text textPrixHeure;

    @FXML
    private Text textTypeVelo;


    public void btnAjoutTranport(ActionEvent actionEvent) {

        try {
            String nom = Nom.getText();
            double latitude = Double.parseDouble(Latitude.getText());
            double longitude = Double.parseDouble(Longitude.getText());
            int capacite = Integer.parseInt(Capacite.getText());
            int nbVelo = Integer.parseInt(NbVelo.getText());
            String typeVelo = TypeVelo.getValue();
            double prixHeure = Double.parseDouble(PrixHeure.getText());

            StationService st = new StationService() {};
            station newStation = new station(2, nom, latitude, longitude, prixHeure, capacite, nbVelo, typeVelo);

            st.add(newStation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText(" La station a été ajoutée avec succès !");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/css/alert-style.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");

            alert.showAndWait();

            Nom.clear();
            Latitude.clear();
            Longitude.clear();
            Capacite.clear();
            NbVelo.clear();
            PrixHeure.clear();
            TypeVelo.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText(" Veuillez entrer des valeurs valides pour les champs numériques.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/css/alert-style.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");

            alert.showAndWait();
        }
    }


}
