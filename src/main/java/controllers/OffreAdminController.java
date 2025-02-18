package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Offre;
import services.OffreService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OffreAdminController implements Initializable {

    public VBox deleteConfirmationBox;
    public TextField confirmationTextField;
    public DatePicker updateEndDateField;
    public DatePicker updateStartDateField;
    public TextField updateDiscountField;
    public TextField updatePriceField;
    public TextField updateLocationField;
    public TextField updateDescriptionField;
    public VBox updateFieldsBox;
    @FXML
    private ListView<Offre> offreListView;

    private OffreService offreService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        offreService = new OffreService();
        loadOffres();

        // Set a custom cell factory to control how Offre objects are displayed
        offreListView.setCellFactory(new Callback<ListView<Offre>, ListCell<Offre>>() {
            @Override
            public ListCell<Offre> call(ListView<Offre> param) {
                return new ListCell<Offre>() {
                    @Override
                    protected void updateItem(Offre offre, boolean empty) {
                        super.updateItem(offre, empty);
                        if (empty || offre == null) {
                            setText(null);
                        } else {
                            // Customize the display text
                            setText(formatOffreDisplay(offre));
                        }
                    }
                };
            }
        });
    }

    private String formatOffreDisplay(Offre offre) {
        return String.format(
                "Description: %s\nPrice: %.2f -> %.2f\nDates: %s to %s\nPlace: %s\nLimit: %d",
                offre.getDescription(),
                offre.getPriceInit(),
                offre.getPriceAfter(),
                offre.getStartDate(),
                offre.getEndDate(),
                offre.getPlace(),
                offre.getNumberLimit()
        );
    }

    private void loadOffres() {
        List<Offre> offres = offreService.getAll();
        ObservableList<Offre> observableList = FXCollections.observableArrayList(offres);
        offreListView.setItems(observableList);
    }

    @FXML
    private void handleUpdateButtonAction() {
        Offre selectedOffre = offreListView.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            // Show the update fields box
            updateFieldsBox.setVisible(true);
            updateFieldsBox.setManaged(true);

            // Fill the fields with the selected offer's data
            updateDescriptionField.setText(selectedOffre.getDescription());
            updatePriceField.setText(String.valueOf(selectedOffre.getPriceInit()));
            updateDiscountField.setText(String.valueOf(selectedOffre.getPriceAfter()));
            updateStartDateField.setValue(LocalDate.parse(selectedOffre.getStartDate(), DATE_FORMATTER));
            updateEndDateField.setValue(LocalDate.parse(selectedOffre.getEndDate(), DATE_FORMATTER));
            updateLocationField.setText(selectedOffre.getPlace());
        } else {
            System.out.println("No offer selected.");
        }
    }

    @FXML
    private void handleDeleteButtonAction() {
        Offre selectedOffre = offreListView.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            // Show the delete confirmation bo;
            deleteConfirmationBox.setVisible(true);
            deleteConfirmationBox.setManaged(true);
        } else {
            System.out.println("No offer selected.");
        }
    }
    @FXML
    private void confirmDelete() {
        // Hide the delete confirmation box
        deleteConfirmationBox.setVisible(false);
        deleteConfirmationBox.setManaged(false);
        Offre selectedOffre = offreListView.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            offreService.delete(selectedOffre);
            loadOffres();
        } else {
            System.out.println("No offer selected.");
        }
    }
    @FXML
    private void cancelDelete() {
        // Hide the delete confirmation box
        deleteConfirmationBox.setVisible(false);
        deleteConfirmationBox.setManaged(false);
        confirmationTextField.clear();
    }

    @FXML
    private void handleAddButtonAction(ActionEvent actionEvent) {
        try {
            // Load the FXML file for the "Add Offer" dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutOffre.fxml"));
            Parent root = loader.load();

            // Get the controller for the "Add Offer" dialog
            AddOffreController addOfferController = loader.getController();

            // Create a new stage (dialog) for the "Add Offer" window
            Stage stage = new Stage();
            stage.setTitle("Add New Offer");
            stage.setScene(new Scene(root));

            // Set the current stage as the owner of the new stage (optional, for modality)
            stage.initOwner(offreListView.getScene().getWindow());

            // Show the dialog and wait for it to close
            stage.showAndWait();

            // Refresh the list of offers after the dialog is closed
            loadOffres();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void confirmUpdate(ActionEvent actionEvent) {
        Offre selectedOffre = offreListView.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            // Update the selected offer with the new data
            selectedOffre.setDescription(updateDescriptionField.getText());
            selectedOffre.setPriceInit(Double.parseDouble(updatePriceField.getText()));
            selectedOffre.setPriceAfter(Double.parseDouble(updateDiscountField.getText()));
            selectedOffre.setStartDate(updateStartDateField.getValue().toString());
            selectedOffre.setEndDate(updateEndDateField.getValue().toString());
            selectedOffre.setPlace(updateLocationField.getText());

            // Save the updated offer to the database
            if (updateEndDateField.getValue().isBefore(updateStartDateField.getValue())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Dates");
                alert.setHeaderText("End Date is before Start Date");
                alert.setContentText("Please select a valid date range.");
                alert.showAndWait();
                return;
            }
            offreService.update(selectedOffre);

            // Hide the update fields box
            updateFieldsBox.setVisible(false);
            updateFieldsBox.setManaged(false);

            // Refresh the list of offers
            loadOffres();
        } else {
            System.out.println("No offer selected.");
        }
    }

    public void cancelUpdate(ActionEvent actionEvent) {
    }
}