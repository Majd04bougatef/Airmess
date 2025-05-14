package controllers.transport;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import models.station;
import netscape.javascript.JSObject;
import services.StationService;
import javafx.scene.web.WebEngine;
import java.net.URL;
import test.Session;
public class FormAddTransport {

    private Session session = Session.getInstance();

    public Text textPays;
    @FXML
    private TextField Capacite, NbVelo, Nom, PrixHeure, Latitude, Longitude;
    @FXML
    private ComboBox<String> TypeVelo;
    @FXML
    private ComboBox<String> pays;
    @FXML
    private WebView mapView;
    @FXML
    private Label lblNomError, lblCapaciteError, lblNbVeloError, lblPrixHeureError, lblTypeVeloError;

    private double lat = 51.1;
    private double lng = -0.3;

    private JavaConnector javaConnector;

    public void initialize() {
        mapView.getEngine().setJavaScriptEnabled(true);
        mapView.getEngine().setConfirmHandler(param -> true);
        if (TypeVelo != null) {
            TypeVelo.getItems().addAll("Vélo urbain", "Vélo de route", "Vélo électrique");
        }

        if (pays != null) {
            pays.getItems().addAll(
                    "Albanie", "Algérie", "Allemagne", "Angola",
                    "Arabie Saoudite", "Argentine", "Arménie", "Australie", "Autriche", "Azerbaïdjan", "Bahamas", "Bahreïn",
                    "Belgique", "Belize", "Bénin", "Brésil", "Bulgarie",
                    "Cameroun", "Canada", "Chili", "Chine", "Colombie",
                    "Corée du Nord", "Corée du Sud", "Costa Rica", "Côte d'Ivoire", "Croatie",
                    "Danemark",  "Dominique", "Égypte", "Émirats arabes unis", "Espagne",
                    "États-Unis", "Éthiopie", "Finlande", "France",
                    "Ghana", "Grèce",
                    "Honduras", "Hongrie", "Inde", "Indonésie", "Irak", "Iran", "Irlande", "Islande","Italie",
                    "Japon", "Jordanie", "Koweït",  "Lesotho", "Lettonie",
                    "Liban", "Libéria", "Libye", "Lituanie", "Luxembourg", "Madagascar", "Malaisie", "Malawi",
                    "Maldives", "Mali", "Malte", "Maroc", "Mauritanie", "Mexique", "Moldavie",
                    "Monaco", "Mongolie", "Monténégro", "Mozambique", "Namibie", "Niger", "Nigéria",
                    "Norvège", "Nouvelle-Zélande", "Oman",
                    "Paraguay", "Pays-Bas", "Pérou", "Philippines", "Pologne", "Portugal", "Qatar", "République centrafricaine",
                    "République dominicaine", "République tchèque", "Roumanie", "Royaume-Uni", "Russie", "Rwanda", "Salvador", "Samoa",
                    "Sénégal", "Serbie", "Singapour", "Slovaquie", "Slovénie", "Soudan",
                    "Soudan du Sud", "Suède", "Suisse", "Syrie", "Tanzanie", "Tchad",
                    "Thaïlande", "Tunisie", "Turquie",
                    "Ukraine", "Uruguay", "Venezuela", "Viêt Nam", "Yémen", "Zambie", "Zimbabwe"
            );
        }

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

    public class JavaConnector {
        public void receiveCoordinates(double latitude, double longitude) {
            System.out.println("Coordonnées reçues : " + latitude + ", " + longitude);
            lat = latitude;
            lng = longitude;
            Platform.runLater(() -> {
                Latitude.setText(String.valueOf(lat));
                Longitude.setText(String.valueOf(lng));
            });
        }
    }

    public void btnAjoutTranport(ActionEvent actionEvent) {
        boolean isValid = true;
        lblNomError.setText("");
        lblCapaciteError.setText("");
        lblNbVeloError.setText("");
        lblPrixHeureError.setText("");

        String nom = Nom.getText().trim();
        if (!nom.matches("[a-zA-Z0-9 ]+")) {
            lblNomError.setText("Nom invalide (lettres, chiffres et espaces seulement)");
            lblNomError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        int capacite = 0;
        try {
            capacite = Integer.parseInt(Capacite.getText().trim());
            if (capacite <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblCapaciteError.setText("Capacité doit être un nombre strictement positif");
            lblCapaciteError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        int nbVelo = 0;
        try {
            nbVelo = Integer.parseInt(NbVelo.getText().trim());
            if (nbVelo < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblNbVeloError.setText("Nombre de vélos doit être un nombre positif");
            lblNbVeloError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        double prixHeure = 0;
        try {
            prixHeure = Double.parseDouble(PrixHeure.getText().trim());
            if (prixHeure < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblPrixHeureError.setText("Prix par heure doit être un nombre positif");
            lblPrixHeureError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        String typeVelo = TypeVelo.getValue();
        if (typeVelo == null || typeVelo.isEmpty()) {
            lblTypeVeloError.setText("Veuillez sélectionner un type de vélo");
            lblTypeVeloError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        String Pays = pays.getValue();
        if (Pays == null || Pays.isEmpty()) {
            lblTypeVeloError.setText("Veuillez sélectionner une pays");
            lblTypeVeloError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Création d'une nouvelle station avec les valeurs par défaut
        // Les valeurs suivantes sont automatiquement initialisées dans le constructeur:
        // - numberRaters = 0
        // - rating = 0.0
        // - statut = "inactive"
        station newStation = new station(session.getId_U(), nom, lat, lng, prixHeure, capacite, nbVelo, typeVelo, Pays);
        
        StationService stService = new StationService() {};
        stService.add(newStation);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("La station a été ajoutée avec succès !");
        alert.showAndWait();

        Nom.clear();
        Capacite.clear();
        NbVelo.clear();
        PrixHeure.clear();
        TypeVelo.getSelectionModel().clearSelection();
        Latitude.clear();
        Longitude.clear();
        lat = 0;
        lng = 0;
    }
}
