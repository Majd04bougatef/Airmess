package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.reservation_transport;
import models.station;
import services.ReservationService;
import services.ReservationTransportService;

import java.sql.Timestamp;
import java.time.LocalDate;

public class ReservationController {

    @FXML
    private Label stationNameLabel;

    @FXML
    private DatePicker dateResPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField prixField;

    @FXML
    private TextField statutField;

    private station currentStation;
    private ReservationTransportService reservationService = new ReservationTransportService(){};

    public void setStation(station st) {
        this.currentStation = st;
        stationNameLabel.setText("Réservation pour : " + st.getNom());
    }

    @FXML
    public void confirmReservation() {
        if (currentStation == null || dateResPicker.getValue() == null || dateFinPicker.getValue() == null || prixField.getText().isEmpty() || statutField.getText().isEmpty()) {
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        int userId = 1;
        int stationId = currentStation.getIdS();
        LocalDate dateRes = dateResPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        double prix = Double.parseDouble(prixField.getText());
        String statut = statutField.getText();

        reservation_transport reservation = new reservation_transport(userId, stationId, statut, Timestamp.valueOf(dateRes.atStartOfDay()), Timestamp.valueOf(dateFin.atStartOfDay()), prix);
        reservationService.add(reservation);

        System.out.println("Réservation confirmée pour la station : " + currentStation.getNom());
    }
}
