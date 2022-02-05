package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.etfbl.krz.cryptography.CACertificate;
import net.etfbl.krz.cryptography.Certificate;
import net.etfbl.krz.cryptography.SecurityUtil;
import net.etfbl.krz.model.Player;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.net.URL;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.ResourceBundle;

import static net.etfbl.krz.cryptography.Certificate.*;

public class LoginController implements Initializable {
    @FXML
    public Button loginBtn;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField username;
    @FXML
    Label certPath;

    private String user="";
    int id =0;

    public void login(){
        Security.addProvider(new BouncyCastleProvider());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        try{
            // procitaj potrebne podatke iz ucitanog sertifikata
            X509Certificate certificate = Certificate.loadUserCertificate(new FileInputStream(new File(certPath.getText())));
            String issuer = certificate.getIssuerDN().getName().replace("CN=","");
            System.out.println("Sertifikat izdao:" + issuer);

            // sacuvaj podatke o igracu
            Player player = new Player();
            player.setUsername(Certificate.getCommonName(certificate));
            player.setEmail(Certificate.getEmail(certificate));
            player.setPassword(passwordField.getText());

            if(!username.getText().equals(user)){
                throw new Exception("Ime u sertifikatu i uneseno ime se ne poklapaju!");
            }

            // ucitaj sertifikat CA tijela koje ga je izdalo
            CACertificate issuerCert;
//            X509Certificate issuerCert;
            if("CA_TIJELO1".equals(issuer)){
                issuerCert = getIssuerCertificate(1);
                id=1;
            } else {
                issuerCert = getIssuerCertificate(2);
                id=2;
            }

            // provjeri da je sertifikat izdalo ca tijelo
            certificate.verify(issuerCert.getCertificate().getPublicKey(), Certificate.BC_PROVIDER);

            // provjeri da li je sertifikat istekao
            certificate.checkValidity();

            // TO DO: provjeri da li je sertifikat povucen
            if(checkCRL("list"+id+".crl",certificate))
                throw new Exception("Sertifikat je povucen!");

            // azuriraj broj prijava

            String hash = SecurityUtil.hashFunction(player.getUsername());

            KeyPair keyPair= Certificate.getUserKeyPair(new File(Main.playersDir+File.separator+ hash+File.separator+player.getUsername()+".jks"),
                    player.getPassword(),player.getUsername());


            File countFile= new File(Main.playersDir+File.separator+ hash+File.separator+"count.txt");

            SecurityUtil.asymmetricDecryption(countFile,keyPair.getPrivate());
            BufferedReader br = new BufferedReader(new FileReader(countFile));
            int count = Integer.parseInt(br.readLine());
            br.close();
            ++ count;
            System.out.println("count=" + count);

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(countFile));
            bufferedWriter.write(String.valueOf(count));
            bufferedWriter.close();
            SecurityUtil.asymmetricEncryption(countFile,keyPair.getPublic());

            if(count>=3)
                revokeCertificate(certificate,"list"+id+".crl",issuerCert);

            ////////////////////////////////////////////////////////////////////////////////
            System.out.println("Sertifikat verifikovan!");

//            System.out.println(player);
            GameController.player = player;
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setTitle("Quiz");
            stage.setScene(scene);
            stage.getIcons().add(new Image(new FileInputStream(new File(Main.resources+File.separator+"icon.png"))));

            stage.show();

            Stage thisStage = (Stage) loginBtn.getScene().getWindow();
            thisStage.close();
        }
        catch (Exception e){
            System.out.println("Sertifikat nevazeci!" + e.getMessage());
//            e.printStackTrace();
        }
    }

    public void addQuestion(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-question.fxml"));
        try{
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setTitle("Add Question");
            stage.setScene(scene);
            stage.getIcons().add(new Image(new FileInputStream(new File(Main.resources+File.separator+"icon.png"))));

            stage.show();

            Stage thisStage = (Stage) loginBtn.getScene().getWindow();
            thisStage.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadCertificate(){
        Stage stage = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload certificate");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Digital certificates","*.cer"));
        File cert = fileChooser.showOpenDialog(stage);

        if(cert!=null){
            certPath.setText(cert.getAbsolutePath());
            try{
                FileInputStream fileInputStream = new FileInputStream(cert);
                X509Certificate userCertificate = Certificate.loadUserCertificate(fileInputStream);
                user = Certificate.getCommonName(userCertificate);
                System.out.println("Sertifikat pripada korisniku:" + user);
                fileInputStream.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            loginBtn.setDisable(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginBtn.setDisable(true);
    }
}