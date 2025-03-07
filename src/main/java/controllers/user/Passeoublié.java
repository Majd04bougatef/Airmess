package controllers.user;
import javafx.geometry.Insets;
import javafx.scene.control.Button;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.UsersService;
import models.EmailService;

import javax.mail.MessagingException;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class Passeoublié {

    @FXML
    private TextField email_identifier;

    private UsersService usersService = new UsersService();
    private String verificationCode;
    private Timeline codeExpirationTimer;
    private Stage verificationStage;
    private String userEmail;
    private void navigateToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/user/login.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    void back(ActionEvent event) throws IOException {
        navigateToLogin(event);
    }
    @FXML
    void Rechercher(ActionEvent event) throws MessagingException {
        String email = email_identifier.getText().trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Un problème est survenu", "Veuillez réessayer.");
            return;
        }

        if (!usersService.emailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Un problème est survenu", "Veuillez réessayer.");
            return;
        }

        userEmail = email;
        verificationCode = generateVerificationCode();
        sendVerificationEmail(email, verificationCode);

        showVerificationWindow(event);
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendVerificationEmail(String email, String code) throws MessagingException {
        String subject = "Code de vérification - Réinitialisation du mot de passe";
        String message = "Votre code: " + code + "\n\nCe code expire dans 2 minutes.";
        EmailService.sendConfirmationEmail(email, subject, message);
    }


    private void showVerificationWindow(ActionEvent originalEvent) {
        verificationStage = new Stage();
        verificationStage.initModality(Modality.APPLICATION_MODAL);

        Label timerLabel = new Label("Temps restant: 2:00");
        TextField codeField = new TextField();
        codeField.setPromptText("Entrez le code");

        Label messageLabel = new Label("");
        messageLabel.setTextFill(Color.RED);

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            if (codeField.getText().equals(verificationCode)) {
                verificationStage.close();
                try {
                    navigateToUpdatePassword(originalEvent);
                } catch (IOException ex) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Un problème est survenu", "Veuillez réessayer.");
                    ex.printStackTrace();
                }
            } else {
                messageLabel.setText("Code incorrect.");
            }
        });

        VBox layout = new VBox(10, timerLabel, codeField, messageLabel, okButton);
        layout.setPadding(new Insets(10, 20, 30, 40)); // top, right, bottom, left
        layout.setAlignment(Pos.CENTER);

        startVerificationTimer(timerLabel, messageLabel);

        verificationStage.setScene(new Scene(layout, 300, 200));
        verificationStage.show();
    }


    private void startVerificationTimer(Label timerLabel, Label messageLabel) {
        final int[] timeLeft = {120};

        codeExpirationTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft[0]--;
            timerLabel.setText("Temps restant: " + (timeLeft[0] / 60) + ":" + (timeLeft[0] % 60));

            if (timeLeft[0] <= 0) {
                codeExpirationTimer.stop();
                verificationCode = null;
                messageLabel.setText("Le code a expiré.");
            }
        }));

        codeExpirationTimer.setCycleCount(Timeline.INDEFINITE);
        codeExpirationTimer.play();
    }

    private void navigateToUpdatePassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/Updatepasswordfromforget.fxml"));
        Parent updateView = loader.load();
        Updatepasswordfromforget controller = loader.getController();
        controller.setUserEmail(userEmail);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(updateView));
        stage.show();
    }
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }




}
