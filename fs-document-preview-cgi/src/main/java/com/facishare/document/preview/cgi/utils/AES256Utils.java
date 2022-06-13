package com.facishare.document.preview.cgi.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created by Aaron on 16/7/4.
 */
public class AES256Utils {
  public static final String aesKey = "nirtHUNF/Ct8J7sf40VaIQui0N5r8gcbxGXKxRhu1C4=";
  public static final String aesIv = "jwNz4Ia8OHVpPyEXIQjJ2g==";
  private static byte[] keyBytes;
  private static byte[] ivBytes;
  public static Cipher decrypt_c;
  public static Cipher encrypt_c;
  public static Base64.Decoder decoder = Base64.getDecoder();

  static {
    try {
      keyBytes = decoder.decode(aesKey);
      ivBytes = decoder.decode(aesIv);
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
    System.out.println(Base64.getEncoder().encode("com.facishare.fsc.common.utils.AES256Utils".getBytes()));
    System.out.println(decode("A1A96F88FD9368B762A6E9E987788676238EF79F0D8546F10E5B6E25B9E091E776ADD00876CE6EAC78A0F61FF1836386D89A1728CC9EA902889D74276D563E411" +
      "E4D3672FF07393086438FEF48E770D2"));
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
    if (hexStr.length() < 1) {
      return null;
    }
    byte[] result = new byte[hexStr.length() / 2];
    for (int i = 0; i < hexStr.length() / 2; i++) {
      int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
      int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
      result[i] = (byte) (high * 16 + low);
    }
    return result;
  }

}
