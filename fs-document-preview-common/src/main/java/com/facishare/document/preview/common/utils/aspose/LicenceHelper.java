package com.facishare.document.preview.common.utils.aspose;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by liuq on 2017/4/17.
 */
public class LicenceHelper {

    private static String licenceStr = "<License>\n" +
            "  <Data>\n" +
            "    <Products>\n" +
            "      <Product>Aspose.Total for Java</Product>\n" +
            "      <Product>Aspose.Words for Java</Product>\n" +
            "    </Products>\n" +
            "    <EditionType>Enterprise</EditionType>\n" +
            "    <SubscriptionExpiry>20991231</SubscriptionExpiry>\n" +
            "    <LicenseExpiry>20991231</LicenseExpiry>\n" +
            "    <SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber>\n" +
            "  </Data>\n" +
            "  <Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature>\n" +
            "</License>";

    public static void setWordLicence() {
        InputStream is = new ByteArrayInputStream(licenceStr.getBytes());
        com.aspose.words.License license = new com.aspose.words.License();
        try {
            license.setLicense(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setExcelLicence()  {
        InputStream is = new ByteArrayInputStream(licenceStr.getBytes());
        com.aspose.cells.License license = new com.aspose.cells.License();
        license.setLicense(is);
    }

    public static void setPptLicence()  {
        InputStream is = new ByteArrayInputStream(licenceStr.getBytes());
        com.aspose.slides.License license = new com.aspose.slides.License();
        license.setLicense(is);
    }

    public static void setPdfLicence() {
        InputStream is = new ByteArrayInputStream(licenceStr.getBytes());
        com.aspose.pdf.License license = new com.aspose.pdf.License();
        try {
            license.setLicense(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAllLicence() {
        setPdfLicence();
        setWordLicence();
        setExcelLicence();
        setPptLicence();
    }

}
