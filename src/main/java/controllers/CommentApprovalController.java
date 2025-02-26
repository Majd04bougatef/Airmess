package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Commentaire;

public class CommentApprovalController {

    @FXML
    private Label originalCommentLabel;

    @FXML
    private Label proposedCommentLabel;

    private boolean approved = false;

    public void setCommentData(String original, String proposed) {
        originalCommentLabel.setText(original);
        proposedCommentLabel.setText(proposed);
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