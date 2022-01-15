package net.etfbl.krz.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Random;

public class Certificate {

    public static final String caDir = "src/main/resources/ca/certs/";
    public static ArrayList<X509Certificate> ca = new ArrayList<>();

    // CA tijelo koje ce izdavati korisnicki sertifikat
    public static X509Certificate CA;

    static  {
        try{
           CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

           InputStream inStream = new FileInputStream(caDir+"ca1.crt");
           ca.add((X509Certificate)certificateFactory.generateCertificate(inStream));

           inStream = new FileInputStream(caDir+"ca2.crt");
           ca.add((X509Certificate) certificateFactory.generateCertificate(inStream));

           System.out.println("CA Tijela ucitana! " );
           inStream.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static X509Certificate getIssuer(){
        CA = ca.get(new Random().nextInt(ca.size()));
        return CA;
    }

}
