package controllers.transport;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.reservation_transport;
import models.station;
import services.ReservationTransportService;
import services.StationService;
import test.Session;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.awt.Desktop;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ReservationTransport {

    private static final String ERREUR_MESSAGE = "Erreur !";
    private static final String MESSAGE_ERREUR = "Veuillez remplir correctement les champs.";
    private static final String PRIX_FORMAT = "%.2f €";

    private Session session = Session.getInstance();

    @FXML private Button btnReserver;
    @FXML private TextField nombreVelo;
    @FXML private TextField dateRes;
    @FXML private DatePicker dateFin;
    @FXML private Text prixTotal;
    @FXML private Text infoStation;
    @FXML private Text recapitulatifDetails;
    @FXML private Text recapPrixTotal;

    private double prixTotalValue = 0.0;
    private station currentStation;
    private double prixParHeure;
    private int nbVeloDispo;
    private int idU = session.getId_U();
    private String reference = generateReference(idU, Timestamp.from(Instant.now()));

    @FXML
    public void initialize() {
        dateRes.setText(LocalDateTime.now().toString().substring(0, 16));
    }

    public void setReservationData(int nbVeloDispo, double prixHeure, station selectedStation) {
        this.currentStation = selectedStation;
        this.nbVeloDispo = nbVeloDispo;
        this.prixParHeure = prixHeure;

        infoStation.setText(String.format("Nom de la station : %s\nNombre de vélos disponibles : %d\nPrix par heure : %.2f €",
                selectedStation.getNom(), nbVeloDispo, prixHeure));

        nombreVelo.setText("1");
        updatePrixTotal();
    }

    @FXML
    private void updatePrixTotal() {
        try {
            int nbVelo = Integer.parseInt(nombreVelo.getText());
            LocalDate dateFinValue = dateFin.getValue();

            if (isInvalidReservation(nbVelo, dateFinValue)) {
                prixTotal.setText(ERREUR_MESSAGE);
                return;
            }

            long heures = ChronoUnit.HOURS.between(LocalDateTime.now(), dateFinValue.atStartOfDay());
            prixTotalValue = calculateTotalPrice(heures, nbVelo);
            prixTotal.setText(String.format(PRIX_FORMAT, prixTotalValue));
        } catch (NumberFormatException e) {
            prixTotal.setText(ERREUR_MESSAGE);
        }
    }

    private boolean isInvalidReservation(int nbVelo, LocalDate dateFinValue) {
        return nbVelo <= 0 || nbVelo > nbVeloDispo || dateFinValue == null || !dateFinValue.isAfter(LocalDate.now());
    }

    private double calculateTotalPrice(long heures, int nbVelo) {
        if (heures <= 0) heures = 1;
        return heures * prixParHeure * nbVelo;
    }

    @FXML
    private void reserverTransport() {
        try {
            int nbVelo = Integer.parseInt(nombreVelo.getText());
            LocalDate dateFinValue = dateFin.getValue();

            if (isInvalidReservation(nbVelo, dateFinValue)) {
                showAlert("Erreur", MESSAGE_ERREUR);
                return;
            }

            updateRecap(nbVelo, dateFinValue);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un nombre valide pour les vélos.");
        }
    }

    private void updateRecap(int nbVelo, LocalDate dateFinValue) {
        recapitulatifDetails.setText(String.format("Référence : %s\nStation : %s\nNombre de vélos : %d\nDate de fin : %s\nStatut : En attente",
                reference, currentStation.getNom(), nbVelo, dateFinValue));
        recapPrixTotal.setText(String.format(PRIX_FORMAT, prixTotalValue));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    public void payerReservation(ActionEvent actionEvent) {
        StationService st = new StationService() {};
        try {
            int nbVeloRes = Integer.parseInt(nombreVelo.getText());

            if (nbVeloRes <= 0 || nbVeloRes > nbVeloDispo) {
                showAlert("Erreur", "Nombre de vélos invalide.");
                return;
            }

            ajouterReservation(nbVeloRes);
            st.updateNombreVelo(currentStation.getIdS(), nbVeloRes);
            genererPDF();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un nombre valide pour les vélos.");
        }
    }

    private void ajouterReservation(int nbVeloRes) {
        if (prixTotalValue <= 0) {
            showAlert("Erreur", "Erreur de calcul du prix.");
            return;
        }

        ReservationTransportService service = new ReservationTransportService(){};

        session.checkLogin();
        int userId = session.getId_U();
        Timestamp dateRes = Timestamp.from(Instant.now());

        reservation_transport reservation = new reservation_transport();
        reservation.setIdS(currentStation.getIdS());
        reservation.setIdU(userId);
        reservation.setDateRes(dateRes);
        reservation.setDateFin(Timestamp.valueOf(dateFin.getValue().atStartOfDay()));
        reservation.setPrix(prixTotalValue);
        reservation.setStatut("en cours");
        reservation.setNombreVelo(nbVeloRes);
        reservation.setReference(reference);

        service.add(reservation);

        showReservationDialog("Votre réservation de " + nbVeloRes + " vélo(s) a été enregistrée !\nRéférence: " + reference);
    }

    private void showReservationDialog(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/ReservationDialog.fxml"));
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

    private String generateReference(int idU, Timestamp dateRes) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(dateRes);
        int randomNum = new Random().nextInt(9000) + 1000;
        String userIdFormatted = String.format("%04d", idU);

        String nom = session.getName();
        String prenom = session.getPrnom();

        String nomPart = (nom.length() >= 2) ? nom.substring(0, 2).toUpperCase() : nom.toUpperCase();
        String prenomPart = (prenom.length() >= 2) ? prenom.substring(prenom.length() - 2).toUpperCase() : prenom.toUpperCase();

        return String.format("AIR-%s-%s%s-%d-%s", datePart, nomPart, prenomPart, randomNum, userIdFormatted);
    }


    @FXML
    public void genererPDF() {
        try {
            String pdfPath = "reservation_" + reference + ".pdf";
            Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

            document.open();

            String qrCodeText = "Référence: " + reference;
            Image qrCodeImage = createQRCodeImage(qrCodeText);

            float xPosition = document.getPageSize().getWidth() - qrCodeImage.getScaledWidth() - 20;
            float yPosition = document.getPageSize().getHeight() - qrCodeImage.getScaledHeight() - 20;
            qrCodeImage.setAbsolutePosition(xPosition, yPosition);
            document.add(qrCodeImage);

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Détails de la Réservation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\nRéférence : " + reference));
            document.add(new Paragraph("Station : " + currentStation.getNom()));
            document.add(new Paragraph("Nombre de vélos : " + nombreVelo.getText()));
            document.add(new Paragraph("Date de fin : " + dateFin.getValue()));
            document.add(new Paragraph("Prix total : " + String.format("%.2f €", prixTotalValue)));
            document.add(new Paragraph("\nStatut : En attente"));

            document.close();

            showAlert("PDF généré", "Le PDF de votre réservation a été généré avec succès.");

            ouvrirPDF(pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la génération du PDF.");
        }
    }

    private void ouvrirPDF(String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le fichier PDF.");
        }
    }

    private Image createQRCodeImage(String text) {
        try {
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200, hintMap);

            File qrCodeFile = new File("QRCode.png");
            MatrixToImageWriter.writeToFile(matrix, "PNG", qrCodeFile);

            Image qrCodeImage = Image.getInstance(qrCodeFile.getAbsolutePath());
            qrCodeImage.scaleToFit(100, 100);
            return qrCodeImage;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la génération du QR code.");
            return null;
        }
    }

}