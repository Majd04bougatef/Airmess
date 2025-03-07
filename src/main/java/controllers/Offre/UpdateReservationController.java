package controllers.Offre;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import models.Reservation;
import services.OffreService;
import services.ReservationService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UpdateReservationController implements Initializable {

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private DatePicker datePicker;


    @FXML
    private Label offerDescriptionLabel;

    private Reservation reservation;
    private ReservationService reservationService;
    private UserReservationsController parentController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reservationService = new ReservationService();

    }

    public void initData(Reservation reservation) {
        this.reservation = reservation;

        // Populate fields with reservation data
        offerDescriptionLabel.setText("Updating reservation for: " + reservation.getIdO().getDescription());

        // Fix date parsing - parse as LocalDateTime first, then extract date
        LocalDateTime dateTime = LocalDateTime.parse(reservation.getDateRes(), DATE_FORMATTER);
        datePicker.setValue(dateTime.toLocalDate());

        OffreService offreService = new OffreService();
        this.reservation.setIdO(offreService.getById(reservation.getIdO().getIdO()));

        LocalDateTime minDate = LocalDateTime.parse(this.reservation.getIdO().getStartDate(), DATE_FORMATTER);
        LocalDateTime maxDate = LocalDateTime.parse(this.reservation.getIdO().getEndDate(), DATE_FORMATTER);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(ChronoLocalDate.from(minDate)) || date.isAfter(ChronoLocalDate.from(maxDate)));
            }
        });
    }
    public void setParentController(UserReservationsController controller) {
        this.parentController = controller;
    }

    @FXML
    private void handleUpdateReservation() {
        try {
            // Update reservation data
            reservation.setDateRes(datePicker.getValue().format(DATE_FORMATTER));


            // Save changes
            reservationService.update(reservation);

            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Reservation Updated");
            alert.setContentText("Your reservation has been successfully updated.");
            alert.showAndWait();

            // Return to the reservations list
            goBack();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Update Failed");
            alert.setContentText("Could not update reservation: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/userReservations.fxml"));
            Parent root = loader.load();

            // If there's a parent controller, refresh its data
            if (parentController != null) {
                parentController.refreshData();
            }

            rootAnchorPane.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}