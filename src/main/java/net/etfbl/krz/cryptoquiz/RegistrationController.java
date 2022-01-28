package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.etfbl.krz.cryptography.Certificate;
import net.etfbl.krz.model.Player;
import net.etfbl.krz.cryptography.SecurityUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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
            if(usernameAvailable(player.getUsername())){
                String hash = SecurityUtil.hashFunction(player.getUsername());

                new File(Main.playersDir+File.separator+hash).mkdir();
                File outputDir= new File(Main.playersDir+File.separator+ hash+File.separator);
                Certificate.issueUserCertificate(player, outputDir);

                // Kreira se kopija sertifikata koja ce biti dostupna korisniku na desktopu,
                // kako korisnik ne bi pristupao direktorijumu sa sertifikatima drugih korisnika

                String desktop = System.getProperty("user.home") + "/Desktop";
//                Files.copy(Paths.get(outputDir+File.separator+player.getUsername()+".cer"),Paths.get(desktop+File.separator+player.getUsername()+".cer"), StandardCopyOption.REPLACE_EXISTING);

                certificatePath.setText(desktop+File.separator+player.getUsername()+".cer");
                username.setText("");
                password.setText("");
                email.setText("");

                // kreira se fajl u kome ce biti upisan broj prijava
                BufferedWriter bw = new BufferedWriter( new FileWriter(outputDir+File.separator+"count.txt"));
                bw.write("0");
                bw.close();

            } else
            {
                System.out.println("Korisnicko ime je zauzeto");
                certificatePath.setText("Korisničko ime je zauzeto!");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean usernameAvailable(String username){
        String hash = SecurityUtil.hashFunction(username);
        System.out.println(username+"=" + hash);

        List<String> players;
        try {
            players = Files.readAllLines(Paths.get(Main.playersList));
            if(players.contains(hash))
                return false;

           FileOutputStream fos = new FileOutputStream(new File(Main.playersList),true);
           fos.write(hash.getBytes(StandardCharsets.UTF_8));
           fos.write("\n".getBytes(StandardCharsets.UTF_8));
           fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return  true;
    }
}
