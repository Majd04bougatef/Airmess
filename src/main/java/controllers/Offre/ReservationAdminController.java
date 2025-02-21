package controllers.Offre;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import models.Reservation;
import services.ReservationService;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationAdminController implements Initializable {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public VBox deleteConfirmationBox;
    @FXML
    private ListView<Reservation> reservationsListView;

    private ReservationService reservationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reservationService = new ReservationService();
        loadReservations();

        // Set a custom cell factory to control how reservations are displayed
        reservationsListView.setCellFactory(new Callback<ListView<Reservation>, ListCell<Reservation>>() {
            @Override
            public ListCell<Reservation> call(ListView<Reservation> param) {
                return new ListCell<Reservation>() {
                    @Override
                    protected void updateItem(Reservation reservation, boolean empty) {
                        super.updateItem(reservation, empty);
                        if (empty || reservation == null) {
                            setText(null);
                        } else {
                            // Customize the display text
                            setText(formatReservationDisplay(reservation));
                        }
                    }
                };
            }
        });
    }

    private String formatReservationDisplay(Reservation reservation) {
        String formattedDate = LocalDateTime.parse(reservation.getDateRes(), DATE_FORMATTER).format(DATE_ONLY_FORMATTER);
        return String.format(
                "Reservation ID: %d\nDate: %s\nPayment Method: %s\nUser: %d",
                reservation.getIdO().getIdO(),
                formattedDate,
                reservation.getModePaiement(),
                reservation.getId_U()
        );
    }

    private void loadReservations() {
        List<Reservation> reservations = reservationService.getAll();
        ObservableList<Reservation> observableList = FXCollections.observableArrayList(reservations);
        reservationsListView.setItems(observableList);
    }

    public void handleDeleteButtonAction(ActionEvent actionEvent) {
        if (reservationsListView.getSelectionModel().getSelectedItem() != null) {
            deleteConfirmationBox.setVisible(true);

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Reservation Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a reservation to delete.");
            alert.showAndWait();
        }
    }

    public void confirmDelete(ActionEvent actionEvent) {
        Reservation selectedReservation = reservationsListView.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            reservationService.delete(selectedReservation);
            loadReservations();
            deleteConfirmationBox.setVisible(false);
        }
    }

    public void cancelDelete(ActionEvent actionEvent) {
        deleteConfirmationBox.setVisible(false);
    }

}