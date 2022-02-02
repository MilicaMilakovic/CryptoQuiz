package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import net.etfbl.krz.cryptography.Certificate;
import net.etfbl.krz.cryptography.SecurityUtil;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.List;
import java.util.ResourceBundle;

import static net.etfbl.krz.cryptoquiz.Main.resultsFile;

public class ResultsController implements Initializable {

    @FXML
    TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            KeyPair keyPair = Certificate.getUserKeyPair(new File("src/main/resources/ca/root.jks"),"sigurnost","root");
            SecurityUtil.asymmetricDecryption(resultsFile,keyPair.getPrivate());
            String res = "";

            List<String> lines = Files.readAllLines(resultsFile.toPath());
            for (String line: lines) {
                String l ="\t\t\t" + line.replace(" # ","\t\t\t");
                System.out.println(l);
                res+= l+"\n";
            }
            textArea.setText(res);

            SecurityUtil.asymmetricEncryption(resultsFile,keyPair.getPublic());

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
