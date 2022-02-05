package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import net.etfbl.krz.cryptography.Certificate;
import net.etfbl.krz.cryptography.SecurityUtil;
import net.etfbl.krz.model.Player;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static net.etfbl.krz.cryptoquiz.Main.resultsFile;

public class ResultsController implements Initializable {

    @FXML
    TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            KeyPair keyPair = Certificate.getUserKeyPair(new File(Main.caDir+"root.jks"),"sigurnost","root");
            SecurityUtil.asymmetricDecryption(resultsFile,keyPair.getPrivate());

            List<String> lines = Files.readAllLines(resultsFile.toPath());
            ArrayList<Player> players = new ArrayList<>();

            for (String line: lines) {
                String[] parts = line.split(" # ");
                String result = parts[2].split("/")[0];
                Player p = new Player(parts[0],parts[1],Integer.parseInt(result));
                players.add(p);
            }

            players.sort(Comparator.comparing(Player::getResult).thenComparing(Player::getTime).reversed());
            String score="";
            for(int i =1; i <= players.size(); i++){
                score+=( "\t\t "+i + ". " + players.get(i-1).getUsername() + "\t\t\t " + players.get(i-1).getTime()
                                        +   "\t\t\t " + players.get(i-1).getResult()+ "/5 \n");
            }

            textArea.setText(score);

            SecurityUtil.asymmetricEncryption(resultsFile,keyPair.getPublic());

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
