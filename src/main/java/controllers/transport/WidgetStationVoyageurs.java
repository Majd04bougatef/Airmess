package controllers.transport;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class WidgetStationVoyageurs {

    @FXML
    private Pane panewidget;

    @FXML
    public void initialize() {
        loadPage("/transport/DisplayStationVoyageurs.fxml");
    }

    @FXML
    void reserver(ActionEvent event) {
        loadPage("/transport/DisplayStationVoyageurs.fxml");
    }

    @FXML
    public void MesReservation(ActionEvent actionEvent) {
        loadPage("/transport/MesReservationTransportVoyageurs.fxml");
    }

    private void loadPage(String s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(s));
            Parent userPage = loader.load();
            panewidget.getChildren().clear();
            panewidget.getChildren().add(userPage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement du FXML : " + s);
        }
    }
}
