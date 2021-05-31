package com.ezpass.smopaye_mobile.Config;

import android.util.Log;

import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class en charge de la configuration globale des paramètres
 */

public class Global {

    public static final String URL_API = "http://wbser.cm.21052112.smopaye.cm/public/";
    public static final String adresseURLGoogleAPI = "https://fcm.googleapis.com/";
    public static final String urlSiteWeb = "https://smopaye.cm/";
    public static final String encrypted_password = "Iyz4BVU2Hlt0cIeIPBlB7Wq15kMDI4NGRmOTNi";
    public static final String salt = "d0284df93b";

    public static double rand = Math.random();
    public static final String security_keys = "56ZS5PQ1RF-eyJsaWNlbnNlSWQiOiI1NlpGVkIjpmYWxzZX0+-==" + rand;
    public static final String espace_clients = "http://espace-client.smopaye.cm";

    public static final double Latitude_marche_soa = 3.9756296;
    public static final double Longitude_marche_soa = 11.5935448;

    public static final double Latitude_soa_campus = 3.9660964;
    public static final double Longitude_soa_campus = 11.5935347;

    public static final double Latitude_omnisport = 3.8906481;
    public static final double Longitude_omnisport = 11.544921;

    public static final double Latitude_camair = 3.8654263;
    public static final double Longitude_camair = 11.5205789;


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


    /**
     * permet de crypter le code qr
     *
     * @see encryptBytes
     *
     * @param plainTextBytes
     * @param passwordString
     * @return un objet de type HashMap
     */
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
