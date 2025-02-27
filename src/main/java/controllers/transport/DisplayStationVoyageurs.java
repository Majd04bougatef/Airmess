package controllers.transport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.station;
import services.StationService;

import java.io.IOException;
import java.util.List;

public class DisplayStationVoyageurs {

    @FXML
    public VBox cardsContainer;

    @FXML
    private ScrollPane mainScrollPane;

    private static DisplayStationVoyageurs instance;

    public DisplayStationVoyageurs() {
        instance = this;
    }

    public static DisplayStationVoyageurs getInstance() {
        return instance;
    }

    public void showAvisForm(Parent formAvis) {
        cardsContainer.getChildren().clear();
        cardsContainer.getChildren().add(formAvis);
    }

    public void initialize() {
        StationService service = new StationService(){};
        List<station> stations = service.getAll();

        HBox hbox = new HBox(10);
        int count = 0;

        for (station st : stations) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/cardStation.fxml"));
            try {
                Parent card = loader.load();
                CardStation controller = loader.getController();


                controller.setData(st);
                hbox.getChildren().add(card);
                count++;

                if (count % 4 == 0) {
                    cardsContainer.getChildren().add(hbox);
                    hbox = new HBox(10);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (count % 4 != 0) {
            cardsContainer.getChildren().add(hbox);
        }
    }


}
