package net.etfbl.krz.cryptography;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class SecurityUtil {

    public static String hashFunction(String user){
        String hash="";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] text = user.getBytes(StandardCharsets.UTF_8);

            text = messageDigest.digest(text);
            hash = Base64.getEncoder().encodeToString(text);


        } catch (Exception e){
            e.printStackTrace();
        }
        return hash;
    }
}
