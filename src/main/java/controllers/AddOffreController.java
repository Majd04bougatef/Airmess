package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.Offre;
import services.OffreService;

public class AddOffreController {

    public Label warningLabel;
    @FXML
    private Button bttn;

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

    @FXML
    private Text text1;

    public void handleAjoutOffre(ActionEvent actionEvent) {
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
        offreService.add(offre);
        //close the form
        form.getScene().getWindow().hide();
    }

    private boolean validateForm() {
        if( prixInitial.getText().isEmpty()
                || nouveauPrix.getText().isEmpty()
                || startDate.getValue() == null
                || endDate.getValue() == null
                || numberLimit.getText().isEmpty()
                || description.getText().isEmpty()
                || place.getText().isEmpty()){
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
        if (startDate.getValue().isAfter(endDate.getValue())) {
            warningLabel.setVisible(true);
            warningLabel.setText("La date de début doit être avant la date de fin.");
            return false;
        }
        if (Integer.parseInt(numberLimit.getText()) < 0) {
            warningLabel.setVisible(true);
            warningLabel.setText("Le nombre de places doit être positif.");
            return false;
        }
        return true;

    }
}
