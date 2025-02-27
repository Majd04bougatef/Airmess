package controllers;


import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import test.Session;

public class HomePage {

    private Session session = Session.getInstance();

    @FXML
    private Pane pane;

    @FXML
    private Text txt1;

    @FXML
    private Text txt2;

    @FXML
    private Text txt3;

    @FXML
    public void initialize() {
        txt2.setText(session.getName());
    }

}
