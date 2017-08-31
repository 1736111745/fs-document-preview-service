package com.facishare.document.preview.cgi.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Created by Aaron on 16/7/4.
 */
public class AES256Utils {
    public static final String aesKey = "nirtHUNF/Ct8J7sf40VaIQui0N5r8gcbxGXKxRhu1C4=";
    public static final String aesIv = "jwNz4Ia8OHVpPyEXIQjJ2g==";
    private static byte[] keyBytes = null;
    private static byte[] ivBytes = null;
    public static Cipher decrypt_c = null;
    public static Cipher encrypt_c = null;

    static {
        try {
            keyBytes = (new BASE64Decoder()).decodeBuffer(aesKey);
            ivBytes = (new BASE64Decoder()).decodeBuffer(aesIv);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            decrypt_c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encrypt_c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            decrypt_c.init(Cipher.DECRYPT_MODE, key, iv, new SecureRandom(keyBytes));
            encrypt_c.init(Cipher.ENCRYPT_MODE, key, iv, new SecureRandom(keyBytes));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(new BASE64Encoder().encode("com.facishare.fsc.common.utils.AES256Utils".getBytes()));
    }

    public static String encode(String source) {
        try {
            byte[] resultByte = encrypt_c.doFinal(source.getBytes("utf-8"));
            return parseByte2HexStr(resultByte);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String source) {
        String result = null;
        try {
            byte[] resultByte = decrypt_c.doFinal(parseHexStr2Byte(source));
            result = new String(resultByte, "utf-8");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}