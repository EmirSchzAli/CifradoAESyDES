package com.ea.dc.cifrado.aes.des.Metodos;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MetodosDES {

    public String encriptar(String mensaje, String llave) {
        // encrypt the message
        byte[] encryptedMsg = encryptSMS(llave, mensaje);
        // convert the byte array to hex format in order for
        // transmission
        //return byte2hex(encryptedMsg);
        return Base64.encodeToString(encryptedMsg, Base64.DEFAULT);
    }

    public String desencriptar(String mensajeEcriptado, String llave) throws Exception {
        String mensaje = "";
        byte[] msg = Base64.decode(mensajeEcriptado, Base64.DEFAULT);
        // decrypt the byte array
        byte[] result = decryptSMS(llave, msg);
        mensaje = new String(result);
        return mensaje;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;
        }
        return hs.toUpperCase();
    }

    // utility function
    public static byte[] encryptSMS(String secretKeyString, String msgContentString) {
        try {
            byte[] returnArray;
            // generate AES secret key from user input
            Key key = generateKey(secretKeyString);
            // specify the cipher algorithm using AES
            Cipher c = Cipher.getInstance("DES");
            // specify the encryption mode
            c.init(Cipher.ENCRYPT_MODE, key);
            // encrypt
            returnArray = c.doFinal(msgContentString.getBytes());
            return returnArray;
        } catch (Exception e) {
            e.printStackTrace();
            byte[] returnArray = null;
            return returnArray;
        }
    }


    private static Key generateKey(String secretKeyString) throws Exception {
        // generate secret key from string
        Key key = new SecretKeySpec(secretKeyString.getBytes(), "DES");
        return key;
    }

    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("hello");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }
    // decryption function

    public static byte[] decryptSMS(String secretKeyString, byte[] encryptedMsg) throws Exception {
        // generate AES secret key from the user input secret key
        Key key = generateKey(secretKeyString);
        // get the cipher algorithm for AES
        Cipher c = Cipher.getInstance("DES");
        // specify the decryption mode
        c.init(Cipher.DECRYPT_MODE, key);
        // decrypt the message
        byte[] decValue = c.doFinal(encryptedMsg);
        return decValue;
    }

}
