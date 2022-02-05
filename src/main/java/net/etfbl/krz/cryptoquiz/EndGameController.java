package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import net.etfbl.krz.cryptography.Certificate;
import net.etfbl.krz.cryptography.SecurityUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.security.KeyPair;
import java.util.ResourceBundle;

import static net.etfbl.krz.cryptoquiz.Main.resultsFile;

public class EndGameController implements Initializable {
    @FXML
    Label scoreLabel;

    public static  int score;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scoreLabel.setText(score+"/5");

        try{
            KeyPair keyPair = Certificate.getUserKeyPair(new File(Main.caDir+"root.jks"),"sigurnost","root");
            SecurityUtil.asymmetricDecryption(resultsFile,keyPair.getPrivate());

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resultsFile,true));

            String result = GameController.player.getUsername()+" # " + GameController.player.getTime() + " # "
                            + GameController.player.getResult()+"/5 \n";
//            System.out.println(result);
            bufferedWriter.write(result);
            bufferedWriter.close();

            SecurityUtil.asymmetricEncryption(resultsFile, keyPair.getPublic());

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
