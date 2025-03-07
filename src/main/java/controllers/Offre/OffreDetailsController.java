package controllers.Offre;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Offre;
import util.GeoCodeApi;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class OffreDetailsController implements Initializable {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private ImageView offreImage;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label priceInitLabel;
    @FXML
    private Label priceAfterLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private Label placeLabel;
    @FXML
    private Label limitLabel;
    @FXML
    private Label aiDescriptionLabel;
    @FXML
    private Button closeButton;
    @FXML
    private AnchorPane mainPane;

    private Offre currentOffre;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String cssPath = getClass().getResource("/css/DisplayStationEntreprise.css").toExternalForm();
        mainPane.getStylesheets().add(cssPath);

        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
    }

    public void setOffre(Offre offre) {
        this.currentOffre = offre;
        displayOffreDetails();
    }

    private void displayOffreDetails() {
        if (currentOffre == null) return;

        // Set image
        if (currentOffre.getImage() != null && !currentOffre.getImage().isEmpty()) {
            File imageFile = new File(currentOffre.getImage());
            if (imageFile.exists()) {
                offreImage.setImage(new Image(imageFile.toURI().toString()));
            }
        }

        // Set text information
        descriptionLabel.setText(currentOffre.getDescription());
        priceInitLabel.setText(String.format("%.2f", currentOffre.getPriceInit()));
        priceAfterLabel.setText(String.format("%.2f", currentOffre.getPriceAfter()));

        LocalDate startDate = LocalDate.parse(currentOffre.getStartDate(), DATE_FORMATTER);
        LocalDate endDate = LocalDate.parse(currentOffre.getEndDate(), DATE_FORMATTER);

        startDateLabel.setText(startDate.format(DATE_ONLY_FORMATTER));
        endDateLabel.setText(endDate.format(DATE_ONLY_FORMATTER));

        try {
            String lat = currentOffre.getPlace().split(",")[0].trim();
            String lon = currentOffre.getPlace().split(",")[1].trim();
            String address = GeoCodeApi.getAddressFromLatLong(lat, lon);
            placeLabel.setText(address);
        } catch (Exception e) {
            placeLabel.setText(currentOffre.getPlace());
        }

        limitLabel.setText(String.valueOf(currentOffre.getNumberLimit()));

        // Set AI-generated description if available
        if (currentOffre.getAiDescription() != null && !currentOffre.getAiDescription().isEmpty()) {
            aiDescriptionLabel.setText(currentOffre.getAiDescription());
        } else {
            aiDescriptionLabel.setText("No AI description available.");
        }
    }
}