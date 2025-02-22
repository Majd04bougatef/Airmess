package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.reservation_transport;
import models.station;
import services.ReservationTransportService;
import test.Session;

import java.sql.Timestamp;
import java.time.Instant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javafx.scene.shape.Rectangle;

public class CardStation {

    private Session session = Session.getInstance();

    @FXML
    private Text idStation;


    @FXML
    private ImageView image;

    @FXML
    private Text nbvelo;

    @FXML
    private Text nom;

    @FXML
    private VBox vboxx;

    private station currentStation;


    public void initialize() {
        applyRoundedImage(image, 20);
    }

    private void applyRoundedImage(ImageView imageView, double radius) {
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        imageView.setClip(clip);
    }


    private String generateReference(int idU, Date dateRes) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(dateRes);

        int randomNum = new Random().nextInt(9000) + 1000;

        String userIdFormatted = String.format("%04d", idU);

        return "AIR-" + datePart +"-"+ randomNum + "-" + userIdFormatted;
    }


    @FXML
    void reserver(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Réservation de vélo");
        alert.setHeaderText("Station: " + currentStation.getNom());
        alert.setContentText("Nombre de vélos disponibles: " + currentStation.getNbVelo());

        // Appliquer le style personnalisé
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/alert-style.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        TextField inputField = new TextField();
        inputField.setPromptText("Entrez le nombre de vélos à réserver");
        inputField.getStyleClass().add("text-field");

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new Text("Indiquez le nombre de vélos à réserver :"), inputField);
        vbox.setStyle("-fx-padding: 10px;");

        alert.getDialogPane().setContent(vbox);

        ButtonType buttonReserver = new ButtonType("Réserver");
        ButtonType buttonAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonReserver, buttonAnnuler);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonReserver) {
                try {
                    int nbVeloRes = Integer.parseInt(inputField.getText());

                    if (nbVeloRes <= 0) {
                        showErrorAlert("Veuillez entrer un nombre valide (> 0).");
                    } else if (nbVeloRes > currentStation.getNbVelo()) {
                        showErrorAlert("Le nombre de vélos demandés dépasse le nombre disponible !");
                    } else {
                        ajouterReservation(nbVeloRes);
                    }
                } catch (NumberFormatException e) {
                    showErrorAlert("Veuillez entrer un nombre valide.");
                }
            }
        });
    }


    private void showErrorAlert(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur de réservation");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.show();
    }

    private void ajouterReservation(int nbVeloRes) {
        ReservationTransportService service = new ReservationTransportService(){};

        session.checkLogin();
        int userId = session.getId_U();
        Timestamp dateRes = Timestamp.from(Instant.now());

        String reference = generateReference(userId, new Date());

        reservation_transport reservation = new reservation_transport();
        reservation.setIdS(currentStation.getIdS());
        reservation.setIdU(userId);
        reservation.setDateRes(dateRes);
        reservation.setDateFin(null);
        reservation.setPrix(0.0);
        reservation.setStatut("en cours");
        reservation.setNombreVelo(nbVeloRes);
        reservation.setReference(reference);

        service.add(reservation);

        showReservationDialog("Votre réservation de " + nbVeloRes + " vélo(s) a été enregistrée !\nRéférence: " + reference);
    }

    private void showReservationDialog(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReservationDialog.fxml"));
            Parent root = loader.load();

            ReservationDialog controller = loader.getController();
            Stage stage = new Stage();
            controller.setDialogStage(stage);
            controller.setMessage(message);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Réservation Confirmée");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @FXML
    void review(ActionEvent event) {
    }

    public void setData(station st) {
        this.currentStation = st;
        nom.setText(st.getNom());
        nbvelo.setText("Vélos disponibles: " + st.getNbVelo());

        idStation.setText(String.valueOf(st.getIdS()));

        String imagePath = switch (st.getTypeVelo()) {
            case "velo de route" -> "/image/locationveloVoyageurs/velo de route.jpeg";
            case "velo urbain" -> "/image/locationveloVoyageurs/velo urbain.jpeg";
            case "velo electrique" -> "/image/locationveloVoyageurs/velo elect.jpeg";
            default -> "/image/default.png";
        };

        image.setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }


}
