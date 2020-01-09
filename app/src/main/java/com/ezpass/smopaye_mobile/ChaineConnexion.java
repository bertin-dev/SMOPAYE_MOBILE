package com.ezpass.smopaye_mobile;

import android.util.Log;

import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ChaineConnexion {


    //private static final String adresseURLsmopayeServer = "https://cm.secure-ws-api-smp-excecute.smopaye.fr/index.php";
    private static final String adresseURLsmopayeServer = "https://webservice.domaine.tests.space.smopaye.fr";
    private static final String adresseURLGoogleAPI = "https://fcm.googleapis.com/";
    private static final String urlSiteWeb = "https://smopaye.cm/";
    private static final String encrypted_password = "Iyz4BVU2Hlt0cIeIPBlB7Wq15kMDI4NGRmOTNi";
    private static final String salt = "d0284df93b";
    private static final String security_keys = "56ZS5PQ1RF-eyJsaWNlbnNlSWQiOiI1NlpGVkIjpmYWxzZX0+-==";

    public static String getAdresseURLsmopayeServer() {
        return adresseURLsmopayeServer;
    }

    public static String getAdresseURLGoogleAPI() {
        return adresseURLGoogleAPI;
    }

    public static String getUrlSiteWeb() {
        return urlSiteWeb;
    }

    public static String getEncrypted_password() {
        return encrypted_password;
    }

    public static String getSalt() {
        return salt;
    }

    public static String getsecurity_keys() {
        return security_keys;
    }







        /*//Encryption test
        String string = "bonjour";
        byte[] bytes = string.getBytes();
        HashMap<String, byte[]> map = encryptBytes(bytes, "123456789");
        Toast.makeText(this, map.toString(), Toast.LENGTH_SHORT).show();

       //Decryption test
        byte[] decrypted = decryptData(map, "123456789");
        if (decrypted != null)
        {
            String decryptedString = new String(decrypted);
            Log.e("MYAPP", "Decrypted String is : " + decryptedString);
            Toast.makeText(this, decryptedString, Toast.LENGTH_SHORT).show();
        }*/



    //cryptage du qr code
    public static HashMap<String, byte[]> encryptBytes(byte[] plainTextBytes, String passwordString)
    {
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();

        try
        {
            //Random salt for next step
            SecureRandom random = new SecureRandom();
            byte salt[] = new byte[256];
            random.nextBytes(salt);

            //PBKDF2 - derive the key from the password, don't use passwords directly
            char[] passwordChar = passwordString.toCharArray(); //Turn password into char[] array
            PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256); //1324 iterations
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            //Create initialization vector for AES
            SecureRandom ivRandom = new SecureRandom(); //not caching previous seeded instance of SecureRandom
            byte[] iv = new byte[16];
            ivRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            //Encrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainTextBytes);

            map.put("salt", salt);
            map.put("iv", iv);
            map.put("encrypted", encrypted);
        }
        catch(Exception e)
        {
            Log.e("MYAPP", "encryption exception", e);
        }

        return map;
    }

    //decryptage

    public static byte[] decryptData(HashMap<String, byte[]> map, String passwordString)
    {
        byte[] decrypted = null;
        try
        {
            byte salt[] = map.get("salt");
            byte iv[] = map.get("iv");
            byte encrypted[] = map.get("encrypted");

            //regenerate key from password
            char[] passwordChar = passwordString.toCharArray();
            PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            //Decrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            decrypted = cipher.doFinal(encrypted);
        }
        catch(Exception e)
        {
            Log.e("MYAPP", "decryption exception", e);
        }

        return decrypted;
    }

}
