package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import test.Session;
import models.Users;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import util.MyDatabase;

import java.util.Optional;


public class Displayexpense {

    private AnchorPane centralAnocherPane;

    public void setCentralAnocherPane(AnchorPane centralAnocherPane) {
        this.centralAnocherPane = centralAnocherPane;
    }

    @FXML
    void addeqpense(ActionEvent event) {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Addexpense.fxml"));
            Parent addExpensePage = loader.load();

            if (centralAnocherPane != null) {
                centralAnocherPane.getChildren().clear(); // Clear previous content
                centralAnocherPane.getChildren().add(addExpensePage);
                System.out.println("Addexpense.fxml loaded inside centralAnchorPane.");
            } else {
                System.err.println("Error: centralAnchorPane is null.");
            }
        } catch (IOException e) {
            System.err.println("Error loading Addexpense.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

}