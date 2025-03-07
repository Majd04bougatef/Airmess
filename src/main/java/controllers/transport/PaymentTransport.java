package controllers.transport;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import services.CurrencyConverter;
import services.ReservationTransportService;
import models.reservation_transport;
import services.SmsService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;


public class PaymentTransport {
    @FXML
    private WebView paymentWebViews;
    @FXML
    private ComboBox<String> currencyComboBox;
    private double totalAmount;
    private String clientSecret;
    private String purchaseId;
    private static reservation_transport reservation;
    private String generatedCode;


    private static final String STRIPE_PUBLIC_KEY = "pk_test_51QyNnpEi7sEs3DVkYb41qO9wxMrJ3WlCOhGvqBcChyar5Wx7WqyVGLS7GOwQs7gXFuuJenSHbljCZIqIO36IJu6D00UUtjeGOr";
    private static final String STRIPE_SECRET_KEY = "sk_test_51QyNnpEi7sEs3DVkSqFNnFbi7GWSJKmgwrT0Ci9bDsAoRXqGWk6b6aCCTYJqGtPuBHH5p6PfmkxPelep7EqkkDPL00x26BnXJt";
    private JavaBridge javaBridge;
    private static final Logger logger = Logger.getLogger(PaymentTransport.class.getName());

    @FXML
    private void initialize() {
        logger.info("PaymentController initialized");

        if (STRIPE_SECRET_KEY == null || STRIPE_SECRET_KEY.isEmpty()) {
            logger.severe("Stripe API key is missing");
            throw new IllegalStateException("Stripe API key is missing");
        }

        Stripe.apiKey = STRIPE_SECRET_KEY;

        if (paymentWebViews != null) {
            paymentWebViews.setContextMenuEnabled(false);
            WebEngine webEngine = paymentWebViews.getEngine();
            webEngine.setJavaScriptEnabled(true);

            javaBridge = new JavaBridge(this);
            JSObject window = (JSObject) webEngine.executeScript("window");
            if (window != null) {
                window.setMember("javaBridge", javaBridge);
                logger.info("JavaBridge lié avec succès à JavaScript.");
            } else {
                logger.severe("Échec de l'accès à l'objet window JavaScript.");
            }
        } else {
            logger.severe("paymentWebViews est null, impossible d'initialiser WebEngine.");
        }

        // Initialisation du ComboBox des devises
        currencyComboBox.getItems().addAll("USD", "EUR", "GBP");
        currencyComboBox.setValue("USD");

        currencyComboBox.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("Convert")) {
                initializePayment();
            }
        });
    }

    public void setReservation(reservation_transport reservation) {
        this.reservation = reservation;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        initializePayment();
    }

    private static void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public boolean verifyCode(String userInputCode) {
        return generatedCode != null && generatedCode.equals(userInputCode);
    }

    private void initializePayment() {
        try {
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

            PaymentIntent paymentIntent = PaymentIntent.create(createParams);
            this.clientSecret = paymentIntent.getClientSecret();

            // Generate and send verification code
            generatedCode = generateVerificationCode();
            SmsService smsService = new SmsService();
            smsService.sendVerificationCode("+21654875020", generatedCode);

            loadPaymentForm(amount, selectedCurrency, generatedCode);
        } catch (StripeException e) {
            logger.severe("Failed to initialize payment: " + e.getMessage());
            showAlert("Payment Error", "Failed to initialize payment: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadPaymentForm(double convertedAmount, String currency, String generatedCode) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/html/paymentTransport.html");
            if (inputStream == null) {
                logger.severe("Payment form not found! Check the file path.");
                showAlert("Error", "Payment form template not found", Alert.AlertType.ERROR);
                return;
            }

            String htmlContent = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            String currencyLabel = currency.equals("USD") ? "USD" : currency;
            String buttonText = String.format("Pay %.2f %s", convertedAmount, currencyLabel);

            htmlContent = htmlContent.replace("${BUTTON_TEXT}", buttonText)
                    .replace("${CLIENT_SECRET}", clientSecret)
                    .replace("${STRIPE_PUBLISHABLE_KEY}", STRIPE_PUBLIC_KEY)
                    .replace("${GENERATED_CODE}", generatedCode);

            WebEngine webEngine = paymentWebViews.getEngine();
            webEngine.loadContent(htmlContent);

            webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    logger.info("Payment form loaded successfully in WebView");
                } else if (newState == Worker.State.FAILED) {
                    logger.severe("Failed to load payment form in WebView");
                }
            });

        } catch (Exception e) {
            logger.severe("Failed to load payment form: " + e.getMessage());
            showAlert("Error", "Failed to load payment form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void onPaymentSuccess() {
        Platform.runLater(() -> {
            showAlert("Paiement réussi", "Le paiement a été effectué avec succès !", Alert.AlertType.INFORMATION);

        });
    }

    private static void ajouterReservation() {
        ReservationTransportService service = new ReservationTransportService(){};

        Timestamp dateRes = Timestamp.from(Instant.now());
        reservation_transport reservation = new reservation_transport();
        reservation.setIdS(reservation.getIdS());
        reservation.setIdU(reservation.getIdU());
        reservation.setDateRes(dateRes);
        reservation.setDateFin(reservation.getDateFin());
        reservation.setPrix(reservation.getPrix());
        reservation.setStatut("en cours");
        reservation.setNombreVelo(reservation.getNombreVelo());
        reservation.setReference(reservation.getReference());

        service.add(reservation);

        showReservationDialog("Votre réservation de " + reservation.getNombreVelo() + " vélo(s) a été enregistrée !\nRéférence: " + reservation.getReference());
    }

    private static void showReservationDialog(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(PaymentTransport.class.getResource("/transport/ReservationDialog.fxml"));
            Parent root = loader.load();

            ReservationDialog controller = loader.getController();
            Stage stage = new Stage();
            controller.setDialogStage(stage);
            controller.setMessage(message);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(PaymentTransport.class.getResource("/css/dialog.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Réservation Confirmée");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class JavaBridge {
        private final PaymentTransport paymentController;

        public JavaBridge(PaymentTransport paymentController) {
            this.paymentController = paymentController;
        }

        public void paymentSuccess(String message) {
            System.out.println("🔔 Message reçu de JS : " + message);

            if ("true".equals(message)) {
                System.out.println("✅ Paiement validé, ajout de la réservation...");
                paymentController.onPaymentSuccess();
            }
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }

}
