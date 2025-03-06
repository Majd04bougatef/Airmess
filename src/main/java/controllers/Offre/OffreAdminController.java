package controllers.Offre;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Offre;
import services.OffreService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OffreAdminController implements Initializable {
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public VBox deleteConfirmationBox;
    public TextField confirmationTextField;
    public DatePicker updateEndDateField;
    public DatePicker updateStartDateField;
    public TextField updateDiscountField;
    public TextField updatePriceField;
    public TextField updateLocationField;
    public TextField updateDescriptionField;
    public VBox updateFieldsBox;
    public TextField updateNumberField;
    public HBox controlButtons;
    public AnchorPane rootAnchorPane;
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
                            setGraphic(null);
                        } else {
                            // Customize the display text
                            setGraphic(formatOffreDisplay(offre));
                            setStyle("-fx-text-fill: black;");
                        }
                    }
                };
            }
        });


        updateFieldsBox.setAlignment(Pos.TOP_CENTER);
    }

    private VBox formatOffreDisplay(Offre offre) {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");

        // Create ImageView for the offer image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 0, 0); -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 5;");
        if (offre.getImage() != null && !offre.getImage().isEmpty()) {
            File imageFile = new File(offre.getImage());
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            }
        }

        // Create labels for offer details
        Label descriptionLabel = new Label("Description: " + offre.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label priceLabel = new Label(String.format("Price: %.2f -> %.2f", offre.getPriceInit(), offre.getPriceAfter()));
        priceLabel.setStyle("-fx-font-size: 12px;");

        Label datesLabel = new Label("Dates: " + LocalDate.parse(offre.getStartDate(), DATE_FORMATTER).format(DATE_ONLY_FORMATTER) + " to " + LocalDate.parse(offre.getEndDate(), DATE_FORMATTER).format(DATE_ONLY_FORMATTER));
        datesLabel.setStyle("-fx-font-size: 12px;");

        Label placeLabel = new Label("Place: " + offre.getPlace());
        placeLabel.setStyle("-fx-font-size: 12px;");

        Label limitLabel = new Label("Number of places: " + offre.getNumberLimit());
        limitLabel.setStyle("-fx-font-size: 12px;");

        // Add all elements to the VBox
        vbox.getChildren().addAll(imageView, descriptionLabel, priceLabel, datesLabel, placeLabel, limitLabel);

        return vbox;
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
            offreListView.setVisible(false);
            controlButtons.setVisible(false);
            updateFieldsBox.setManaged(true);

            // Fill the fields with the selected offer's data
            updateDescriptionField.setText(selectedOffre.getDescription());
            updatePriceField.setText(String.valueOf(selectedOffre.getPriceInit()));
            updateDiscountField.setText(String.valueOf(selectedOffre.getPriceAfter()));
            updateStartDateField.setValue(LocalDate.parse(selectedOffre.getStartDate(), DATE_FORMATTER));
            updateEndDateField.setValue(LocalDate.parse(selectedOffre.getEndDate(), DATE_FORMATTER));
            updateLocationField.setText(selectedOffre.getPlace());
            updateNumberField.setText(String.valueOf(selectedOffre.getNumberLimit()));
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
            // Clear the current content and set the new content
            rootAnchorPane.getChildren().clear();
            rootAnchorPane.getChildren().add(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void confirmUpdate(ActionEvent actionEvent) {
        Offre selectedOffre = offreListView.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            // Update the selected offer with the new data
            if (updateDescriptionField.getText().isEmpty() || updatePriceField.getText().isEmpty() || updateDiscountField.getText().isEmpty() || updateStartDateField.getValue() == null || updateEndDateField.getValue() == null || updateLocationField.getText().isEmpty() || updateNumberField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Fields");
                alert.setHeaderText("Please fill in all the fields");
                alert.setContentText("All fields are required.");
                alert.showAndWait();
                return;
            }
            try {
                Double.parseDouble(updatePriceField.getText());
                Double.parseDouble(updateDiscountField.getText());

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Price");
                alert.setHeaderText("Price must be a number");
                alert.setContentText("Please enter a valid price.");
                alert.showAndWait();
                return;
            }
            try {
                Integer.parseInt(updateNumberField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Number");
                alert.setHeaderText("Number of places must be an integer");
                alert.setContentText("Please enter a valid number.");
                alert.showAndWait();
                return;
            }
            if (Double.parseDouble(updatePriceField.getText()) < 0 || Double.parseDouble(updateDiscountField.getText()) < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Price");
                alert.setHeaderText("Price must be positive");
                alert.setContentText("Please enter a valid price.");
                alert.showAndWait();
                return;
            }
            if (Double.parseDouble(updateDiscountField.getText()) >= Double.parseDouble(updatePriceField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Discount");
                alert.setHeaderText("Discount must be less than Price");
                alert.setContentText("Please enter a valid discount.");
                alert.showAndWait();
                return;
            }
            selectedOffre.setDescription(updateDescriptionField.getText());
            selectedOffre.setPriceInit(Double.parseDouble(updatePriceField.getText()));
            selectedOffre.setPriceAfter(Double.parseDouble(updateDiscountField.getText()));
            selectedOffre.setStartDate(updateStartDateField.getValue().toString());
            selectedOffre.setEndDate(updateEndDateField.getValue().toString());
            selectedOffre.setPlace(updateLocationField.getText());
            selectedOffre.setNumberLimit(Integer.parseInt(updateNumberField.getText()));


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
            offreListView.setVisible(true);
            controlButtons.setVisible(true);
            updateFieldsBox.setManaged(false);

            // Refresh the list of offers
            loadOffres();
        } else {
            System.out.println("No offer selected.");
        }
    }

    public void cancelUpdate(ActionEvent actionEvent) {
        // Hide the update fields box
        updateFieldsBox.setVisible(false);
        offreListView.setVisible(true);
        controlButtons.setVisible(true);
        updateFieldsBox.setManaged(false);
    }

    public void showReservations(ActionEvent actionEvent) {
        try {
            // Load the FXML file for the "Reservations" dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservationAdmin.fxml"));
            Parent root = loader.load();

            // Get the controller for the "Reservations" dialog
            ReservationAdminController reservationsController = loader.getController();
            reservationsController.initialize(offreListView.getSelectionModel().getSelectedItem());

            // Create a new stage (dialog) for the "Reservations" window
            Stage stage = new Stage();
            stage.setTitle("View Reservations");
            stage.setScene(new Scene(root));

            // Set the current stage as the owner of the new stage (optional, for modality)
            stage.initOwner(offreListView.getScene().getWindow());

            // Show the dialog and wait for it to close
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}