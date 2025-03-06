package controllers.transport;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import models.*;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import services.MessageService;
import services.ReservationTransportService;
import test.Session;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

public class DiscussionEntreprise {
    public ScrollPane scrollPane;
    public Label chatitle;
    private Session session = Session.getInstance();

    @FXML private ListView<String> voyageursList;
    @FXML private VBox chatBox;
    @FXML private Label chatTitle;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private Label initialesVoyageur;
    @FXML private MenuButton menuButton;


    private int currentVoyageurId = -1;
    private int enterpriseId = session.getId_U();

    private MessageService messageService = new MessageService();
    private ReservationTransportService res = new ReservationTransportService(){};

    @FXML
    public void initialize() {
        loadVoyageurs();
        voyageursList.setOnMouseClicked(event -> handleVoyageurClick());
    }

    @FXML
    private void loadVoyageurs() {
        Map<Integer, String> voyageursMap = res.getUniqueVoyageurNames(enterpriseId);

        if (!voyageursMap.isEmpty()) {
            voyageursList.getItems().clear();
            voyageursList.getItems().addAll(voyageursMap.values());
            voyageursList.getSelectionModel().select(0);
            handleVoyageurClick();
        } else {
            System.out.println("Aucun voyageur trouvé pour l'entreprise.");
        }
    }

    @FXML
    private void handleVoyageurClick() {
        String selectedVoyageur = voyageursList.getSelectionModel().getSelectedItem();
        if (selectedVoyageur != null) {
            chatTitle.setText(selectedVoyageur);

            String[] parts = selectedVoyageur.split(" ");
            String initials = (parts.length >= 2)
                    ? parts[0].substring(0, 1).toUpperCase() + parts[1].substring(0, 1).toUpperCase()
                    : parts[0].substring(0, 1).toUpperCase();

            initialesVoyageur.setText(initials);
            for (Map.Entry<Integer, String> entry : res.getUniqueVoyageurNames(enterpriseId).entrySet()) {
                if (entry.getValue().equals(selectedVoyageur)) {
                    currentVoyageurId = entry.getKey();
                    break;
                }
            }
            loadMessages(enterpriseId, currentVoyageurId);
        }
    }

    private void loadMessages(int enterpriseId, int voyageurId) {
        chatBox.getChildren().clear();
        List<MessageM> messages = messageService.getMessages(enterpriseId, voyageurId);

        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp yesterday = new Timestamp(now.getTime() - 24 * 60 * 60 * 1000);

        for (int i = messages.size() - 1; i >= 0; i--) {
            MessageM msg = messages.get(i);
            HBox messageContainer = new HBox(10);

            Timestamp messageTimestamp = msg.getDateM();
            String formattedTime;

            if (messageTimestamp.getTime() >= now.getTime() - (now.getTime() % (24 * 60 * 60 * 1000))) {
                formattedTime = timeFormatter.format(messageTimestamp);
            } else if (messageTimestamp.getTime() >= yesterday.getTime() - (yesterday.getTime() % (24 * 60 * 60 * 1000))) {
                formattedTime = "Hier " + timeFormatter.format(messageTimestamp);
            } else {
                formattedTime = dateFormatter.format(messageTimestamp);
            }

            // Création du label du message
            Label messageLabel = new Label(msg.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-padding: 8px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

            // Ajout d'un Tooltip avec la date complète pour tous les messages
            Tooltip tooltip = new Tooltip(dateFormatter.format(messageTimestamp));
            Tooltip.install(messageLabel, tooltip);

            if (msg.getSenderId() == enterpriseId) {
                // Message envoyé par l'entreprise (sans affichage de l'heure)
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #3498db; -fx-text-fill: white;");
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                messageContainer.getChildren().add(messageLabel);
            } else {
                Label timeLabel = new Label(formattedTime);
                timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                String senderName = getVoyageurInitial(voyageurId);
                Label senderInitial = new Label(senderName);
                senderInitial.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-color: #3498db; -fx-background-radius: 50%; -fx-padding: 8px; " +
                        "-fx-min-width: 30px; -fx-min-height: 30px; -fx-alignment: center;");

                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #ecf0f1; -fx-text-fill: black;");
                messageContainer.setAlignment(Pos.CENTER_LEFT);
                messageContainer.getChildren().addAll(senderInitial, messageLabel, timeLabel);
            }

            chatBox.getChildren().add(0, messageContainer);
        }
    }





    private String getVoyageurInitial(int voyageurId) {
        String fullName = res.getUniqueVoyageurNames(enterpriseId).get(voyageurId);
        return fullName.isEmpty() ? "?" : fullName.substring(0, 1).toUpperCase();
    }

    @FXML
    private void sendMessage() {
        String content = messageInput.getText().trim();
        if (!content.isEmpty() && currentVoyageurId != -1) {
            MessageM msg = new MessageM(enterpriseId, currentVoyageurId, content, new Timestamp(System.currentTimeMillis()));
            messageService.sendMessage(msg);
            messageInput.clear();
            loadMessages(enterpriseId, currentVoyageurId);
        }
    }

    public void handleReservationClick(MouseEvent mouseEvent) {
    }

    @FXML
    private void deleteDiscussion() {
        if (currentVoyageurId != -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette discussion ?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.setTitle("Confirmation");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    messageService.delete(enterpriseId, currentVoyageurId);
                    chatBox.getChildren().clear();
                    loadVoyageurs();
                }
            });
        }
    }

}
