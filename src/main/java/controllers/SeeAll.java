package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.ReviewBonplan;
import services.ReviewBonPlanServices;
import java.util.List;

public class SeeAll {

    @FXML
    private VBox commentsList;

    private final ReviewBonPlanServices reviewService = new ReviewBonPlanServices() {};

    public void loadComments(int bonPlanId) {
        List<ReviewBonplan> comments = reviewService.getCommentsByBonPlan(bonPlanId);
        commentsList.getChildren().clear();

        for (ReviewBonplan review : comments) {
            Label commentLabel = new Label("⭐ " + review.getRating() + " - " + review.getCommente());
            commentLabel.setWrapText(true);
            commentLabel.getStyleClass().add("comment-text");

            commentsList.getChildren().add(commentLabel);
        }
    }

    @FXML
    private void goBackToPreviousPage() {
        // Code to go back to the previous page
    }
}
