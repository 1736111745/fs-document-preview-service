package com.facishare.document.preview.cgi.utils;

import application.dcs.Convert;

import java.util.UUID;

/**
 * Created by liuq on 16/8/10.
 * 本算法利用62个可打印字符，通过随机生成32位UUID，由于UUID都为十六进制，所以将UUID分成8组，每4个为一组，然后通过模62操作，结果作为索引取出字符
 */
public class SampleUUID {
    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };


    public static String getUUID() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString().toLowerCase();
    }
    private final static String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private final static String configDir = root + "yozo_config";
    public static void main(String[] args) {
        Convert convert = new Convert(configDir);
        convert.setAcceptTracks(true);
        //convert.setTempPath(new PathHelper().getConvertTempPath());
        //convert.setAutoDeleteTempFiles(true);
        convert.setHtmlTitle("文档预览");
        convert.setShowTitle(true);
        convert.setShowPic(true);
        convert.setHtmlEncoding("UTF-8");
        convert.setConvertForPhone(true);
        //convert.setAutoDeleteTempFiles(true);
        convert.convertMStoHtmlOfSvg("/Users/liuq/Downloads/a.doc","/Users/liuq/temp/bbb.html");
    }
}
