package net.etfbl.krz.cryptoquiz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.etfbl.krz.model.Certificate;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class Main extends Application {
    public static final File file = new File("src/main/resources/images");
    public static final String resources = file.getAbsolutePath();

    public static final  String questionsDir = (new File("src/main/resources/questions")).getAbsolutePath();

    public static final SecretKeySpec stegoKey;

    static {
        // Kljuc za dekripciju pitanja sakrivenog u slici je otisak lozinke SIGURNOST

        byte[] key = "SIGURNOST".getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // uzima se 16 bajtova jer je to potrebna duzina kljuca za AES

        stegoKey =new SecretKeySpec(key,"AES");
    }

    @Override
    public void start(Stage stage) throws IOException {

        X509Certificate cert = Certificate.getIssuer();
        System.out.println(cert.getSubjectDN());
        System.out.println(cert.getIssuerDN());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}