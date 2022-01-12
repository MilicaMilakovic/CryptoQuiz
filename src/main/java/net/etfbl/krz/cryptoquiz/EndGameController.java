package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class EndGameController implements Initializable {
    @FXML
    Label scoreLabel;

    public static  int score;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scoreLabel.setText(score+"/5");
    }
}
