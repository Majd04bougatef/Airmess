package controllers.expense;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Expense;
import services.ExpenseService;
import test.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Addexpense {

    @FXML
    private Label condrollerdate;

    @FXML
    private Label contrellercantiter;

    @FXML
    private Label controllercategorie;

    @FXML
    private Label controllerdecription;

    @FXML
    private Label controllerimage;

    @FXML
    private Label controllernomex;

    @FXML
    private ImageView fotodepence;

    @FXML
    private TextField amount;

    @FXML
    private ComboBox<String> categorie;

    @FXML
    private DatePicker date;

    @FXML
    private TextArea descript;

    @FXML
    private TextField name;


    private Session session = Session.getInstance();
    private ExpenseService expenseService = new ExpenseService();
    private String currentImagePath;

    public void initialize() {
        if (categorie != null) {
            categorie.getItems().addAll("transport", "restauration", "Hébergements", "autre");
        }

        try {
            session.checkLogin();
        } catch (SecurityException e) {
            controllernomex.setText("Vous devez être connecté");
            controllernomex.setStyle("-fx-text-fill: red;");
            return;
        }

        // Add real-time validation listeners
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                controllernomex.setText("Le nom est requis");
                controllernomex.setStyle("-fx-text-fill: red;");
            } else if (newValue.length() < 3) {
                controllernomex.setText("Minimum 3 caractères");
                controllernomex.setStyle("-fx-text-fill: red;");
            } else if (newValue.length() > 50) {
                controllernomex.setText("Maximum 50 caractères");
                controllernomex.setStyle("-fx-text-fill: red;");
            } else {
                controllernomex.setText("");
            }
        });

        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                amount.setText(oldValue);
                contrellercantiter.setText("Nombres uniquement");
                contrellercantiter.setStyle("-fx-text-fill: red;");
            } else {
                try {
                    if (!newValue.isEmpty()) {
                        double value = Double.parseDouble(newValue);
                        if (value <= 0) {
                            contrellercantiter.setText("Montant doit être > 0");
                            contrellercantiter.setStyle("-fx-text-fill: red;");
                        } else {
                            contrellercantiter.setText("");
                        }
                    }
                } catch (NumberFormatException e) {
                    contrellercantiter.setText("Montant invalide");
                    contrellercantiter.setStyle("-fx-text-fill: red;");
                }
            }
        });

        descript.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                controllerdecription.setText("Description requise");
                controllerdecription.setStyle("-fx-text-fill: red;");
            } else if (newValue.length() < 8) {
                controllerdecription.setText("Minimum 5 caractères");
                controllerdecription.setStyle("-fx-text-fill: red;");
            } else if (newValue.length() > 500) {
                controllerdecription.setText("Maximum 500 caractères");
                controllerdecription.setStyle("-fx-text-fill: red;");
            } else {
                controllerdecription.setText("");
            }
        });

        categorie.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                controllercategorie.setText("Catégorie requise");
                controllercategorie.setStyle("-fx-text-fill: red;");
            } else {
                controllercategorie.setText("");
            }
        });

        date.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                condrollerdate.setText("Date requise");
                condrollerdate.setStyle("-fx-text-fill: red;");
            } else if (newValue.isAfter(LocalDate.now())) {
                condrollerdate.setText("Date future non permise");
                condrollerdate.setStyle("-fx-text-fill: red;");
            } else {
                condrollerdate.setText("");
            }
        });
    }

    @FXML
    void addexpens(ActionEvent event) {
        try {
            session.checkLogin();

            if (validateInputs()) {
                Expense expense = new Expense();
                expense.setNameEX(name.getText());
                expense.setAmount(Double.parseDouble(amount.getText()));
                expense.setDescription(descript.getText());
                expense.setCategory(categorie.getValue());
                expense.setDateE(date.getValue());
                expense.setImagedepense(currentImagePath);
                expense.setId_U(session.getId_U());

                expenseService.add(expense);

                // Show success in a label (you might want to add a success label to your FXML)
                clearFields();
                controllernomex.setText("Dépense ajoutée avec succès");
                controllernomex.setStyle("-fx-text-fill: green;");
            }
        } catch (SecurityException e) {
            controllernomex.setText("Authentification requise");
            controllernomex.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            controllernomex.setText("Erreur: " + e.getMessage());
            controllernomex.setStyle("-fx-text-fill: red;");
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate name
        if (name.getText().trim().isEmpty()) {
            controllernomex.setText("Le nom est requis");
            controllernomex.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (name.getText().length() < 3 || name.getText().length() > 50) {
            controllernomex.setText("Nom: 3-50 caractères");
            controllernomex.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate amount
        if (amount.getText().trim().isEmpty()) {
            contrellercantiter.setText("Montant requis");
            contrellercantiter.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else {
            try {
                double amountValue = Double.parseDouble(amount.getText());
                if (amountValue <= 0) {
                    contrellercantiter.setText("Montant doit être > 0");
                    contrellercantiter.setStyle("-fx-text-fill: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                contrellercantiter.setText("Montant invalide");
                contrellercantiter.setStyle("-fx-text-fill: red;");
                isValid = false;
            }
        }

        // Validate description
        if (descript.getText().trim().isEmpty()) {
            controllerdecription.setText("Description requise");
            controllerdecription.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (descript.getText().length() < 10 || descript.getText().length() > 500) {
            controllerdecription.setText("Description: 10-500 caractères");
            controllerdecription.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate category
        if (categorie.getValue() == null) {
            controllercategorie.setText("Catégorie requise");
            controllercategorie.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate date
        if (date.getValue() == null) {
            condrollerdate.setText("Date requise");
            condrollerdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (date.getValue().isAfter(LocalDate.now())) {
            condrollerdate.setText("Date future non permise");
            condrollerdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate image
        if (fotodepence.getImage() == null) {
            controllerimage.setText("Image requise");
            controllerimage.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        return isValid;
    }

    @FXML
    void Ajouterunephotodep(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                if (file.length() > 5 * 1024 * 1024) {
                    controllerimage.setText("Image > 5MB non permise");
                    controllerimage.setStyle("-fx-text-fill: red;");
                    return;
                }

                String destinationPath = "C:/xampp/htdocs/imguser/" + file.getName();
                File destinationFile = new File(destinationPath);

                Files.copy(file.toPath(), destinationFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                Image image = new Image(destinationFile.toURI().toString());
                fotodepence.setImage(image);
                currentImagePath = destinationPath;
                controllerimage.setText("");

            } catch (IOException e) {
                controllerimage.setText("Erreur: " + e.getMessage());
                controllerimage.setStyle("-fx-text-fill: red;");
                System.err.println("Erreur d'image: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        name.clear();
        amount.clear();
        descript.clear();
        categorie.setValue(null);
        date.setValue(null);
        fotodepence.setImage(null);
        currentImagePath = null;

        // Clear error messages
        controllernomex.setText("");
        contrellercantiter.setText("");
        controllerdecription.setText("");
        controllercategorie.setText("");
        condrollerdate.setText("");
        controllerimage.setText("");
    }


    @FXML
    private void depaff(ActionEvent event) {

    }

}