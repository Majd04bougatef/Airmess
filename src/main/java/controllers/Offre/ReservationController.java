package controllers.Offre;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
    public AnchorPane rootAnchorPane;

    @FXML
    private FlowPane cardsContainer;

    private OffreService offreService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        offreService = new OffreService();
        loadOffreCards();
        cardsContainer.setHgap(10);
        cardsContainer.setVgap(10);
        cardsContainer.setPadding(new Insets(10));
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
            if (offre.getNumberLimit() == 0 || LocalDate.parse(offre.getEndDate(), DATE_FORMATTER).isBefore(LocalDate.now())) {
                card.getStyleClass().add("expired-card");
                javafx.scene.effect.ColorAdjust monochrome = new javafx.scene.effect.ColorAdjust();
                monochrome.setSaturation(-1.0); // -1.0 means completely desaturated (grayscale)

                // Create blur effect
                javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(3.0);

                // Chain effects: monochrome first, then blur
                blur.setInput(monochrome);

                // Apply combined effect to the card
                card.setEffect(blur);
                // monochrome filter

            }
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createOffreCard(Offre offre) {
        // Main card container
        VBox card = new VBox();
        card.getStyleClass().clear();
        card.getStyleClass().add("card");

        card.setMinWidth(315);
        card.setMaxWidth(315);
        card.setMinHeight(420);
        card.setMaxHeight(420);
        card.setAlignment(Pos.TOP_CENTER);

        String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
        card.getStylesheets().add(cssPath);

        // Image container
        HBox imageContainer = new HBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPadding(new Insets(10, 0, 0, 0));

        // Image at the top
        ImageView imageView = new ImageView();
        imageView.setFitWidth(280.0);
        imageView.setFitHeight(130.0);
        imageView.setPreserveRatio(false);
        if (offre.getImage() != null && !offre.getImage().isEmpty()) {
            File imageFile = new File(offre.getImage());
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            }
        }

        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());

        clip.setArcWidth(50);
        clip.setArcHeight(50);
        imageView.setClip(clip);

        imageView.getStyleClass().clear();
        imageView.getStyleClass().add("card-image");
        imageContainer.getChildren().add(imageView);

        // Hidden ID
        Text idText = new Text(String.valueOf(offre.getIdO()));
        idText.setVisible(false);
        idText.setOpacity(0);

        // Content VBox - reduce fixed height
        VBox contentBox = new VBox(5);
        contentBox.getStyleClass().add("card-content");
        contentBox.setMinHeight(150);
        contentBox.setMaxHeight(150);
        contentBox.setPadding(new Insets(5, 10, 5, 10));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Text descriptionText = new Text(offre.getDescription());
        descriptionText.getStyleClass().add("card-title");
        descriptionText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        // Limit text length to prevent excessive wrapping
        if (descriptionText.getText().length() > 80) {
            descriptionText.setText(descriptionText.getText().substring(0, 77) + "...");
        }

        Text priceText = new Text(String.format("%.2f", offre.getPriceInit()));
        priceText.setFont(Font.font("Arial", 11));
        priceText.setStyle("-fx-strikethrough: true; -fx-fill: red;");
        priceText.setFill(javafx.scene.paint.Color.RED);

        Text discountText = new Text(String.format(" %.2f", offre.getPriceAfter()));
        discountText.setFont(Font.font("Arial", 18));
        discountText.setStyle("-fx-fill: green;");
        discountText.setFill(javafx.scene.paint.Color.GREEN);
        HBox priceContainer = new HBox(5);
        priceContainer.setAlignment(Pos.CENTER);
        priceContainer.getChildren().addAll(priceText, discountText);

        Text datesText = new Text("Dates: " + LocalDate.parse(offre.getStartDate(), DATE_FORMATTER).format(DATE_ONLY_FORMATTER)
                + " to " + LocalDate.parse(offre.getEndDate(), DATE_FORMATTER).format(DATE_ONLY_FORMATTER));
        datesText.getStyleClass().add("card-subtitle");
        datesText.setTextAlignment(TextAlignment.LEFT);


        VBox dateContainer = new VBox(5);
        dateContainer.setAlignment(Pos.CENTER_LEFT);
        dateContainer.getChildren().add(datesText);
        Text placeText;
        try {
            String lat = offre.getPlace().split(",")[0].trim();
            String lon = offre.getPlace().split(",")[1].trim();
            String address = GeoCodeApi.getAddressFromLatLong(lat, lon);
            // Limit address length
            if (address.length() > 60) {
                address = address.substring(0, 57) + "...";
            }
            placeText = new Text("Place: " + address);
        } catch (Exception e) {
            placeText = new Text("Place: " + offre.getPlace());
        }
        placeText.getStyleClass().add("card-subtitle");
        placeText.setWrappingWidth(270);

        Text limitText = new Text("Limit: " + offre.getNumberLimit());
        limitText.getStyleClass().add("card-subtitle");
        limitText.setTextAlignment(TextAlignment.LEFT);

        dateContainer.getChildren().addAll(placeText, limitText);
        contentBox.getChildren().addAll(descriptionText, priceContainer, dateContainer);

        // Spacer to push button to bottom but with controlled expansion
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.SOMETIMES);
        spacer.setMinHeight(10);

        // Buttons container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 0, 5, 0));
        buttonBox.getStyleClass().clear();
        buttonBox.getStyleClass().add("card-actions");
        buttonBox.setMinHeight(30);
        buttonBox.setMaxHeight(30);

        Button reserveButton = new Button("Reserve Now");
        reserveButton.getStyleClass().add("btn-primary");
        reserveButton.setOnAction(event -> handleReserveButtonAction(offre));
        Button detailsButton = new Button("Details");
        detailsButton.getStyleClass().add("btn-secondary");
        detailsButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/offreDetails.fxml"));
                Parent root = loader.load();
                OffreDetailsController controller = loader.getController();
                controller.setOffre(offre);
                Stage stage = new Stage();
                stage.setTitle("Offer Details");
                stage.setScene(new Scene(root));
                stage.initOwner(cardsContainer.getScene().getWindow());
                stage.show();
            } catch (Exception e) {
                System.err.println("Failed to load offre details window." + e.getMessage());
            }
        });

        buttonBox.getChildren().add(reserveButton);
        buttonBox.getChildren().add(detailsButton);

        // Add all components to the main card
        card.getChildren().addAll(imageContainer, idText, contentBox, spacer, buttonBox);

        // Handle disabled state
        if (LocalDate.parse(offre.getEndDate(), DATE_FORMATTER).isBefore(LocalDate.now()) || offre.getNumberLimit() == 0) {
            reserveButton.setDisable(true);
            detailsButton.setDisable(true);
            card.getStyleClass().add("expired-card");
        }

        return card;
    }

    //    private void handleReserveButtonAction(Offre offre) {
//
//        try {
//            // Load the FXML file for the reservation dialog
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation_dialog.fxml"));
//            Parent root = loader.load();
//
//            // Get the controller for the reservation dialog
//            ReservationDialogController controller = loader.getController();
//            LocalDate date = LocalDate.parse(offre.getEndDate(), DATE_FORMATTER);
//
//            controller.setDate(date);
//            // Create a new stage (dialog) for the reservation window
//            Stage stage = new Stage();
//            stage.setTitle("Reserve Offer");
//            stage.setScene(new Scene(root));
//
//            // Set the current stage as the owner of the new stage (optional, for modality)
//            stage.initOwner(cardsContainer.getScene().getWindow());
//
//            // Pass the dialog stage to the controller
//            controller.setDialogStage(stage);
//
//            // Show the dialog and wait for it to close
//            stage.showAndWait();
//
//            // Handle the result after the dialog is closed
//            if (controller.isConfirmed()) {
//                String selectedDate = controller.getSelectedDate();
//                String paymentMethod = controller.getPaimentMethod();
//                ReservationService reservationService = new ReservationService();
//                System.out.println(paymentMethod);
//                switch (paymentMethod) {
//                    case "Reserve with Card":
//                        paymentMethod = "carte";
//                        break;
//                    default:
//                        paymentMethod = "espece";
//                        break;
//                }
//                Reservation reservation = new Reservation(offre, selectedDate, paymentMethod, 1);
//                ReservationService reservationService1 = new ReservationService();
//                reservationService1.add(reservation);
//                OffreService offreService = new OffreService();
//                offre.setNumberLimit(offre.getNumberLimit() - 1);
//                offreService.update(offre);
//                loadOffreCards();
//            }
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            System.out.println("Failed to load the reservation dialog.");
//            e.printStackTrace();
//        }
//    }
    private void handleReserveButtonAction(Offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/paymentWindow.fxml"));
            Parent root = loader.load();
            PaymentController controller = loader.getController();
            controller.setTotalAmount(offre.getPriceAfter());
            controller.setOffre(offre);
            rootAnchorPane.getChildren().setAll(root);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleUserReservationsButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/userReservations.fxml"));
            Parent root = loader.load();
            rootAnchorPane.getChildren().setAll(root);
        } catch (Exception e) {
            System.err.println("Failed to load user reservations window." + e.getMessage());
        }
    }

}