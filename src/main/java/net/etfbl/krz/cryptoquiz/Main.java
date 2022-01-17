package net.etfbl.krz.cryptoquiz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.etfbl.krz.model.Certificate;
import net.etfbl.krz.model.SecurityUtil;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

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


        System.out.println("Private Key:");
        System.out.println(Certificate.caKey);
        System.out.println("-------");
        System.out.println(Certificate.CA.getPublicKey());

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