package com.duobeiyun.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils
{
    private static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};

    public static String decryptDES(String decryptString, String decryptKey)
            throws Exception
    {
        String base = decryptKey + "c78fc00ce2304a1dac035fbb2e4cfa21";

        String base64DecryptKey = new String(Base64.encode(base.getBytes(),  Base64.DEFAULT),"UTF-8").substring(0, 8);

        byte[] byteMi = Base64.decode(decryptString.getBytes(), Base64.DEFAULT);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(base64DecryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }
}
