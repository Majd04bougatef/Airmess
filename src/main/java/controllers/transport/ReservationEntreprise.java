package controllers.transport;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.ReservationTransportDTO;
import services.ReservationTransportService;
import test.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationEntreprise {
    private Session session = Session.getInstance();

    @FXML
    private TableView<ReservationTransportDTO> tableview;
    @FXML
    private TableColumn<ReservationTransportDTO, String> Ref;
    @FXML
    private TableColumn<ReservationTransportDTO, String> colNomUser;
    @FXML
    private TableColumn<ReservationTransportDTO, String> dateRes;
    @FXML
    private TableColumn<ReservationTransportDTO, String> dateFin;
    @FXML
    private TableColumn<ReservationTransportDTO, Integer> colNbVelo;
    @FXML
    private TableColumn<ReservationTransportDTO, String> colTypeVelo;
    @FXML
    private TextField searchField;
    @FXML
    private DatePicker datePicker;

    private ReservationTransportService reservationService = new ReservationTransportService(){};
    private ObservableList<ReservationTransportDTO> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Ref.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colNomUser.setCellValueFactory(new PropertyValueFactory<>("nomUser"));
        dateRes.setCellValueFactory(new PropertyValueFactory<>("dateRes"));
        dateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colNbVelo.setCellValueFactory(new PropertyValueFactory<>("nombreVelo"));
        colTypeVelo.setCellValueFactory(new PropertyValueFactory<>("typeVelo"));

        loadReservations();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> filterData());
    }

    private void loadReservations() {
        List<ReservationTransportDTO> reservations = reservationService.getAllReservationAndNameUser(session.getId_U());
        masterData.setAll(reservations);
        tableview.setItems(masterData);
    }

    private void filterData() {
        String searchText = searchField.getText().toLowerCase();
        LocalDate selectedDate = datePicker.getValue();

        List<ReservationTransportDTO> filteredList = masterData.stream()
                .filter(reservation -> reservation.getReference().toLowerCase().contains(searchText))
                .filter(reservation -> selectedDate == null || reservation.getDateRes().toLocalDateTime().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());

        tableview.setItems(FXCollections.observableArrayList(filteredList));
    }
}
