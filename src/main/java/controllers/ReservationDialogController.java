package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import services.ReservationService;

import java.time.LocalDate;

public class ReservationDialogController {

    @FXML
    private ToggleGroup paymentMethod;

    @FXML
    public RadioButton cashRadioButton;

    @FXML
    public RadioButton cardRadioButton;

    @FXML
    private DatePicker datePicker;

    private Stage dialogStage;
    private boolean confirmed = false;

    @FXML
    private void initialize() {
        paymentMethod = new ToggleGroup();
        cashRadioButton.setToggleGroup(paymentMethod);
        cardRadioButton.setToggleGroup(paymentMethod);
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
        if (!cardRadioButton.isSelected() && !cashRadioButton.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Payment method not selected");
            alert.setContentText("Please select a payment method.");
            alert.showAndWait();
            return;
        }
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

        confirmed = true;
        ReservationService reservationService = new ReservationService();
        dialogStage.close();
    }

    @FXML
    private void handleCancelButtonAction() {
        dialogStage.close();
    }

    public String getPaimentMethod() {
        if (cardRadioButton.isSelected()) {
            return "Reserve with Card";
        } else if (cashRadioButton.isSelected()) {
            return "Reserve with Cash";
        } else {
            return "Reserve with Cash";
        }
    }
}