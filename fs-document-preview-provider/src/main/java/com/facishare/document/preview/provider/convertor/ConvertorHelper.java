package com.facishare.document.preview.provider.convertor;

import application.dcs.IHtmlConvertor;
import application.dcs.IPICConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;


/**
 * Created by liuq on 2016/11/9.
 */
@Slf4j
public class ConvertorHelper {
    public ConvertorHelper() throws Exception {
    }

    public static String toSvg(int page1, int page2, String filePath, String baseDir) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = convertobj.convertor.convertMStoPic(filePath);
            if (ipicConvertor != null) {
                int resultCode = ipicConvertor.resultCode();
                if (resultCode == 0) {
                    String fileName = (page1 + 1) + ".svg";
                    String svgFilePath = FilenameUtils.concat(baseDir, fileName);
                    ipicConvertor.convertToSVG(page1, page2, 1.0f, baseDir);
                    ipicConvertor.close();
                    File file = new File(svgFilePath);
                    if (file.exists()) {
                        return svgFilePath;
                    } else {
                        return "";
                    }
                } else {
                    log.warn("filePath:{},resultCode:{}", filePath, resultCode);
                    return "";
                }
            } else {
                log.warn("converter is null");
                return "";
            }
        } catch (Exception e) {
            log.error("toSvg,filepath:{}", filePath, e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    public static String toPng(int page1, int page2, String filePath, String baseDir, int startIndex, int type) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = type == 1 ?
                    convertobj.convertor.convertMStoPic(filePath) :
                    convertobj.convertor.convertPdftoPic(filePath);
            int resultCode = ipicConvertor.resultCode();
            if (resultCode == 0) {
                String fileName = (page1 + startIndex) + ".png";
                String pngFilePath = baseDir + "/" + fileName;
                ipicConvertor.convertToPNG(page1, page2, 2f, baseDir);
                ipicConvertor.close();
                File file = new File(pngFilePath);
                if (file.exists()) {
                    return pngFilePath;
                } else {
                    return "";
                }
            } else {
                log.warn("filePath:{},pageIndex:{},resultCode:{}", filePath, page1, resultCode);
                return "";
            }
        } catch (Exception e) {
            log.error("toPng,filepath:{}", filePath, e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    public static String toHtml(int page1, String filePath, String baseDir) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IHtmlConvertor htmlConvertor = convertobj.convertor.convertMStoHtml(filePath);
            int resultCode = htmlConvertor.resultCode();
            if (resultCode == 0) {
                htmlConvertor.setNormal(true);
                String fileName = (page1 + 1) + ".html";
                String htmlFilePath = baseDir + "/" + fileName;
                htmlConvertor.convertToHtml(htmlFilePath, page1);
                htmlConvertor.close();
                File file = new File(htmlFilePath);
                if (file.exists()) {
                    return htmlFilePath;
                } else {
                    return "";
                }
            } else {
                log.warn("resultcode:{}", resultCode);
                return "";
            }
        } catch (Exception e) {
            log.error("toHtml,filepath:{}", filePath, e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    public static void main(String[] args) throws Exception {
//        String mongoServer = "mongodb://app_globaluser:A1366A48F63C5BD86DEDC20253B210137D2327C561B46729357EB373DD8F89C2@vlnx112044001.x.firstshare.cn:27017,vlnx112046001.x.firstshare.cn:27017,vlnx112045001.x.firstshare.cn:27017";
//        Preconditions.checkNotNull(mongoServer, "The servers configuration is incorrect!");
//        //如果密码是加密的,那就做解密处理
//        Pattern p = Pattern.compile("mongodb://((.+):(.*)@)");
//        Matcher m = p.matcher(mongoServer);
//        if (m.find()) {
//            try {
//                String pwd = UrlEscapers.urlFormParameterEscaper().escape(PasswordUtil.decode(m.group(3)));
//                mongoServer = mongoServer.substring(0, m.end(2) + 1) + pwd + mongoServer.substring(m.end(1) - 1);
//            } catch (Exception e) {
//               // log.error("cannot decode " + m.group(3), e);
//            }
//        }

//        String path="/Users/liuq/Downloads/【纷享公开课】高效时间管理.pptx";
//        String path1="/Users/liuq/Downloads";
//        byte[] bytes= FileUtils.readFileToByteArray(new File(path));
//        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
//
//        for(int i=0;i<5;i++) {
//            ExecutorService executorService = Executors.newSingleThreadExecutor();
//            int finalI = i;
//            int finalI1 = i;
//            System.out.println("convert:"+finalI);
//            executorService.execute(() ->
//                  toPng(1, 1,path,path1,1,1));
//        }

    }
}
