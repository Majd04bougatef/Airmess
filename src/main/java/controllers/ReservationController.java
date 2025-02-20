package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Offre;
import models.Reservation;
import services.OffreService;
import services.ReservationService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {

    @FXML
    private FlowPane cardsContainer;

    private OffreService offreService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        offreService = new OffreService();
        loadOffreCards();
    }

    private void loadOffreCards() {
        List<Offre> offres = offreService.getAll();
        for (Offre offre : offres) {
            VBox card = createOffreCard(offre);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createOffreCard(Offre offre) {
        // Create a card layout
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 20; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Add offer details
        Label descriptionLabel = new Label("Description: " + offre.getDescription());
        descriptionLabel.setFont(new Font(14));

        Label priceLabel = new Label(String.format("Price: %.2f -> %.2f", offre.getPriceInit(), offre.getPriceAfter()));
        priceLabel.setFont(new Font(14));

        Label datesLabel = new Label("Dates: " + offre.getStartDate() + " to " + offre.getEndDate());
        datesLabel.setFont(new Font(14));

        Label placeLabel = new Label("Place: " + offre.getPlace());
        placeLabel.setFont(new Font(14));

        Label limitLabel = new Label("Limit: " + offre.getNumberLimit());
        limitLabel.setFont(new Font(14));

        // Add "Reserve Now" button
        Button reserveButton = new Button("Reserve Now");
        reserveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5;");
        reserveButton.setOnAction(event -> handleReserveButtonAction(offre));

        // Add all elements to the card
        card.getChildren().addAll(descriptionLabel, priceLabel, datesLabel, placeLabel, limitLabel, reserveButton);

        return card;
    }

    private void handleReserveButtonAction(Offre offre) {
        try {
            // Load the FXML file for the reservation dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation_dialog.fxml"));
            Parent root = loader.load();

            // Get the controller for the reservation dialog
            ReservationDialogController controller = loader.getController();

            // Create a new stage (dialog) for the reservation window
            Stage stage = new Stage();
            stage.setTitle("Reserve Offer");
            stage.setScene(new Scene(root));

            // Set the current stage as the owner of the new stage (optional, for modality)
            stage.initOwner(cardsContainer.getScene().getWindow());

            // Pass the dialog stage to the controller
            controller.setDialogStage(stage);

            // Show the dialog and wait for it to close
            stage.showAndWait();

            // Handle the result after the dialog is closed
            if (controller.isConfirmed()) {
                String selectedDate = controller.getSelectedDate();
                String paymentMethod = controller.getPaimentMethod();
                ReservationService reservationService = new ReservationService();
                System.out.println(paymentMethod);
                switch (paymentMethod) {
                    case "Reserve with Card":
                        paymentMethod = "carte";
                        break;
                    case "Reserve with Cash":
                        paymentMethod = "espece";
                        break;
                    default:
                        paymentMethod = "espece";
                        break;
                }
                Reservation reservation = new Reservation(offre, selectedDate, paymentMethod, 1);
                ReservationService reservationService1 = new ReservationService();
                reservationService1.add(reservation);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to load the reservation dialog.");
            e.printStackTrace();
        }
    }
}