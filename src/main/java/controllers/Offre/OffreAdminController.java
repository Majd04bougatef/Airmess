package controllers.Offre;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    private TableView<Offre> offreTableView;
    @FXML
    private TableColumn<Offre, String> descriptionColumn;
    @FXML
    private TableColumn<Offre, Double> priceInitColumn;
    @FXML
    private TableColumn<Offre, Double> priceAfterColumn;
    @FXML
    private TableColumn<Offre, String> startDateColumn;
    @FXML
    private TableColumn<Offre, String> endDateColumn;
    @FXML
    private TableColumn<Offre, String> placeColumn;
    @FXML
    private TableColumn<Offre, Integer> numberLimitColumn;
    @FXML
    private TableColumn<Offre, String> imageColumn;

    private OffreService offreService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        offreService = new OffreService();

        // Initialize table columns
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceInitColumn.setCellValueFactory(new PropertyValueFactory<>("priceInit"));
        priceAfterColumn.setCellValueFactory(new PropertyValueFactory<>("priceAfter"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        numberLimitColumn.setCellValueFactory(new PropertyValueFactory<>("numberLimit"));

        // Custom cell factories for date columns to format dates
        startDateColumn.setCellValueFactory(cellData -> {
            String startDate = cellData.getValue().getStartDate();
            if (startDate != null) {
                LocalDate date = LocalDate.parse(startDate, DATE_FORMATTER);
                return javafx.beans.binding.Bindings.createStringBinding(() -> date.format(DATE_ONLY_FORMATTER));
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });

        endDateColumn.setCellValueFactory(cellData -> {
            String endDate = cellData.getValue().getEndDate();
            if (endDate != null) {
                LocalDate date = LocalDate.parse(endDate, DATE_FORMATTER);
                return javafx.beans.binding.Bindings.createStringBinding(() -> date.format(DATE_ONLY_FORMATTER));
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });

        // Image column
        imageColumn.setCellFactory(param -> new TableCell<Offre, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        imageView.setImage(new Image(imageFile.toURI().toString()));
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        loadOffres();
        updateFieldsBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
    }

    private void loadOffres() {
        List<Offre> offres = offreService.getAll();
        ObservableList<Offre> observableList = FXCollections.observableArrayList(offres);
        offreTableView.setItems(observableList);
    }

    @FXML
    private void handleUpdateButtonAction() {
        Offre selectedOffre = offreTableView.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            // Show the update fields box
            updateFieldsBox.setVisible(true);
            offreTableView.setVisible(false);
            offreTableView.setManaged(false);
            controlButtons.setVisible(false);
            controlButtons.setManaged(false);
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
        Offre selectedOffre = offreTableView.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            // Show the delete confirmation box
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
        Offre selectedOffre = offreTableView.getSelectionModel().getSelectedItem();
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
        Offre selectedOffre = offreTableView.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            // Update the selected offer with the new data
            if (updateDescriptionField.getText().isEmpty() || updatePriceField.getText().isEmpty() ||
                    updateDiscountField.getText().isEmpty() || updateStartDateField.getValue() == null ||
                    updateEndDateField.getValue() == null || updateLocationField.getText().isEmpty() ||
                    updateNumberField.getText().isEmpty()) {
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
            offreTableView.setVisible(true);
            controlButtons.setVisible(true);
            updateFieldsBox.setManaged(false);
            offreTableView.setManaged(true);
            controlButtons.setManaged(true);

            // Refresh the list of offers
            loadOffres();
        } else {
            System.out.println("No offer selected.");
        }
    }

    public void cancelUpdate(ActionEvent actionEvent) {
        // Hide the update fields box
        updateFieldsBox.setVisible(false);
        offreTableView.setVisible(true);
        offreTableView.setManaged(true);
        controlButtons.setVisible(true);
        controlButtons.setManaged(true);
        updateFieldsBox.setManaged(false);
    }

    public void showReservations(ActionEvent actionEvent) {
        try {
            // Load the FXML file for the "Reservations" dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservationAdmin.fxml"));
            Parent root = loader.load();

            // Get the controller for the "Reservations" dialog
            ReservationAdminController reservationsController = loader.getController();
            reservationsController.initialize(offreTableView.getSelectionModel().getSelectedItem());

            // Create a new stage (dialog) for the "Reservations" window
            Stage stage = new Stage();
            stage.setTitle("View Reservations");
            stage.setScene(new Scene(root));

            // Set the current stage as the owner of the new stage (optional, for modality)
            stage.initOwner(offreTableView.getScene().getWindow());

            // Show the dialog and wait for it to close
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}