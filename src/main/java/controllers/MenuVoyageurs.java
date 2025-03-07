package controllers;

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


public class MenuVoyageurs {

    @FXML
    private ImageView fotouser;
    @FXML
    private Text nameuser;

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
    void depense(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/expense/displayexpense.fxml"));
            Parent expensePage = loader.load();

            // Get the correct controller and pass the centralAnchorPane reference
            Displayexpense displayExpenseController = loader.getController();
            displayExpenseController.setCentralAnocherPane(centralAnocherPane);

            centralAnocherPane.getChildren().clear(); // Clear previous content
            centralAnocherPane.getChildren().add(expensePage);

            System.out.println("Displayexpense.fxml loaded inside centralAnocherPane.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void compt(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/detailuser.fxml"));
            Parent userPage = loader.load();

            // Get the controller and pass the centralAnocherPane reference
            Detailuser detailUserController = loader.getController();
            detailUserController.setCentralAnocherPane(centralAnocherPane);

            centralAnocherPane.getChildren().clear(); // Clear previous content
            centralAnocherPane.getChildren().add(userPage);

            System.out.println("Detailuser.fxml loaded inside centralAnocherPane.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void BonPlan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherBonPlan.fxml"));
            Parent afficherBonPlan = loader.load();

            AfficherBonPlan controller = loader.getController();
            controller.setCentralAnchorPane(centralAnocherPane); // ✅ Transmettre l'AnchorPane

            centralAnocherPane.getChildren().clear();
            centralAnocherPane.getChildren().add(afficherBonPlan);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Socail(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SocialMediaview.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void User(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddBonPlan.fxml"));
            Parent userPage = loader.load();

            centralAnocherPane.getChildren().setAll(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Velo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport/widgetStationVoyageurs.fxml"));
            Parent userPage = loader.load();


            centralAnocherPane.getChildren().clear();
            centralAnocherPane.getChildren().add(userPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void logout(ActionEvent event) {
        try {
            // Clear the session
            Session.getInstance().logout();

            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/login.fxml"));
            Parent loginPage = loader.load();

            // Get the current stage safely
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

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
            nameuser.setText(session.getCurrentUser().getName());
        }

        if (session.getCurrentUser() != null && session.getCurrentUser().getImagesU() != null) {
            try {
                File imageFile = new File(session.getCurrentUser().getImagesU());
                if (imageFile.exists()) {
                    Image userImage = new Image(imageFile.toURI().toString()); // Convert to valid URL
                    fotouser.setImage(userImage);

                    fotouser.setFitWidth(100);
                    fotouser.setFitHeight(58);
                    fotouser.setPreserveRatio(true);
                } else {
                    System.out.println("Image file not found: " + imageFile.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
