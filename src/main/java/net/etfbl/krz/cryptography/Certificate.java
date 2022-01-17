package net.etfbl.krz.cryptography;

import net.etfbl.krz.model.Player;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Certificate {

    public static final String caDir = "src/main/resources/ca/certs/";
    public static final String privDir = "src/main/resources/ca/private/";
    public static ArrayList<CACertificate> ca = new ArrayList<>();

    // CA tijelo koje ce izdavati korisnicki sertifikat
    public static X509Certificate CA;
    public static RSAPrivateKey caKey;

    private static final String BC_PROVIDER = "BC";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    // distinguished_name defaultne opcije koje ce se upisati u korisnicki sertifikat
    public static final String countryName ="BA";
    public static final String stateOrProvinceName = "RS";
    public static final String localityName = "Banja Luka";
    public static final String organizationName = "Elektrotehnicki fakulet";
    public static final String organizationalUnitName = "ETF";

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

    public static void issueUserCertificate(Player player, File outputDir) throws Exception{

        Security.addProvider(new BouncyCastleProvider());

        // CA tijelo koje izdaje sertifikat
        String s= getCommonName(Certificate.CA);
        X500Name rootCertIssuer = new X500Name("CN="+s);

        // generisi kljuceve za korisnika

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM,BC_PROVIDER);
        keyPairGenerator.initialize(2048);

        // generisi novi sertifikat i unesi polja

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();

        String params = "C="+countryName + ", ST="+stateOrProvinceName+ ", L="+localityName
                        +", O="+organizationName+ ", OU="+organizationalUnitName+ ", CN="+player.getUsername()+", E="+player.getEmail();

        X500Name issuedCertSubject = new X500Name(params);

        BigInteger issuedCertSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));
        KeyPair issuedCertKeyPair = keyPairGenerator.generateKeyPair();

        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(issuedCertSubject, issuedCertKeyPair.getPublic());
        JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER);

        // CA potpisuje sertifikat svojim privatnim kljucem
        ContentSigner csrContentSigner = csrBuilder.build(Certificate.caKey);
        PKCS10CertificationRequest csr = p10Builder.build(csrContentSigner);

        X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(rootCertIssuer, issuedCertSerialNum, startDate, endDate, csr.getSubject(), csr.getSubjectPublicKeyInfo());

        JcaX509ExtensionUtils issuedCertExtUtils = new JcaX509ExtensionUtils();

        // postavljanje ekstenzija

        issuedCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false)); // nije CA sertifikat
        issuedCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, issuedCertExtUtils.createAuthorityKeyIdentifier(Certificate.CA));
        issuedCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, issuedCertExtUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo()));
        issuedCertBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyEncipherment));
        issuedCertBuilder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

        // izdavanje sertifikata
        X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(csrContentSigner);
        X509Certificate issuedCert  = new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(issuedCertHolder);

        // CA verifikuje sertifikat svojim javnim kljucem
        issuedCert.verify(Certificate.CA.getPublicKey(), BC_PROVIDER);

        writeCertToFile(issuedCert, outputDir.getAbsolutePath()+File.separator+player.getUsername()+".cer");
//        exportKeyPairToKeystoreFile(issuedCertKeyPair, issuedCert, username, username+".pfx", "PKCS12", "password");

    }
    static void writeCertToFile(X509Certificate certificate, String fileName) throws Exception {
        FileOutputStream certificateOut = new FileOutputStream(new File(fileName));
        certificateOut.write("-----BEGIN CERTIFICATE-----\n".getBytes());
        certificateOut.write(Base64.encode(certificate.getEncoded()));
        certificateOut.write("\n-----END CERTIFICATE-----".getBytes());
        certificateOut.close();
    }
//    static void exportKeyPairToKeystoreFile(KeyPair keyPair, Certificate certificate, String alias, String fileName, String storeType, String storePass) throws Exception {
//        KeyStore sslKeyStore = KeyStore.getInstance(storeType, BC_PROVIDER);
//        sslKeyStore.load(null, null);
////        sslKeyStore.setKeyEntry(alias, keyPair.getPrivate(),null, new Certificate[]{certificate});
////        FileOutputStream keyStoreOs = new FileOutputStream();
////        sslKeyStore.store(keyStoreOs, storePass.toCharArray());
//    }

    public static String getCommonName(X509Certificate certificate) throws Exception{
        X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];
        return IETFUtils.valueToString(cn.getFirst().getValue());
    }

    public static X509Certificate loadUserCertificate(FileInputStream fis) throws Exception{
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return  (X509Certificate)certificateFactory.generateCertificate(fis);
    }
}
