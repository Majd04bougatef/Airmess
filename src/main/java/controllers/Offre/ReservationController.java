package controllers.Offre;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Offre;
import models.Reservation;
import services.OffreService;
import services.ReservationService;
import util.GeoCodeApi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.mysql.cj.util.TimeUtil.DATE_FORMATTER;

public class ReservationController implements Initializable {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private FlowPane cardsContainer;

    private OffreService offreService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        offreService = new OffreService();
        loadOffreCards();
    }

    private void loadOffreCards() {
        cardsContainer.getChildren().clear();
        List<Offre> offres = offreService.getAll()
                .stream()
                .sorted((o1, o2) -> {
                    boolean o1ExpiredOrNoPlaces = o1.getNumberLimit() == 0 || LocalDate.parse(o1.getEndDate(), DATE_FORMATTER).isBefore(LocalDate.now());
                    boolean o2ExpiredOrNoPlaces = o2.getNumberLimit() == 0 || LocalDate.parse(o2.getEndDate(), DATE_FORMATTER).isBefore(LocalDate.now());
                    return Boolean.compare(o1ExpiredOrNoPlaces, o2ExpiredOrNoPlaces);
                })
                .toList();
        for (Offre offre : offres) {
            VBox card = createOffreCard(offre);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createOffreCard(Offre offre) {
        // Create a card layout
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 20; -fx-border-color: #cccccc; -fx-border-radius: 15; -fx-background-radius: 15; -fx-pref-width: 300; -fx-pref-height: 200;");

        // Add offer details
        Label descriptionLabel = new Label("Description: " + offre.getDescription());
        descriptionLabel.setFont(new Font(14));
        descriptionLabel.setStyle("-fx-text-alignment: center; -fx-alignment: center;");

        Label priceLabel = new Label(String.format("Price: %.2f", offre.getPriceInit()));
        priceLabel.setFont(new Font(14));
        priceLabel.setStyle("-fx-text-alignment: center; -fx-alignment: center;");

        Label discountLabel = new Label(String.format("Discounted Price: %.2f", offre.getPriceAfter()));
        discountLabel.setFont(new Font(14));
        discountLabel.setStyle("-fx-text-alignment: center; -fx-alignment: center; -fx-strikethrough: true;");

        Label datesLabel = new Label("Dates: " + LocalDate.parse(offre.getStartDate(), DATE_FORMATTER).format(DATE_ONLY_FORMATTER) + " to " + LocalDate.parse(offre.getEndDate(), DATE_FORMATTER).format(DATE_ONLY_FORMATTER));
        datesLabel.setFont(new Font(14));
        datesLabel.setStyle("-fx-text-alignment: center; -fx-alignment: center;");

        Label placeLabel;
        try {
            String lat = offre.getPlace().split(",")[0].trim();
            String lon = offre.getPlace().split(",")[1].trim();
            placeLabel = new Label("Place: " + GeoCodeApi.getAddressFromLatLong(lat, lon));
            placeLabel.setFont(new Font(14));
            placeLabel.setStyle("-fx-text-alignment: center; -fx-alignment: center;");
        } catch (Exception e) {
            placeLabel = new Label("Place: " + offre.getPlace());
            placeLabel.setFont(new Font(14));
            placeLabel.setStyle("-fx-text-alignment: center; -fx-alignment: center;");
        }

        Label limitLabel = new Label("Limit: " + offre.getNumberLimit());
        limitLabel.setFont(new Font(14));
        limitLabel.setStyle("-fx-text-alignment: center; -fx-alignment: center;");

        // Add ImageView for the offer image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        if (offre.getImage() != null && !offre.getImage().isEmpty()) {
            File imageFile = new File(offre.getImage());
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            }
        }

        // Add "Reserve Now" button
        Button reserveButton = new Button("Reserve Now");
        reserveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 15;");
        reserveButton.setOnAction(event -> handleReserveButtonAction(offre));

        // Add all elements to the card
        card.getChildren().addAll(imageView, descriptionLabel, priceLabel, discountLabel, datesLabel, placeLabel, limitLabel, reserveButton);
        card.setStyle("-fx-alignment: center; -fx-background-color: #f0f0f0; -fx-pref-width: 300; -fx-pref-height: 200;");

        if (offre.getNumberLimit() == 0) {
            reserveButton.setDisable(true);
        }
        if (LocalDate.parse(offre.getEndDate(), DATE_FORMATTER).isBefore(LocalDate.now())) {
            reserveButton.setDisable(true);
            card.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 20; -fx-border-color: #ff0000; -fx-border-radius: 15; -fx-background-radius: 15; -fx-pref-width: 300; -fx-pref-height: 200;");
            card.setEffect(new javafx.scene.effect.GaussianBlur(6));
        }
        return card;
    }

    private void handleReserveButtonAction(Offre offre) {
        try {
            // Load the FXML file for the reservation dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation_dialog.fxml"));
            Parent root = loader.load();

            // Get the controller for the reservation dialog
            ReservationDialogController controller = loader.getController();
            LocalDate date = LocalDate.parse(offre.getEndDate(), DATE_FORMATTER);

            controller.setDate(date);
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
                    default:
                        paymentMethod = "espece";
                        break;
                }
                Reservation reservation = new Reservation(offre, selectedDate, paymentMethod, 1);
                ReservationService reservationService1 = new ReservationService();
                reservationService1.add(reservation);
                OffreService offreService = new OffreService();
                offre.setNumberLimit(offre.getNumberLimit() - 1);
                offreService.update(offre);
                loadOffreCards();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to load the reservation dialog.");
            e.printStackTrace();
        }
    }
}