package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import models.Commentaire;

public class CommentApprovalController {

    @FXML
    private Label originalCommentLabel;

    @FXML
    private TextArea proposedCommentTextArea;

    @FXML
    private Label moderationExplanationLabel; // New Label

    private boolean approved = false;

    public void setCommentData(String original, String proposed, String explanation) {
        originalCommentLabel.setText("Original Comment: " + original);
        proposedCommentTextArea.setText(proposed);
        moderationExplanationLabel.setText("Moderation Reason: " + explanation); // Set Explanation
        proposedCommentTextArea.setEditable(false); // Prevent editing
    }

    @FXML
    public void handleApprove(ActionEvent event) {
        approved = true;
        closeDialog(event);
    }

    @FXML
    public void handleReject(ActionEvent event) {
        approved = false;
        closeDialog(event);
    }

    private void closeDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public boolean isApproved() {
        return approved;
    }
}