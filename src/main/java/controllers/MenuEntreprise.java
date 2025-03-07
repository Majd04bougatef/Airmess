package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.MouseEvent;

import test.Session;

import java.io.IOException;

import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import javafx.scene.text.Text;
import javafx.stage.Stage;


import controllers.expense.Displayexpense;
import controllers.user.Detailuser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import test.Session;
import javafx.scene.image.Image;


import java.io.File;
import java.io.IOException;


import javafx.stage.Stage;


public class MenuEntreprise {
    @FXML
    private Text nameus;


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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/widgetStationEntreprise.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reservation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/ReservationEntreprise.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void discussion(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/discussionEntreprise.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void Offre(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/offreAdmin.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout(ActionEvent actionEvent) {
        try {
            // Clear the session
            Session.getInstance().logout();


            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/login.fxml"));
            Parent loginPage = loader.load();


            // Get the current stage safely
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Clear current scene to avoid memory leaks
            stage.getScene().setRoot(new Pane());

            // Set the login scene
            Scene scene = new Scene(loginPage);
            stage.setScene(scene);
            stage.show();

            System.out.println("User logged out successfully.");
        } catch (IOException e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }

    }
    @FXML
    public void initialize() {
        // Get the current user from session
        Session session = Session.getInstance();

        if (session.getCurrentUser() != null && session.getCurrentUser().getName() != null) {
            nameus.setText(session.getCurrentUser().getName());
        }

        if (session.getCurrentUser() != null && session.getCurrentUser().getImagesU() != null) {
            try {
                File imageFile = new File(session.getCurrentUser().getImagesU());
                if (imageFile.exists()) {
                    Image userImage = new Image(imageFile.toURI().toString()); // Convert to valid URL
                    ImageView imageView = new ImageView(userImage);

                    imageView.setFitWidth(100);
                    imageView.setFitHeight(58);
                    imageView.setPreserveRatio(true);

                    // Add the ImageView to the AnchorPane or any other container
                    centralAnocherPane.getChildren().add(imageView);
                } else {
                    System.out.println("Image file not found: " + imageFile.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
