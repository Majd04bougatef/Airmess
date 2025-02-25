package controllers.transport;

import controllers.DisplayStationVoyageurs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.station;
import test.Session;

import java.io.IOException;
import javafx.scene.shape.Rectangle;

public class CardStation {

    public Button btnAvis;

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

    @FXML
    void review(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/formAvis.fxml"));
            Parent formAvis = loader.load();

            FormAvis controller = loader.getController();
            controller.setStation(currentStation);

            DisplayStationVoyageurs mainController = DisplayStationVoyageurs.getInstance();
            mainController.showAvisForm(formAvis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyRoundedImage(ImageView imageView, double radius) {
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        imageView.setClip(clip);
    }


    @FXML
    void reserver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/reservationTransport.fxml"));
            Parent reservationForm = loader.load();

            ReservationTransport controller = loader.getController();

            controller.setReservationData(
                    currentStation.getNbVelo(),
                    currentStation.getPrixheure(),
                    currentStation
            );

            DisplayStationVoyageurs mainController = DisplayStationVoyageurs.getInstance();
            mainController.showAvisForm(reservationForm);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        image.setImage(new Image(imagePath));
        //image.setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }


}
