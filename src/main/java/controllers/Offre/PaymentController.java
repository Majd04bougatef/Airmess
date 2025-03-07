package controllers.Offre;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import models.Offre;
import models.Reservation;
import netscape.javascript.JSObject;
import services.CurrencyConverter;
import services.OffreService;
import services.ReservationService;
import test.Session;
import util.Mailer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PaymentController {
    public DatePicker reservationDatePicker;
    public AnchorPane rootAnchorPane;
    public Label informationsLabel;
    @FXML
    private WebView paymentWebView;
    @FXML
    private ComboBox<String> currencyComboBox;
    private double totalAmount;
    private String clientSecret;
    private String purchaseId;

    private Reservation reservation;
    private Offre offre;

    private ReservationController reservationController;

    private static final String STRIPE_PUBLIC_KEY = "pk_test_51QyNnpEi7sEs3DVkYb41qO9wxMrJ3WlCOhGvqBcChyar5Wx7WqyVGLS7GOwQs7gXFuuJenSHbljCZIqIO36IJu6D00UUtjeGOr";
    private static final String STRIPE_SECRET_KEY = "sk_test_51QyNnpEi7sEs3DVkSqFNnFbi7GWSJKmgwrT0Ci9bDsAoRXqGWk6b6aCCTYJqGtPuBHH5p6PfmkxPelep7EqkkDPL00x26BnXJt";
    private JavaBridge javaBridge;

    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());

    public Offre getOffre() {
        return offre;
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(offre.getStartDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(offre.getEndDate(), formatter);
        informationsLabel.setText("Please select a date between : " + offre.getStartDate().split(" ")[0] + " and " + offre.getEndDate().split(" ")[0] + " :");
        reservationDatePicker.setDayCellFactory(picker -> new DateCell(){
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date, empty);
                setDisable(date.isBefore(startDate.toLocalDate()) || date.isAfter(endDate.toLocalDate()));
            }
        });
    }
    @FXML
    private VBox paymentContainer; // Add this to your FXML and wrap WebView in it

    private void updateWebViewDisabledState(boolean disabled) {
        if (disabled) {
            paymentWebView.setStyle("-fx-opacity: 0.5;");
            // add blur effect

            GaussianBlur blur = new GaussianBlur(5);
            paymentWebView.setEffect(blur);
        } else {
            paymentWebView.setStyle("-fx-opacity: 1.0;");
            paymentWebView.setEffect(null);
        }
    }
    @FXML
    private void initialize() {
        logger.info("PaymentController initialized");

        if (STRIPE_SECRET_KEY == null || STRIPE_SECRET_KEY.isEmpty()) {
            logger.severe("Stripe API key is missing");
            throw new IllegalStateException("Stripe API key is missing");
        }

        logger.info("Stripe API key: " + STRIPE_SECRET_KEY);
        Stripe.apiKey = STRIPE_SECRET_KEY;
        logger.info("Stripe API key set successfully");

        if (paymentWebView != null) {
            paymentWebView.setContextMenuEnabled(false);
            WebEngine webEngine = paymentWebView.getEngine();
            webEngine.setJavaScriptEnabled(true);
        }

        currencyComboBox.getItems().addAll("USD", "EUR", "GBP");
        currencyComboBox.setValue("USD");

        currencyComboBox.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("Convert")) {
                initializePayment();
            }
        });

        reservationDatePicker.valueProperty().addListener((observableValue, oldVal, newVal) -> {
           validateReservationDate(newVal);
        });

        paymentWebView.setDisable(true);
        updateWebViewDisabledState(true);
    }

    private void validateReservationDate(LocalDate selectedDate) {
        if (selectedDate == null || offre == null) {
            paymentWebView.setDisable(true);
            updateWebViewDisabledState(true);
            return;
        }

        try {
            // Fix date parsing by taking only the date part before the space
            String startDateStr = offre.getStartDate().split(" ")[0];
            String endDateStr = offre.getEndDate().split(" ")[0];

            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            System.out.println("Start: " + startDate);
            System.out.println("End: " + endDate);
            System.out.println("Selected: " + selectedDate);

            // Check if selected date is within range
            boolean isValidDate = !selectedDate.isBefore(startDate) && !selectedDate.isAfter(endDate);

            // Enable/disable payment based on date validity
            paymentWebView.setDisable(!isValidDate);
            updateWebViewDisabledState(!isValidDate);

            if (!isValidDate) {
                showAlert("Invalid Date",
                        "Please select a date between " + startDate + " and " + endDate,
                        Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            logger.severe("Date parsing error: " + e.getMessage());
            showAlert("Date Error", "Invalid date format: " + e.getMessage(), Alert.AlertType.ERROR);
            paymentWebView.setDisable(true);
        }
    }
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        initializePayment();
    }

    public void setReservationController(ReservationController reservationController) {
        this.reservationController = reservationController;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void initializePayment() {
        try {
            // Set API key explicitly before each Stripe operation
            Stripe.apiKey = STRIPE_SECRET_KEY;

            String selectedCurrency = currencyComboBox.getValue();
            double amount = totalAmount;
            if (!selectedCurrency.equals("USD")) {
                CurrencyConverter converter = new CurrencyConverter();
                amount = converter.convert(totalAmount, "USD", selectedCurrency);
            }

            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setAmount((long) (amount * 100))
                    .setCurrency(selectedCurrency.equals("USD") ? "usd" : selectedCurrency.toLowerCase())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            // Log before creating payment intent
            logger.info("Creating payment intent with amount: " + amount + " " + selectedCurrency);

            PaymentIntent paymentIntent = PaymentIntent.create(createParams);
            this.clientSecret = paymentIntent.getClientSecret();

            // Log success and call loadPaymentForm if successful
            logger.info("Payment intent created successfully");
            loadPaymentForm(amount, selectedCurrency);
        } catch (StripeException e) {
            logger.severe("Failed to initialize payment: " + e.getMessage());
            showAlert("Payment Error", "Failed to initialize payment: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void loadPaymentForm(double convertedAmount, String currency) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/paymentform.html");
            if (inputStream == null) {
                throw new FileNotFoundException("Payment form not found");
            }
            String htmlContent = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            String currencyLabel = currency.equals("USD") ? "USD" : currency;
            String buttonText = String.format("Pay %.2f %s", convertedAmount, currencyLabel);

            htmlContent = htmlContent.replace("${BUTTON_TEXT}", buttonText)
                    .replace("${CLIENT_SECRET}", clientSecret)
                    .replace("${STRIPE_PUBLISHABLE_KEY}", STRIPE_PUBLIC_KEY);

            WebEngine webEngine = paymentWebView.getEngine();
            webEngine.loadContent(htmlContent);

            webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    javaBridge = new JavaBridge(this);
                    javaBridge.setOffre(offre);
                    window.setMember("java", javaBridge);
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to load payment form: " + e.getMessage());
        }
    }
    @FXML
    private void handlePaymentComplete() {
        if (paymentWebView != null && javaBridge != null) {
            Platform.runLater(() -> {
                try {
                    WebEngine webEngine = paymentWebView.getEngine();
                    webEngine.executeScript(
                            "document.querySelector('#payment-form button[type=\"submit\"]').click();"
                    );
                } catch (Exception e) {
                    showAlert("Error", "Failed to complete payment", Alert.AlertType.ERROR);
                }
            });
        }
    }

    public void handleCloseButtonAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
            Parent root = loader.load();
            rootAnchorPane.getChildren().setAll(root);
        } catch (Exception e) {
            System.err.println("Failed to load reservation window: " + e.getMessage());
        }

    }

    public static class JavaBridge {
        private Offre offre;
        private PaymentController controller;

        public JavaBridge(PaymentController controller) {
            this.controller = controller;
        }

        public void setOffre(Offre offre) {
            this.offre = offre;
        }

        public Offre getOffre() {
            return offre;
        }

        public void handlePaymentSuccess(String paymentId) {
            System.out.println("Payment successful: " + paymentId);
            // Show confirmation to user
                controller.showAlert("Payment Successful",
                        "Your payment has been processed successfully!", Alert.AlertType.INFORMATION);
            ReservationService reservationService = new ReservationService();
            Reservation reservation = new Reservation();
            reservation.setIdO(offre);
            reservation.setId_U(Session.getInstance().getId_U());
            reservation.setDateRes(controller.reservationDatePicker.getValue().toString());
            reservation.setModePaiement("carte");
            reservationService.add(reservation);
            offre.setNumberLimit(offre.getNumberLimit() - 1);
            OffreService offreService = new OffreService();
            offreService.update(offre);
            controller.handleCloseButtonAction(null);
            CompletableFuture.runAsync(() -> {
                Mailer.sendReservationConfirmation(Session.getInstance().getEmail(), reservation, offre);
            });
        }
        public void handlePaymentError(String errorMessage) {
            System.err.println("Payment failed: " + errorMessage);
        }
    }
}
