package controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import models.station;
import services.StationService;
import test.Session;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DisplayStationEntreprise implements Initializable {

    private Session session = Session.getInstance();

    @FXML
    private TableColumn<station, Integer> colCapacite;

    @FXML
    private TableColumn<station, Integer> colNbVelo;

    @FXML
    private TableColumn<station, String> colNom;

    @FXML
    private TableColumn<station, Double> colPrixHeure;

    @FXML
    private TableColumn<station,Void> colAction;

    @FXML
    private TableColumn<station, String> colTypeVelo;

    @FXML
    private TableView<station> stationTable;

    private StationService stationService = new StationService() {};

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println(session.getId_U());
        loadData();


        stationTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                station selectedStation = stationTable.getSelectionModel().getSelectedItem();
                if (selectedStation != null) {
                    showEditDialog(selectedStation);
                }
            }
        });
    }

    private void loadData() {
        List<station> listStation = stationService.getAll().stream().filter(station -> station.getIdU() == session.getId_U()).collect(Collectors.toList());
        ObservableList<station> observableList = FXCollections.observableArrayList(listStation);

        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrixHeure.setCellValueFactory(new PropertyValueFactory<>("prixheure"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colNbVelo.setCellValueFactory(new PropertyValueFactory<>("nbVelo"));
        colTypeVelo.setCellValueFactory(new PropertyValueFactory<>("typeVelo"));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    station selectedStation = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(selectedStation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        stationTable.setItems(observableList);
    }

    private void showDeleteConfirmation(station selectedStation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la station : " + selectedStation.getNom());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette station ?");

        ButtonType btnDelete = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnDelete, btnCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnDelete) {
            stationService.delete(selectedStation);
            loadData();
        }
    }

    private void showStationDetails(station selectedStation) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la Station");
        alert.setHeaderText("Informations de la station : " + selectedStation.getNom());

        String details = "🚲 Nom : " + selectedStation.getNom() +
                "\n💰 Prix/Heure : " + selectedStation.getPrixheure() + " €" +
                "\n🏠 Capacité : " + selectedStation.getCapacite() +
                "\n🚴 Nombre de vélos : " + selectedStation.getNbVelo() +
                "\n🔧 Type de vélo : " + selectedStation.getTypeVelo();

        alert.setContentText(details);
        alert.showAndWait();
    }

    private void showEditDialog(station selectedStation) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modifier la Station");
        alert.setHeaderText("Modifier les informations de la station : " + selectedStation.getNom());

        GridPane grid = new GridPane();
        TextField nomField = new TextField(selectedStation.getNom());
        TextField prixHeureField = new TextField(String.valueOf(selectedStation.getPrixheure()));
        TextField capaciteField = new TextField(String.valueOf(selectedStation.getCapacite()));
        TextField nbVeloField = new TextField(String.valueOf(selectedStation.getNbVelo()));

        ComboBox<String> typeVeloBox = new ComboBox<>();
        typeVeloBox.getItems().addAll("Vélo de route", "Vélo urbain", "Vélo électrique");
        typeVeloBox.setValue(selectedStation.getTypeVelo());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prix/Heure:"), 0, 1);
        grid.add(prixHeureField, 1, 1);
        grid.add(new Label("Capacité:"), 0, 2);
        grid.add(capaciteField, 1, 2);
        grid.add(new Label("Nombre de vélos:"), 0, 3);
        grid.add(nbVeloField, 1, 3);
        grid.add(new Label("Type de vélo:"), 0, 4);
        grid.add(typeVeloBox, 1, 4);

        alert.getDialogPane().setContent(grid);

        ButtonType btnSave = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnSave, btnCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnSave) {
            selectedStation.setNom(nomField.getText());
            selectedStation.setPrixheure(Double.parseDouble(prixHeureField.getText()));
            selectedStation.setCapacite(Integer.parseInt(capaciteField.getText()));
            selectedStation.setNbVelo(Integer.parseInt(nbVeloField.getText()));
            selectedStation.setTypeVelo(typeVeloBox.getValue());

            stationService.update(selectedStation);

            stationTable.refresh();
        }
    }
}
