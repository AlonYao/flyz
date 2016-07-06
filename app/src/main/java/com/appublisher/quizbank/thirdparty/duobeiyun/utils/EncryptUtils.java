package com.appublisher.quizbank.thirdparty.duobeiyun.utils;

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
        String base64DecryptKey = Constants.KEY;

        byte[] byteMi = Base64.decode(decryptString.getBytes(), Base64.DEFAULT);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(base64DecryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }
}
