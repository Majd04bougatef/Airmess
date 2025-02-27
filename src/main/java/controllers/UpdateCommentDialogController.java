package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class UpdateCommentDialogController {

    @FXML
    private TextArea commentTextArea;

    private String newCommentText;
    private boolean updated = false;

    public void initialize() {
        // Initialization logic (if needed)
    }

    public void setCommentText(String commentText) {
        commentTextArea.setText(commentText);
    }

    public String getNewCommentText() {
        return newCommentText;
    }

    public boolean isUpdated() {
        return updated;
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        newCommentText = commentTextArea.getText();
        updated = true;
        closeDialog(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        updated = false;
        closeDialog(event);
    }

    private void closeDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}