package controllers.transport;

import com.google.protobuf.Message;
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

        System.out.println(session.getId_U());
        Map<Integer, String> enterpriseMap = res.getUniqueEnterpriseNames(currentUserId);

        System.out.println(enterpriseMap);
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

            for (Map.Entry<Integer, String> entry : res.getUniqueEnterpriseNames(currentUserId).entrySet()) {
                if (entry.getValue().equals(selectedEnterprise)) {
                    currentEnterpriseId = entry.getKey();
                    break;
                }
            }

            loadMessages(currentUserId, currentEnterpriseId);
        }
    }

    private void loadMessages(int userId, int enterpriseId) {
        chatBox.getChildren().clear();
        List<MessageM> messages = messageService.getMessages(userId, enterpriseId);

        for (MessageM msg : messages) {
            HBox messageContainer = new HBox();
            Label messageLabel = new Label(msg.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-padding: 8px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

            if (msg.getSenderId() == userId) {
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #2ecc71; -fx-text-fill: white;");
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
            } else {
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #ecf0f1; -fx-text-fill: black;");
                messageContainer.setAlignment(Pos.CENTER_LEFT);
            }

            messageContainer.getChildren().add(messageLabel);
            chatBox.getChildren().add(messageContainer);
        }
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
}
