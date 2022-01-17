package net.etfbl.krz.cryptoquiz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.etfbl.krz.cryptography.CACertificate;
import net.etfbl.krz.cryptography.Certificate;
import net.etfbl.krz.model.Player;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ResourceBundle;

import static net.etfbl.krz.cryptography.Certificate.getIssuerCertificate;

public class LoginController implements Initializable {
    @FXML
    public Button loginBtn;
    @FXML
    Label certPath;

    public void login(){
        Security.addProvider(new BouncyCastleProvider());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        try{
            X509Certificate certificate = Certificate.loadUserCertificate(new FileInputStream(new File(certPath.getText())));
            String issuer = certificate.getIssuerDN().getName().replace("CN=","");
            System.out.println("Sertifikat izdao:" + issuer);

            Player player = new Player();
            player.setUsername(Certificate.getCommonName(certificate));
            player.setEmail(Certificate.getEmail(certificate));

           X509Certificate issuerCert;
            if(issuer.equals("CA_TIJELO1")){
                issuerCert = getIssuerCertificate(1);
            } else {
                issuerCert = getIssuerCertificate(2);
            }

            certificate.verify(issuerCert.getPublicKey(), Certificate.BC_PROVIDER);
            System.out.println("Sertifikat verifikovan!");

            GameController.player = player;
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setTitle("Login");
            stage.setScene(scene);
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
        fileChooser.setTitle("Choose Photo");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Digital certificates","*.cer"));
        File cert = fileChooser.showOpenDialog(stage);

        if(cert!=null){
            certPath.setText(cert.getAbsolutePath());
            try{
                FileInputStream fileInputStream = new FileInputStream(cert);
                X509Certificate userCertificate = Certificate.loadUserCertificate(fileInputStream);
                String user = Certificate.getCommonName(userCertificate);
                System.out.println("This certificate belongs to:" + user);
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