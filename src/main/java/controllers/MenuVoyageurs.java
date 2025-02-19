package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;

public class MenuVoyageurs {

    @FXML
    private AnchorPane anchorpane1;

    @FXML
    private AnchorPane centralAnocherPane;

    @FXML
    private Text iconSedeconnecter;

    @FXML
    private Circle imageUser;

    @FXML
    private ImageView imgrecherche;

    @FXML
    private ImageView logo;

    @FXML
    private Pane pane1;

    @FXML
    private TextField rechercheMenu_Voyageurs;

    @FXML
    private Separator seaprtor2;

    @FXML
    private Separator separtor1;

    @FXML
    private VBox vboxmenu_voyageurs;

    @FXML
    void BonPlan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Home(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homePage.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Offre(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Socail(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddSocialMedia.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void User(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Velo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayStationVoyageurs.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void logout(ActionEvent event) {



    }


}
