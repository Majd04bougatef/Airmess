package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.control.TextArea;
import models.review_transport;
import models.station;
import services.ReviewTransportService;
import test.Session;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

public class FormAvis {
    private Session session = Session.getInstance();


    @FXML
    private Text stationNom;

    @FXML
    private TextArea avisText;

    @FXML
    private ImageView star1, star2, star3, star4, star5;

    private station currentStation;


    private int selectedRating = 0;

    private final Image emptyStar = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/star-empty.png")));
    private final Image filledStar = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/star-filled.png")));

    public void setStation(station st) {
        this.currentStation = st;
        stationNom.setText("Avis pour la station : " + st.getNom());
    }

    @FXML
    public void handleStarClick(javafx.scene.input.MouseEvent event) {
        ImageView clickedStar = (ImageView) event.getSource();
        List<ImageView> stars = List.of(star1, star2, star3, star4, star5);

        selectedRating = stars.indexOf(clickedStar) + 1;

        updateStars();
    }

    private void updateStars() {
        List<ImageView> stars = List.of(star1, star2, star3, star4, star5);
        for (int i = 0; i < stars.size(); i++) {
            if (i < selectedRating) {
                stars.get(i).setImage(filledStar);
            } else {
                stars.get(i).setImage(emptyStar);
            }
        }
    }

    @FXML
    void envoyerAvis() {
        if (selectedRating < 1 || selectedRating > 5) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une note entre 1 et 5 !");
            alert.showAndWait();
            return; // Sortie de la méthode pour éviter l'ajout invalide
        }

        if (currentStation == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Aucune station sélectionnée !");
            alert.showAndWait();
            return;
        }

        int stationId = currentStation.getIdS();
        ReviewTransportService reviewTransportService = new ReviewTransportService(){};
        review_transport rt = new review_transport(session.getId_U(), stationId, selectedRating, avisText.getText());

        reviewTransportService.add(rt);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Votre avis a été envoyé avec succès !");
        alert.showAndWait();
    }

    public void goBack(javafx.scene.input.MouseEvent mouseEvent) {
        DisplayStationVoyageurs mainController = DisplayStationVoyageurs.getInstance();
        mainController.cardsContainer.getChildren().clear();

        mainController.initialize();
    }
}
