package net.etfbl.krz.model;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


public class CACertificate {
    private X509Certificate certificate;
    private RSAPrivateKey privateKey;

    public CACertificate(FileInputStream certificate, File privateKey) {
       try{
           // Ucitaj sertifikat CA tijela
           CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
           this.certificate = (X509Certificate)certificateFactory.generateCertificate(certificate);

           // Ucitaj privatni kljuc CA tijela

//           PEMParser pemParser = new PEMParser(privateKey);
//           JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
//           PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
//
//           this.privateKey= (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);

           String key = new String(Files.readAllBytes(privateKey.toPath()));
           key = key.replaceAll(System.lineSeparator(), "").replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
           KeyFactory keyFactory = KeyFactory.getInstance("RSA");

           PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
           this.privateKey =(RSAPrivateKey) keyFactory.generatePrivate(keySpecPKCS8);

       } catch (Exception e){
           e.printStackTrace();
       }
    }

    public CACertificate() {
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
