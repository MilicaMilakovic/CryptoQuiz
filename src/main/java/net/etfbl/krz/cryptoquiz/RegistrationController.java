package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import net.etfbl.krz.model.Certificate;
import net.etfbl.krz.model.Player;

public class RegistrationController {
    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    TextField email;

    @FXML
    Label certificatePath;

    public void register(){
        String user = username.getText();
        String pass = password.getText();
        String mail = email.getText();

        Player player = new Player(user,pass,mail);
        try {
            Certificate.issueUserCertificate(player);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
