package controllers.Offre;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import models.Offre;
import models.Reservation;
import netscape.javascript.JSObject;
import services.CurrencyConverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PaymentController {
    @FXML
    private WebView paymentWebView;
    @FXML
    private ComboBox<String> currencyComboBox;
    private double totalAmount;
    private String clientSecret;
    private String purchaseId;

    private Reservation reservation;

    private ReservationController reservationController;

    private static final String STRIPE_PUBLIC_KEY = "pk_test_51QyNnpEi7sEs3DVkYb41qO9wxMrJ3WlCOhGvqBcChyar5Wx7WqyVGLS7GOwQs7gXFuuJenSHbljCZIqIO36IJu6D00UUtjeGOr";
    private static final String STRIPE_SECRET_KEY = "sk_test_51QyNnpEi7sEs3DVkSqFNnFbi7GWSJKmgwrT0Ci9bDsAoRXqGWk6b6aCCTYJqGtPuBHH5p6PfmkxPelep7EqkkDPL00x26BnXJt";
    private JavaBridge javaBridge;

    @FXML
    private void initialize() {
        Logger.getLogger(PaymentController.class.getName()).info("PaymentController initialized");

        Stripe.apiKey = STRIPE_SECRET_KEY;

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
            PaymentIntent paymentIntent = PaymentIntent.create(createParams);
            this.clientSecret = paymentIntent.getClientSecret();
        } catch (StripeException e) {
            System.err.println("Failed to initialize payment: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    private void loadPaymentForm(double convertedAmount, String currency) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/payment_form.html");
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
                    window.setMember("javaBridge", javaBridge);
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

    public static class JavaBridge {
        private PaymentController controller;

        public JavaBridge(PaymentController controller) {
            this.controller = controller;
        }

        public void handlePaymentSuccess(String paymentId) {
            System.out.println("Payment successful: " + paymentId);
        }
        public void handlePaymentError(String errorMessage) {
            System.err.println("Payment failed: " + errorMessage);
        }

    }
}
