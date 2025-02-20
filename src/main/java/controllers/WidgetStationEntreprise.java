package controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class WidgetStationEntreprise {

    @FXML
    private Pane panewidget;

    @FXML
    public void initialize() {
        loadPage("/DisplayStationEntreprise.fxml");
    }

    @FXML
    void btnFormAdd() {
        loadPage("/FormAddTransport.fxml");
    }

    @FXML
    void btnGettStation() {
        loadPage("/DisplayStationEntreprise.fxml");
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent userPage = loader.load();
            panewidget.getChildren().clear();
            panewidget.getChildren().add(userPage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement du FXML : " + fxmlFile);
        }
    }

}
