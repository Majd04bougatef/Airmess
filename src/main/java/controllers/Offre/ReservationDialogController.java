package controllers.Offre;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import services.ReservationService;

import java.time.LocalDate;
import java.util.Date;

public class ReservationDialogController {
    @FXML
    private DatePicker datePicker;

    private Stage dialogStage;
    private boolean confirmed = false;
    LocalDate date;

    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getSelectedDate() {
        return datePicker.getValue() != null ? datePicker.getValue().toString() : null;
    }

    @FXML
    private void handleConfirmButtonAction() {
        if (datePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Date not selected");
            alert.setContentText("Please select a date.");
            alert.showAndWait();
            return;
        }
        if (datePicker.getValue().isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Date");
            alert.setContentText("Please select a future date.");
            alert.showAndWait();
            return;
        }
        if (datePicker.getValue().isAfter(date)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Date");
            alert.setContentText("Please select a date before the end date.");
            alert.showAndWait();
            return;
        }

        confirmed = true;
        ReservationService reservationService = new ReservationService();
        dialogStage.close();
    }

    @FXML
    private void handleCancelButtonAction() {
        dialogStage.close();
    }

    public String getPaimentMethod() {
        return "Reserve with Card";
    }
}