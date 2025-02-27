package controllers.transport;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import models.station;
import netscape.javascript.JSObject;
import services.StationService;

import java.util.List;
import java.util.stream.Collectors;

public class AllStationVoyageurs {
    public WebView mapView;

    private StationService stationService = new StationService() {};
    public void initialize() {
        List<station> stations = stationService.getAll();

        String jsonStations = new Gson().toJson(stations);
        String escapedJson = jsonStations.replace("\"", "\\\"");


        WebEngine webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/html/mapsVoyageurs.html").toExternalForm());
        webEngine.setJavaScriptEnabled(true);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                webEngine.executeScript("window.loadStations(\"" + escapedJson + "\");");
            }
        });
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaApp", this);
                System.out.println("JavaFX est maintenant connecté à JavaScript.");
            }
        });
    }

    public void sendLocation(double latitude, double longitude) {
        Platform.runLater(() -> {
            System.out.println("Localisation reçue : Latitude=" + latitude + ", Longitude=" + longitude);
        });
    }
}
