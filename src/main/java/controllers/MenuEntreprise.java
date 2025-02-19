package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class MenuEntreprise {

    @FXML
    private AnchorPane anchorpane1;

    @FXML
    private AnchorPane centralAnocherPane;

    @FXML
    private ImageView iconLogout;

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


    public void Home(ActionEvent actionEvent) {
    }

    public void User(ActionEvent actionEvent) {
    }

    public void Velo(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/widgetStationEntreprise.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void BonPlan(ActionEvent actionEvent) {
    }

    public void Offre(ActionEvent actionEvent) {
    }

    public void Socail(ActionEvent actionEvent) {
    }

    public void logout(ActionEvent actionEvent) {

    }
}
