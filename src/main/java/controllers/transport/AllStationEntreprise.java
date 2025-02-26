package controllers.transport;

import com.google.gson.Gson;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import models.station;
import services.StationService;
import test.Session;

import java.util.List;
import java.util.stream.Collectors;

public class AllStationEntreprise {

    @FXML
    private WebView mapView;

    private final Session session = Session.getInstance();
    private final StationService stationService = new StationService() {};

    public void initialize() {
        WebEngine webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/html/mapsEntreprise.html").toExternalForm());
        webEngine.setJavaScriptEnabled(true);

        int userId = session.getId_U();
        List<station> stations = stationService.getAll().stream()
                .filter(st -> st.getIdU() == userId)
                .collect(Collectors.toList());

        String jsonStations = new Gson().toJson(stations);
        String escapedJson = jsonStations.replace("\"", "\\\"");

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                webEngine.executeScript("window.loadStations(\"" + escapedJson + "\");");
            }
        });
    }
}
