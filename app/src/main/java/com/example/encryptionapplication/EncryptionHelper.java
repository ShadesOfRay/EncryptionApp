package com.example.encryptionapplication;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.net.Uri;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionHelper {

    private static KeyStore keyStore = null;
    private static final String STORE_NAME = "AndroidKeyStore";
    //reads the image in as a byte string
    //hopefully the uri doesnt return something sour
    public static byte[] encryptGivenFile(String id, Uri uri, ContentResolver contentResolver, SharedPreferences.Editor editor) {
        File img = new File(uri.getPath());
        System.out.println(uri.getPath());
        InputStream in;
        byte[] byteString = null;
        byte[] encryptedOut = null;
        try {
            //in = new FileInputStream(img);
            in = contentResolver.openInputStream(uri);
            byteString = new byte[in.available()];
            in.read(byteString);
            in.close();

            keyStore = KeyStore.getInstance(STORE_NAME);
            keyStore.load(null);

            KeyGenerator keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, STORE_NAME);
            keyGen.init(new KeyGenParameterSpec.Builder(id,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .setKeySize(256)
                    .build());

            keyGen.generateKey();

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(id,null));
            AlgorithmParameters params = cipher.getParameters();
            System.out.println("tag length is: " + params.getParameterSpec(GCMParameterSpec.class).getTLen());
            byte[] encryptionIv = params.getParameterSpec(GCMParameterSpec.class).getIV();
            //byte[] encryptionIv = cipher.getIV();
            String ivString = Base64.encodeToString(encryptionIv, Base64.DEFAULT);
            editor.putString(id, ivString);
            editor.commit();
            encryptedOut = cipher.doFinal(byteString);


        } catch (Exception e){
            e.printStackTrace();
        }
        return encryptedOut;
    }

    public static byte[] decryptGivenFile(String id, Uri uri, ContentResolver contentResolver, String iv){
        InputStream in;
        byte[] byteString;
        byte[] decryptedOut = null;

        try{
            in = contentResolver.openInputStream(uri);
            byteString = new byte[in.available()];
            in.read(byteString);
            in.close();

            keyStore = KeyStore.getInstance(STORE_NAME);
            keyStore.load(null);

            byte[] ivBytes = Base64.decode(iv, Base64.DEFAULT);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keyStore.getKey(id,null), new GCMParameterSpec(128,ivBytes));
            decryptedOut = cipher.doFinal(byteString);

        } catch (Exception e){
            e.printStackTrace();
        }

        //Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        //cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(id,null));
        //AlgorithmParameters params = cipher.getParameters();
        //byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        //encryptedOut = cipher.doFinal(byteString);

        return decryptedOut;
    }
}
