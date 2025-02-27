package controllers.transport;

import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import models.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import services.MessageService;
import services.ReservationTransportService;
import test.Session;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class DiscussionEntreprise {
    private Session session = Session.getInstance();

    @FXML private ListView<String> voyageursList;
    @FXML private VBox chatBox;
    @FXML private Label chatTitle;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;

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
            chatTitle.setText("Discussion avec " + selectedVoyageur);

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

        for (MessageM msg : messages) {
            HBox messageContainer = new HBox();
            Label messageLabel = new Label(msg.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-padding: 8px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

            if (msg.getSenderId() == enterpriseId) {
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #3498db; -fx-text-fill: white;");
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
        if (!content.isEmpty() && currentVoyageurId != -1) {
            MessageM msg = new MessageM(enterpriseId, currentVoyageurId, content, new Timestamp(System.currentTimeMillis()));
            messageService.sendMessage(msg);
            messageInput.clear();
            loadMessages(enterpriseId, currentVoyageurId);
        }
    }

    public void handleReservationClick(MouseEvent mouseEvent) {
    }
}
