package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import models.reservation_transport;
import models.station;
import services.ReservationTransportService;
import services.StationService;

import java.util.List;
import java.util.stream.Collectors;

public class MesReservationTransportVoyageurs {

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ListView<String> reservationListView;

    private ReservationTransportService reservationService = new ReservationTransportService() {};

    private int userId = 2;

    @FXML
    public void initialize() {
        statusFilter.setItems(FXCollections.observableArrayList("En cours", "Terminée"));
        statusFilter.setValue("En cours");
        loadReservations("En cours");

        statusFilter.setOnAction(event -> filterReservations());
    }

    public void filterReservations() {
        String selectedStatus = statusFilter.getValue();
        loadReservations(selectedStatus);
    }

    private void loadReservations(String status) {
        List<reservation_transport> reservations = reservationService.getAll().stream().filter(r -> r.getIdU() == userId && r.getStatut().trim().equalsIgnoreCase(status.trim())).collect(Collectors.toList());

        ObservableList<String> reservationStrings = FXCollections.observableArrayList();
        for (reservation_transport r : reservations) {

            StationService st = new StationService() {};
            station s = new station();
            s= st.getById(r.getIdS());
            String reservationText = String.format(  "📍 %s | 📅 %s | 💰 %.2f€/h | 🏷 %s",s.getNom() , r.getDateRes(), s.getPrixheure(), r.getStatut());

            reservationStrings.add(reservationText);
        }

        System.out.println("Données envoyées à ListView : " + reservationStrings);

        Platform.runLater(() -> {
            reservationListView.setItems(reservationStrings);
            reservationListView.refresh();
        });
    }

}
