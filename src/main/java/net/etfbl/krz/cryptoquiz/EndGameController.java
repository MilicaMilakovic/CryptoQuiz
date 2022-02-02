package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class EndGameController implements Initializable {
    @FXML
    Label scoreLabel;

    public static  int score;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scoreLabel.setText(score+"/5");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Main.resultsFile,true))){
            String result = GameController.player.getUsername()+" | " + GameController.player.getTime() + " | "
                            + GameController.player.getResult()+"\n";
            bufferedWriter.write(result);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
