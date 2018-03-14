package com.facishare.document.preview.cgi.utils;

import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsPSMDetector;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by liuq on 2017/5/9.
 */
public class EncodingDetect {
  public static void main(String[] args) throws IOException {
    String file = "/Users/liuq/Downloads/encode.txt";
    String encode = detectCharset(file);
    System.out.println(encode);
  }

  public static  String detectCharset(String filePath) throws IOException {
    int lang= nsPSMDetector.ALL;
    nsDetector detector=new nsDetector(lang);
    final String[] result = {"UTF-8"};
    detector.Init(charset -> {
      HtmlCharsetDetector.found=true;
      result[0] =charset;
    });
    FileInputStream fileInputStream= new FileInputStream(filePath);
    BufferedInputStream impBufferedInputStream=new BufferedInputStream(fileInputStream);
    byte[] buffer=new byte[1024];
    int len;
    boolean done=false;
    boolean isAscii=true;
    while((len=impBufferedInputStream.read(buffer, 0, buffer.length))!=-1)
    {

      if(isAscii)
      {
        isAscii=detector.isAscii(buffer, len);
      }
      if(!isAscii&&!done)
      {
        done=detector.DoIt(buffer, len, false);
      }
    }
    detector.DataEnd();
    return result[0];
  }

}