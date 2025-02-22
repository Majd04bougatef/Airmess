package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import models.station;

public class ReservationTransport {

        @FXML
        private TextField nbVeloField;

        @FXML
        private TextField stationField;

        public void setReservationData(int nbVeloRes, station currentStation) {
            nbVeloField.setText(String.valueOf(nbVeloRes));
            stationField.setText(currentStation.getNom());
        }

}
