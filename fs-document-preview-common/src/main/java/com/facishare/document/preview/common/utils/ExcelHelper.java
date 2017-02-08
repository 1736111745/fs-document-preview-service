package com.facishare.document.preview.common.utils;

import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorksheetCollection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by liuq on 2017/2/6.
 */
public class ExcelHelper {

    public static boolean getLicense() {
        boolean result = false;
        try {
            License aposeLic = new License();
            aposeLic.setLicense(getLicenseStream());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static InputStream getLicenseStream() {
        String license = "<License>\n" +
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
        InputStream is = new ByteArrayInputStream(license.getBytes());
        return is;

    }

    public static   void  getSheets(String filePath) throws Exception {
        getLicense();
        Workbook workbook = new Workbook(filePath);
        WorksheetCollection worksheets = workbook.getWorksheets();
        System.out.println(worksheets);
    }

    public static void main(String[] args) throws Exception {

        ExcelHelper.getSheets("/Users/liuq/Downloads/副本K0342销售单2017.2.7艾菲尔骨瓜2.xlsx");
    }
}
