package controllers;

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


import javafx.scene.input.MouseEvent; // Utiliser le bon type d'événement
import java.io.IOException;

public class MenuVoyageurs {

    @FXML
    private AnchorPane anchorpane1;

    @FXML
    private AnchorPane centralAnocherPane;

    @FXML
    private ImageView iconBonPlan;

    @FXML
    private ImageView iconHome;

    @FXML
    private ImageView iconLogout;

    @FXML
    private ImageView iconOffre;

    @FXML
    private Text iconSedeconnecter;

    @FXML
    private ImageView iconSocailMedia;

    @FXML
    private ImageView iconTransport;

    @FXML
    private ImageView iconUser;

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
    private Text textIconBonPlan;

    @FXML
    private Text textIconHome;

    @FXML
    private Text textIconOffre;

    @FXML
    private Text textIconSocail;

    @FXML
    private Text textIconUser;

    @FXML
    private Text textIconVelo;

    @FXML
    private VBox vboxmenu_voyageurs;

    @FXML
    public void initialize() {
        iconHome.setOnMouseClicked(this::loadHomePage);
        iconUser.setOnMouseClicked(this::loadUserPage);
        iconTransport.setOnMouseClicked(this::loadTranportPage);
        iconBonPlan.setOnMouseClicked(this::loadBonplanPage);
        iconOffre.setOnMouseClicked(this::loadOffrePage);
        iconSocailMedia.setOnMouseClicked(this::loadSocialMediaPage);
    }


    private void loadHomePage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homePage.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTranportPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBonplanPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadOffrePage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSocialMediaPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
