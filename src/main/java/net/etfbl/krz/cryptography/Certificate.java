package net.etfbl.krz.cryptography;

import net.etfbl.krz.cryptoquiz.Main;
import net.etfbl.krz.model.Player;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jcajce.provider.asymmetric.X509;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
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

    public static final String BC_PROVIDER = "BC";
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

            CACertificate ca1 = getCACertificate(new File(Main.caDir+"ca1.jks"),"sigurnost","ca1");
            ca1.setId(1);
            CACertificate ca2 = getCACertificate(new File(Main.caDir+"ca2.jks"),"sigurnost","ca2");
            ca2.setId(2);

            ca.add(ca1);
            ca.add(ca2);


//            ** Koristeno prilikom generisanja keystorea

//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            FileInputStream inStream = new FileInputStream(caDir+"ca1.crt");
//            File keyReader = new File(privDir+"ca1_pkcs8.key");
//            CACertificate ca1 = new CACertificate(inStream,keyReader,1);
//            ca.add(ca1);
//
//            createKeyStore("ca1","sigurnost",new File("src/main/resources/ca"),ca1.getCertificate(),ca1.getPrivateKey());
//
//            inStream = new FileInputStream(caDir+"ca2.crt");
//            keyReader = new File(privDir+"ca2_pkcs8.key");
//            CACertificate ca2 = new CACertificate(inStream,keyReader,2);
//            createKeyStore("ca2","sigurnost",new File("src/main/resources/ca"),ca2.getCertificate(),ca2.getPrivateKey());
//
//            ca.add(ca2);
//           inStream.close();

            System.out.println("CA Tijela ucitana! " );

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
        issuedCertBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature));
        issuedCertBuilder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

        // izdavanje sertifikata
        X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(csrContentSigner);
        X509Certificate issuedCert  = new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(issuedCertHolder);

        // CA verifikuje sertifikat svojim javnim kljucem
        issuedCert.verify(Certificate.CA.getPublicKey(), BC_PROVIDER);


//        writeKeyToFile(issuedCertKeyPair.getPrivate(),outputDir.getAbsolutePath()+File.separator+player.getUsername()+"Private.key");

        String desktop = System.getProperty("user.home") + "/Desktop";

        writeCertToFile(issuedCert,desktop+File.separator+player.getUsername()+".cer");
        createKeyStore(player.getUsername(),player.getPassword(),outputDir,issuedCert, issuedCertKeyPair.getPrivate());
    }

    static void writeCertToFile(X509Certificate certificate, String fileName) throws Exception {
        FileOutputStream certificateOut = new FileOutputStream(new File(fileName));
        certificateOut.write("-----BEGIN CERTIFICATE-----\n".getBytes());
        certificateOut.write(Base64.encode(certificate.getEncoded()));
        certificateOut.write("\n-----END CERTIFICATE-----".getBytes());
        certificateOut.close();
    }

    public static void writeKeyToFile(PrivateKey key,String filename) throws Exception{

        String str = "-----BEGIN PRIVATE KEY-----\n";
        str+= java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        str+="\n-----END PRIVATE KEY-----\n";

        FileOutputStream fis = new FileOutputStream(new File(filename));
        fis.write(str.getBytes(StandardCharsets.UTF_8));
        fis.close();
    }

    public static void createKeyStore(String alias, String password, File outputDir, X509Certificate issuedCertificate, PrivateKey privateKey) throws Exception{
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        System.out.println("Create Key Store");

        char[] pwdArray = password.toCharArray();
        ks.load(null,pwdArray);
        ks.setKeyEntry(alias,privateKey,pwdArray,new java.security.cert.Certificate[]{issuedCertificate});

        FileOutputStream fos = new FileOutputStream(outputDir.getAbsolutePath()+File.separator+alias+".jks");
        ks.store(fos,pwdArray);

        System.out.println("Keystore created");
    }

    // za citanje CA tijela iz keystore-a
    public static CACertificate getCACertificate(File file,String password,String username) throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");

        System.out.println("Read keystore:");
        char[] pwdArray = password.toCharArray();
        ks.load(new FileInputStream(file),pwdArray);

        CACertificate ca = new CACertificate();

        if(ks.containsAlias(username)){
            Key key = ks.getKey(username,pwdArray);
            ca.setPrivateKey((RSAPrivateKey) key);

            java.security.cert.Certificate certificate = ks.getCertificate(username);
            ca.setCertificate((X509Certificate) certificate);
        }

        return  ca;
    }

    public static X509Certificate getUserCertificate(File keystore, String password, String username) throws Exception{
        KeyStore ks = KeyStore.getInstance("JKS");

        char[] pwdArray = password.toCharArray();
        ks.load(new FileInputStream(keystore),pwdArray);

        java.security.cert.Certificate certificate = ks.getCertificate(username);
        return (X509Certificate) certificate;
    }

    public static KeyPair getUserKeyPair(File keystore, String password, String username) throws Exception{

        KeyStore ks = KeyStore.getInstance("JKS");
        char[] pwdArray = password.toCharArray();
        ks.load(new FileInputStream(keystore),pwdArray);

        PrivateKey privateKey = (PrivateKey) ks.getKey(username,pwdArray);
        java.security.cert.Certificate certificate = ks.getCertificate(username);

        return new KeyPair(certificate.getPublicKey(),privateKey);
    }

    public static String getCommonName(X509Certificate certificate) throws Exception{
        X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];
        return IETFUtils.valueToString(cn.getFirst().getValue());
    }

    public static String getEmail(X509Certificate certificate) throws Exception{
        X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.E)[0];
        return IETFUtils.valueToString(cn.getFirst().getValue());
    }

    public static X509Certificate loadUserCertificate(FileInputStream fis) throws Exception{
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return  (X509Certificate)certificateFactory.generateCertificate(fis);
    }

    public static CACertificate getIssuerCertificate(int id){
        for (CACertificate cert : ca){
            if(cert.getId()==id) {
               return cert;
            }
        }
        return null;
    }

    public static void revokeCertificate(X509Certificate revokedCert , String list,CACertificate issuerCertificate) throws Exception{

        X509CRLParser crlParser = new X509CRLParser();
        crlParser.engineInit(new FileInputStream(Main.caDir+list));
        X509CRL crl = (X509CRL) crlParser.engineRead();

        X500Name rootCertIssuer = new JcaX509CertificateHolder(issuerCertificate.getCertificate()).getSubject();
        X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(rootCertIssuer,new Date());
        crlBuilder.addCRLEntry(revokedCert.getSerialNumber(), new Date(), CRLReason.CESSATION_OF_OPERATION);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        crlBuilder.setNextUpdate(endDate);

        crlBuilder.addCRL(new X509CRLHolder(crl.getEncoded()));

        JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER);
        ContentSigner contentSigner = csrBuilder.build(issuerCertificate.getPrivateKey());

        X509CRLHolder c = crlBuilder.build(contentSigner);

        String str = "-----BEGIN X509 CRL-----\n";
        str+= java.util.Base64.getEncoder().encodeToString(c.getEncoded());
        str+="\n-----END X509 CRL-----\n";

        FileOutputStream fis = new FileOutputStream(new File(Main.caDir+list));
        fis.write(str.getBytes(StandardCharsets.UTF_8));
        fis.close();
    }

    public static boolean checkCRL(String list , X509Certificate certificate){
        System.out.println("Provjerava se lista: " + list);
        try (InputStream inStream = new FileInputStream(Main.caDir+list)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL)cf.generateCRL(inStream);

            X509CRLEntry revokedCertificate = crl.getRevokedCertificate(certificate.getSerialNumber());
            if(revokedCertificate!=null)
                return true;
        } catch (Exception e ){
            e.printStackTrace();
            return  false;
        }
        return  false;
    }

}
