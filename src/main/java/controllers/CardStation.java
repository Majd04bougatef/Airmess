package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.station;

import java.io.IOException;

public class CardStation {

    @FXML
    private ImageView image;

    @FXML
    private Text nbvelo;

    @FXML
    private Text nom;

    @FXML
    private VBox vboxx;

    private station currentStation;
    private DisplayStationVoyageurs parentController; // Référence au contrôleur parent

    public void setParentController(DisplayStationVoyageurs parentController) {
        this.parentController = parentController;
    }

    @FXML
    void reserver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservationStationVoyageurs.fxml"));
            Parent reservationRoot = loader.load();

            ReservationController controller = loader.getController();
            controller.setStation(currentStation);

            // Charger dans le ScrollPane du DisplayStationVoyageurs
            DisplayStationVoyageurs mainController = DisplayStationVoyageurs.getInstance();
            mainController.setScrollContent(reservationRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void review(ActionEvent event) {
        System.out.println("Revue de la station: " + currentStation.getNom());
    }

    public void setData(station st) {
        this.currentStation = st;
        nom.setText(st.getNom());
        nbvelo.setText("Vélos disponibles: " + st.getNbVelo());

        if (st.getTypeVelo().equals("velo de route")) {
            image.setImage(new Image(getClass().getResourceAsStream("/image/locationveloVoyageurs/velo de route.jpeg")));
        } else if (st.getTypeVelo().equals("velo urbain")) {
            image.setImage(new Image(getClass().getResourceAsStream("/image/locationveloVoyageurs/velo urbain.jpeg")));
        } else if (st.getTypeVelo().equals("velo electrique")) {
            image.setImage(new Image(getClass().getResourceAsStream("/image/locationveloVoyageurs/velo elect.jpeg")));
        }
    }
}
