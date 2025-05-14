package controllers.transport;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.reservation_transport;
import models.station;
import services.ReservationTransportService;
import services.StationService;
import test.Session;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MesReservationTransportVoyageurs {

    private Session session = Session.getInstance();

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ListView<HBox> reservationListView;

    private ReservationTransportService reservationService = new ReservationTransportService(){};

    @FXML
    public void initialize() {
        statusFilter.setItems(FXCollections.observableArrayList("En cours", "Terminée"));
        statusFilter.setValue("En cours");
        loadReservations();

        statusFilter.setOnAction(event -> filterReservations());

        reservationListView.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
    }

    public void filterReservations() {
        String selectedStatus = statusFilter.getValue();
        loadReservations();
    }

    private void loadReservations() {
        List<reservation_transport> reservations = reservationService.getAll().stream()
                .filter(r -> r.getIdU() == session.getId_U() )
                .collect(Collectors.toList());

        ObservableList<HBox> reservationItems = FXCollections.observableArrayList();

        for (reservation_transport r : reservations) {
            StationService st = new StationService() {};
            station s = st.getById(r.getIdS());
            String reservationText = String.format("Réf: %s | 📍 %s | 📅 %s - %s | 💰 %.2f€/h | 🏷 %s",
                    r.getReference(),
                    s.getNom(),
                    r.getDateRes(),
                    r.getDateFin(),
                    r.getPrix(),
                    r.getStatut());

            HBox hbox = new HBox();
            Text reservationLabel = new Text(reservationText);

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Button updateButton = new Button("Update");
            updateButton.setOnAction(event -> loadDetailPage(r.getId()));

            hbox.getChildren().addAll(reservationLabel, spacer, updateButton);

            if ("Terminée".equalsIgnoreCase(r.getStatut().trim())) {
                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteButton.setOnAction(event -> confirmAndDeleteReservation(r.getId()));
                hbox.getChildren().add(deleteButton);
            }

            reservationItems.add(hbox);
        }

        Platform.runLater(() -> {
            reservationListView.setItems(reservationItems);
            reservationListView.refresh();
        });
    }


    public void loadDetailPage(int reservationId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/ModifierReservationVoyageurs.fxml"));
            Parent root = loader.load();

            ModifierReservationVoyageurs controller = loader.getController();
            controller.setReservationId(reservationId);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier la Réservation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmAndDeleteReservation(int reservationId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette réservation ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            reservationService.deletee(reservationId);
            filterReservations();
        }
    }

}
