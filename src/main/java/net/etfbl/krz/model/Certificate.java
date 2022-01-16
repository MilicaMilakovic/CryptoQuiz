package net.etfbl.krz.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Random;

public class Certificate {

    public static final String caDir = "src/main/resources/ca/certs/";
    public static final String privDir = "src/main/resources/ca/private/";
    public static ArrayList<CACertificate> ca = new ArrayList<>();

    // CA tijelo koje ce izdavati korisnicki sertifikat
    public static X509Certificate CA;
    public static RSAPrivateKey caKey;

    static  {
        try{
           CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            FileInputStream inStream = new FileInputStream(caDir+"ca1.crt");
            File keyReader = new File(privDir+"ca1_pkcs8.key");

            ca.add(new CACertificate(inStream,keyReader));

            inStream = new FileInputStream(caDir+"ca2.crt");
            keyReader = new File(privDir+"ca2_pkcs8.key");
            ca.add(new CACertificate(inStream,keyReader));

           System.out.println("CA Tijela ucitana! " );
           inStream.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static X509Certificate getIssuer(){
        CACertificate certInfo = ca.get(new Random().nextInt(ca.size()));
        CA = certInfo.getCertificate();
        caKey = certInfo.getPrivateKey();
        return CA;
    }

}
