package controllers.Offre;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import models.Reservation;
import services.OffreService;
import services.ReservationService;
import test.Session;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserReservationsController implements Initializable {

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, String> offerDescriptionColumn;

    @FXML
    private TableColumn<Reservation, String> dateColumn;

    @FXML
    private TableColumn<Reservation, Double> priceColumn;

    @FXML
    private TableColumn<Reservation, Void> actionsColumn;

    private ReservationService reservationService;
    private ObservableList<Reservation> reservationsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reservationService = new ReservationService();
        setupTableColumns();
        loadUserReservations();
    }
    // In UpdateReservationController.java
// Find where you're parsing the date string and modify it:

    // Replace existing date parsing code with:
    private Date parseDate(String dateString) {
        try {
            // Define a formatter that matches your input format
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
            return formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Date Error",
                    "Invalid date format",
                    "Please use the format DD-MM-YYYY HH:MM:SS");
            return null;
        }
    }


    private void setupTableColumns() {
        // For the offer description - replace getOffreProperty with appropriate method
        offerDescriptionColumn.setCellValueFactory(cellData -> {
            Reservation reservation = cellData.getValue();
            OffreService offreService = new OffreService();
            reservation.setIdO(offreService.getById(reservation.getIdO().getIdO()));
            String description = reservation.getIdO() != null ?
                    reservation.getIdO().getDescription() : "N/A";
            return new SimpleStringProperty(description);
        });

        dateColumn.setCellValueFactory(cellData -> {
            String dateString = cellData.getValue().getDateRes();
            try {
                // First try to parse with the format that's causing the error
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                Date date = inputFormat.parse(dateString);
                return new SimpleStringProperty(outputFormat.format(date));
            } catch (ParseException e) {
                try {
                    // If first format fails, try other common formats
                    SimpleDateFormat alternateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = alternateFormat.parse(dateString);
                    return new SimpleStringProperty(dateString);
                } catch (ParseException ex) {
                    // If all parsing fails, return the original string
                    return new SimpleStringProperty(dateString);
                }
            }
        });
        // For the price - replace getPriceProperty with appropriate method
        priceColumn.setCellValueFactory(cellData -> {
            Reservation reservation = cellData.getValue();
            OffreService offreService = new OffreService();
            reservation.setIdO(offreService.getById(reservation.getIdO().getIdO()));
            double price = reservation.getIdO() != null ?
                    reservation.getIdO().getPriceAfter() : 0.0;
            return new SimpleDoubleProperty(price).asObject();
        });

        setupActionsColumn();
    }
    private void setupActionsColumn() {
        Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Reservation, Void> call(final TableColumn<Reservation, Void> param) {
                        return new TableCell<>() {
                            private final Button updateBtn = new Button("Update");
                            private final Button cancelBtn = new Button("Cancel");
                            private final HBox pane = new HBox(5);

                            {
                                updateBtn.getStyleClass().add("update-button");
                                cancelBtn.getStyleClass().add("delete-button");

                                pane.setAlignment(Pos.CENTER);
                                pane.getChildren().addAll(updateBtn, cancelBtn);

                                updateBtn.setOnAction(event -> {
                                    Reservation reservation = getTableView().getItems().get(getIndex());

                                    handleUpdateReservation(reservation);
                                });

                                cancelBtn.setOnAction(event -> {
                                    Reservation reservation = getTableView().getItems().get(getIndex());
                                    handleCancelReservation(reservation);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                setGraphic(empty ? null : pane);
                            }
                        };
                    }
                };

        actionsColumn.setCellFactory(cellFactory);
    }

    private void loadUserReservations() {
        List<Reservation> userReservations = reservationService.getAll()
                                                                .stream()
                                .filter(reservation -> reservation.getId_U() == Session.getInstance().getId_U())
                                .toList();

        reservationsList.clear();
        reservationsList.addAll(userReservations);
        reservationsTable.setItems(reservationsList);
    }

    private void handleUpdateReservation(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateReservation.fxml"));
            Parent root = loader.load();

            UpdateReservationController controller = loader.getController();
            controller.initData(reservation);
            controller.setParentController(this);

            rootAnchorPane.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load update form", e.getMessage());
        }
    }

    private void handleCancelReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancellation");
        alert.setHeaderText("Cancel Reservation");
        alert.setContentText("Are you sure you want to cancel this reservation?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationService.delete(reservation);
                loadUserReservations(); // Refresh the table
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation Cancelled",
                        "Your reservation has been successfully cancelled.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Cancellation Failed", e.getMessage());
            }
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
            Parent root = loader.load();
            rootAnchorPane.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to be called from the UpdateReservationController after an update
    public void refreshData() {
        loadUserReservations();
    }
}