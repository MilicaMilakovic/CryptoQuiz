package net.etfbl.krz.cryptography;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class SecurityUtil {

    public static String hashFunction(String user){
        String hash="";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] text = user.getBytes(StandardCharsets.UTF_8);

            text = messageDigest.digest(text);
            hash = Base64.getEncoder().encodeToString(text);
            hash = hash.replace("/","%2F");

        } catch (Exception e){
            e.printStackTrace();
        }
        return hash;
    }

    public static void asymmetricEncription(File file, PublicKey key) throws Exception{
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(fileBytes);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(encrypted);
        fileOutputStream.close();
    }

    public static void asymmetricDecription(File file, PrivateKey key) throws Exception{
        byte[] encrypted = Files.readAllBytes(file.toPath());
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,key);

        byte[] decrypted = cipher.doFinal(encrypted);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(decrypted);
        fileOutputStream.close();
    }





}
