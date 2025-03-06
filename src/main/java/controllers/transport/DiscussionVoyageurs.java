package controllers.transport;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.MessageM;
import services.MessageService;
import services.ReservationTransportService;
import test.Session;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;


public class DiscussionVoyageurs {
    private Session session = Session.getInstance();

    @FXML private ListView<String> reservationList;
    @FXML private VBox chatBox;
    @FXML private Label chatTitle;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;

    private int currentReservationId = -1;
    private int currentUserId = session.getId_U();
    private int currentEnterpriseId = -1;

    private MessageService messageService = new MessageService();
    private ReservationTransportService res = new ReservationTransportService() {};

    @FXML
    public void initialize() {
        loadReservations();
        reservationList.setOnMouseClicked(event -> handleReservationClick());
    }

    @FXML
    private void loadReservations() {
        Map<Integer, String> enterpriseMap = res.getUniqueEnterpriseNames(currentUserId);
        if (!enterpriseMap.isEmpty()) {
            reservationList.getItems().clear();
            reservationList.getItems().addAll(enterpriseMap.values());
            reservationList.getSelectionModel().select(0);
            handleReservationClick();
        } else {
            System.out.println("Aucune entreprise trouvée pour l'utilisateur.");
        }
    }

    @FXML
    private void handleReservationClick() {
        String selectedEnterprise = reservationList.getSelectionModel().getSelectedItem();
        if (selectedEnterprise != null) {
            chatTitle.setText("Discussion avec " + selectedEnterprise);

            // Trouve l'ID de l'entreprise sélectionnée
            for (Map.Entry<Integer, String> entry : res.getUniqueEnterpriseNames(currentUserId).entrySet()) {
                if (entry.getValue().equals(selectedEnterprise)) {
                    currentEnterpriseId = entry.getKey();
                    break;
                }
            }

            // Charge les messages existants
            loadMessages(currentUserId, currentEnterpriseId);

            // Ajouter un message automatique si la discussion est nouvelle
            addAutomaticMessage();
        }
    }

    private void addAutomaticMessage() {
        // Vérifie si c'est la première fois que le voyageur commence une conversation
        List<MessageM> existingMessages = messageService.getMessages(currentUserId, currentEnterpriseId);
        if (existingMessages.isEmpty()) {
            // Crée un message automatique
            String content = "Merci pour votre message ! L'entreprise va vous répondre dans les plus brefs délais.";
            MessageM automaticMessage = new MessageM(currentUserId, currentEnterpriseId, content, new Timestamp(System.currentTimeMillis()));
            messageService.sendMessage(automaticMessage);

            // Recharge les messages pour afficher le message automatique
            loadMessages(currentUserId, currentEnterpriseId);
        }
    }


    private void loadMessages(int userId, int enterpriseId) {
        chatBox.getChildren().clear();
        List<MessageM> messages = messageService.getMessages(userId, enterpriseId);

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

            Label messageLabel = new Label(msg.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-padding: 8px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

            Tooltip tooltip = new Tooltip(dateFormatter.format(messageTimestamp));
            Tooltip.install(messageLabel, tooltip);

            if (msg.getSenderId() == userId) {
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #3498db; -fx-text-fill: white;");
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                messageContainer.getChildren().add(messageLabel);
            } else {
                Label timeLabel = new Label(formattedTime);
                timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                String senderName = getEnterpriseInitial(enterpriseId);
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

    private String getEnterpriseInitial(int enterpriseId) {
        String fullName = res.getUniqueEnterpriseNames(currentUserId).get(enterpriseId);
        return fullName.isEmpty() ? "?" : fullName.substring(0, 1).toUpperCase();
    }

    @FXML
    private void sendMessage() {
        String content = messageInput.getText().trim();
        if (!content.isEmpty() && currentEnterpriseId != -1) {
            MessageM msg = new MessageM(currentUserId, currentEnterpriseId, content, new Timestamp(System.currentTimeMillis()));
            messageService.sendMessage(msg);
            messageInput.clear();
            loadMessages(currentUserId, currentEnterpriseId);
        }
    }

    public void deleteDiscussion(ActionEvent actionEvent) {
    }
}
