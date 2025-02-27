package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.Commentaire;

import java.util.List;

public class AllCommentsDialogController {

    @FXML
    private VBox commentsContainer;

    public void setComments(List<Commentaire> comments) {
        commentsContainer.getChildren().clear();

        for (Commentaire comment : comments) {
            Label commentLabel = new Label(comment.getDescription());
            commentLabel.setWrapText(true);
            commentsContainer.getChildren().add(commentLabel);
        }
    }
}