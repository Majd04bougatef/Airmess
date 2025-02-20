package controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.reservation_transport;
import services.ReservationTransportService;
import java.sql.Timestamp;
import java.time.Instant;

public class ModifierReservationVoyageurs {
    @FXML
    private Label nombreVeloLabel;
    @FXML
    private Label prixHeureLabel;
    @FXML
    private Label stationNameLabel;

    @FXML
    private Label dateResLabel;

    @FXML
    private Label dateFinLabel;

    @FXML
    private Label prixLabel;

    @FXML
    private Button updateButton;

    @FXML
    private Button cancelButton;

    private int reservationId;
    private ReservationTransportService reservationService = new ReservationTransportService() {};

    public void setReservationId(int id) {
        this.reservationId = id;
        loadReservationDetails();
    }

    private void loadReservationDetails() {
        reservation_transport reservation = reservationService.getById(reservationId);
        if (reservation != null) {
            stationNameLabel.setText(" " + new services.StationService(){}.getById(reservation.getIdS()).getNom());
            dateResLabel.setText(" " + reservation.getDateRes().toString());

            Timestamp dateFin = Timestamp.from(Instant.now());
            dateFinLabel.setText(" " + dateFin.toString());

            reservation.setDateFin(dateFin);
            nombreVeloLabel.setText(" "+reservation.getNombreVelo());
            reservation = reservationService.CalculPrix(reservation);
            prixLabel.setText("Prix total: " + String.format("%.2f €", reservation.getPrix()));
        }
    }

    @FXML
    private void handleUpdate() {
        reservation_transport reservation = reservationService.getById(reservationId);
        if (reservation != null) {
            reservation.setDateFin(Timestamp.from(Instant.now()));
            reservation = reservationService.CalculPrix(reservation);
            reservationService.update(reservation);

            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
