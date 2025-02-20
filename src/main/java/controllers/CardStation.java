package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.reservation_transport;
import models.station;
import services.ReservationTransportService;
import test.Session;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

public class CardStation {

    private Session session = Session.getInstance();

    @FXML
    private Text idStation;


    @FXML
    private ImageView image;

    @FXML
    private Text nbvelo;

    @FXML
    private Text nom;

    @FXML
    private VBox vboxx;

    private station currentStation;
    private DisplayStationVoyageurs parentController;

    public void setParentController(DisplayStationVoyageurs parentController) {
        this.parentController = parentController;
    }

    @FXML
    void reserver(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Réservation de vélo");
        alert.setHeaderText("Station: " + currentStation.getNom());
        alert.setContentText("Nombre de vélos disponibles: " + currentStation.getNbVelo());

        TextField inputField = new TextField();
        inputField.setPromptText("Entrez le nombre de vélos à réserver");

        VBox vbox = new VBox();
        vbox.getChildren().addAll(new Text("Indiquez le nombre de vélos à réserver :"), inputField);
        alert.getDialogPane().setContent(vbox);

        ButtonType buttonReserver = new ButtonType("Réserver");
        ButtonType buttonAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonReserver, buttonAnnuler);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonReserver) {
                try {
                    int nbVeloRes = Integer.parseInt(inputField.getText());

                    if (nbVeloRes <= 0) {
                        showErrorAlert("Veuillez entrer un nombre valide (> 0).");
                    } else if (nbVeloRes > currentStation.getNbVelo()) {
                        showErrorAlert("Le nombre de vélos demandés dépasse le nombre disponible !");
                    } else {
                        ajouterReservation(nbVeloRes);
                    }
                } catch (NumberFormatException e) {
                    showErrorAlert("Veuillez entrer un nombre valide.");
                }
            }
        });
    }

    private void showErrorAlert(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur de réservation");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.show();
    }

    private void ajouterReservation(int nbVeloRes) {



        ReservationTransportService service = new ReservationTransportService() {};

        reservation_transport reservation = new reservation_transport();
        reservation.setIdS(currentStation.getIdS());
        session.checkLogin();
        reservation.setIdU(session.getId_U());
        reservation.setDateRes(Timestamp.from(Instant.now()));
        reservation.setDateFin(null);
        reservation.setPrix(0.0);
        reservation.setStatut("en cours");
        reservation.setNombreVelo(nbVeloRes);

        service.add(reservation);

        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle("Réservation confirmée");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Votre réservation de " + nbVeloRes + " vélo(s) a été enregistrée !");
        confirmation.show();
    }


    @FXML
    void review(ActionEvent event) {
        System.out.println("Revue de la station: " + currentStation.getNom());
    }

    public void setData(station st) {
        this.currentStation = st;
        nom.setText(st.getNom());
        nbvelo.setText("Vélos disponibles: " + st.getNbVelo());

        idStation.setText(String.valueOf(st.getIdS()));

        String imagePath = switch (st.getTypeVelo()) {
            case "velo de route" -> "/image/locationveloVoyageurs/velo de route.jpeg";
            case "velo urbain" -> "/image/locationveloVoyageurs/velo urbain.jpeg";
            case "velo electrique" -> "/image/locationveloVoyageurs/velo elect.jpeg";
            default -> "/image/default.png";
        };

        image.setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }


}
