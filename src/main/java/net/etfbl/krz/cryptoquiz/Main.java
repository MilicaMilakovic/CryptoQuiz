package net.etfbl.krz.cryptoquiz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.etfbl.krz.cryptography.CACertificate;
import net.etfbl.krz.cryptography.Certificate;
import net.etfbl.krz.cryptography.SecurityUtil;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class Main extends Application {

    public static final File file = new File("src/main/resources/images");
    public static final File resultsFile = new File("src/main/resources/results.txt");
    public static final String resources = file.getAbsolutePath();
    public static final String caDir = "src/main/resources/ca/";
    public static final  String questionsDir = (new File("src/main/resources/questions")).getAbsolutePath();
    public static final String playersDir = (new File("src/main/resources/HuPTrnrah5W9DuupQx6Weu7sDRA=")).getAbsolutePath();
    public static final String playersList = (new File("src/main/resources/HuPTrnrah5W9DuupQx6Weu7sDRA=/c1zxZl0P029IZeS8dDmr42lXLgQ=.txt")).getAbsolutePath();

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
    public void start(Stage stage) throws Exception {

        X509Certificate cert = Certificate.getIssuer();
        System.out.println(cert.getSubjectDN());
        System.out.println(cert.getIssuerDN());
        X500Name rootCertIssuer = new JcaX509CertificateHolder(Certificate.CA).getSubject();
        X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];

        String s= IETFUtils.valueToString(cn.getFirst().getValue());
        System.out.println("------");
        System.out.println(s);

        ////// kriptovanje fajla sa rezultatima na pocetku, kad je prazan
//        KeyPair keyPair = Certificate.getUserKeyPair(new File("src/main/resources/ca/root.jks"),"sigurnost","root");
//        SecurityUtil.asymmetricEncryption(resultsFile,keyPair.getPublic());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("start-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 522);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Login");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}