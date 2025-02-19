package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import services.ReservationService;

public class ReservationDialogController {

    public RadioButton cashRadioButton;
    public RadioButton cardRadioButton;
    @FXML
    private DatePicker datePicker;

    private Stage dialogStage;
    private boolean confirmed = false;

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
        if (datePicker.getValue() != null && cardRadioButton.getText() != null) {
            confirmed = true;
            ReservationService reservationService = new ReservationService();
            dialogStage.close();
        } else {
            System.out.println("Please select a date and payment method.");
        }
    }

    @FXML
    private void handleCancelButtonAction() {
        dialogStage.close();
    }
}