package controllers.Offre;

import controllers.transport.FormAddTransport;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import models.Offre;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import services.OffreService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class AddOffreController {

    public Label warningLabel;
    public WebView mapView;
    public AnchorPane rootAnchorPane;
    public TextArea descriptionn;
    private JavaConnector javaConnector;

    @FXML
    private TextField description;

    @FXML
    private DatePicker endDate;

    @FXML
    private Pane form;

    @FXML
    private TextField nouveauPrix;

    @FXML
    private TextField numberLimit;

    @FXML
    private TextField place;

    @FXML
    private TextField prixInitial;

    @FXML
    private DatePicker startDate;
    private String imagePath;

    private double lat = 51.1;
    private double lng = -0.3;

    public void handleUploadButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(form.getScene().getWindow());
        if (file != null) {
            imagePath = file.getAbsolutePath();
            System.out.println(file.getAbsolutePath());
        }
    }

    public class JavaConnector {
        public void receiveCoordinates(double latitude, double longitude) {
            System.out.println("Coordonnées reçues : " + latitude + ", " + longitude);
            lat = latitude;
            lng = longitude;
            Platform.runLater(() -> {
                place.setText(latitude + ", " + longitude);

            });
        }
    }
    public void initialize() {
        mapView.getEngine().setJavaScriptEnabled(true);
        mapView.getEngine().setConfirmHandler(param -> true);


        WebEngine webEngine = mapView.getEngine();
        URL url = getClass().getResource("/html/map.html");
        if (url != null) {
            webEngine.load(url.toExternalForm());
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    javaConnector = new JavaConnector();
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaConnector", javaConnector);

                    // Débogage : vérifiez si le membre est défini
                    webEngine.executeScript("console.log('javaConnector défini ?', typeof window.javaConnector);");
                }
            });
        }
    }


    public void handleAjoutOffre() {
        if (!validateForm()) {
            return;
        }
        warningLabel.setVisible(false);
        OffreService offreService = new OffreService();
        Offre offre = new Offre();
        offre.setId_U(1);
        offre.setPriceInit(Double.parseDouble(prixInitial.getText()));
        offre.setPriceAfter(Double.parseDouble(nouveauPrix.getText()));
        offre.setStartDate(startDate.getValue().toString());
        offre.setEndDate(endDate.getValue().toString());
        offre.setNumberLimit(Integer.parseInt(numberLimit.getText()));
        offre.setDescription(description.getText());
        offre.setPlace(place.getText());
        offre.setImage(imagePath);
        offre.setAiDescription(descriptionn.getText());
        offreService.add(offre);
        //close the form
        try {
            // Load the FXML file for the "Add Offer" dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/offreAdmin.fxml"));
            Parent root = loader.load();
            // Clear the current content and set the new content
            rootAnchorPane.getChildren().clear();
            rootAnchorPane.getChildren().add(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean validateForm() {
        if( prixInitial.getText().isEmpty()
                || nouveauPrix.getText().isEmpty()
                || startDate.getValue() == null
                || endDate.getValue() == null
                || numberLimit.getText().isEmpty()
                || description.getText().isEmpty()
                || place.getText().isEmpty()
                || imagePath == null) {
            warningLabel.setVisible(true);
            warningLabel.setText("Veuillez remplir tous les champs.");
            return false;
        }
        try {
            Double.parseDouble(prixInitial.getText());
            Double.parseDouble(nouveauPrix.getText());
            Integer.parseInt(numberLimit.getText());
        } catch (NumberFormatException e) {
            warningLabel.setVisible(true);
            warningLabel.setText("Les champs prix et nombre de places doivent être des nombres.");
            return false;
        }
        if (Double.parseDouble(prixInitial.getText()) < 0 || Double.parseDouble(nouveauPrix.getText()) < 0) {
            warningLabel.setVisible(true);
            warningLabel.setText("Les prix doivent être positifs.");
            return false;
        }
        if (Double.parseDouble(prixInitial.getText())<Double.parseDouble(nouveauPrix.getText())) {
            warningLabel.setVisible(true);
            warningLabel.setText("Les prix Initial doit etre supérieur.");
            return false;
        }
        if (startDate.getValue().isAfter(endDate.getValue())) {
            warningLabel.setVisible(true);
            warningLabel.setText("La date de début doit être avant la date de fin.");
            return false;
        }
        if (endDate.getValue().isBefore(LocalDate.now())) {
            warningLabel.setVisible(true);
            warningLabel.setText("La date de fin doit être après la date d'aujourd'hui.");
            return false;
        }
        if (Integer.parseInt(numberLimit.getText()) < 0) {
            warningLabel.setVisible(true);
            warningLabel.setText("Le nombre de places doit être positif.");
            return false;
        }
        return true;

    }
    @FXML
    public void generateAiContent(ActionEvent actionEvent) {
        try {
            String apiKey = "AIzaSyBLxnvxGsT3kt-2q6KYS7TDrZhECI5UNBI";
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            // Create content object
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            // Construct the prompt with offer details
            String messageContent = "Generate a detailed and appealing description for this offer: " +
                    description.getText() + " located in " + place.getText() +
                    ". Price reduced from " + prixInitial.getText() + " to " + nouveauPrix.getText() +
                    ". Only " + numberLimit.getText() + " places available from " +
                    (startDate.getValue() != null ? startDate.getValue() : "") +
                    " to " + (endDate.getValue() != null ? endDate.getValue() : "");

            part.put("text", messageContent);
            parts.put(part);
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contents);
            requestBody.put("generationConfig", new JSONObject()
                    .put("temperature", 0.7)
                    .put("maxOutputTokens", 800));

            // Create HTTP connection
            java.net.URL url = new java.net.URL(endpoint);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send request
            try (java.io.OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            StringBuilder response = new StringBuilder();
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Parse response JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            String generatedText = "";

            if (jsonResponse.has("candidates") && jsonResponse.getJSONArray("candidates").length() > 0) {
                JSONObject candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);
                if (candidate.has("content")) {
                    JSONObject candidateContent = candidate.getJSONObject("content");
                    if (candidateContent.has("parts") && candidateContent.getJSONArray("parts").length() > 0) {
                        generatedText = candidateContent.getJSONArray("parts").getJSONObject(0).getString("text");
                    }
                }
            }

            // Update the description field
            if (!generatedText.isEmpty()) {
                descriptionn.setText(generatedText);
            } else {
                warningLabel.setText("Failed to generate AI content");
                warningLabel.setVisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            warningLabel.setText("Error generating AI content: " + e.getMessage());
            warningLabel.setVisible(true);
        }
    }
}
