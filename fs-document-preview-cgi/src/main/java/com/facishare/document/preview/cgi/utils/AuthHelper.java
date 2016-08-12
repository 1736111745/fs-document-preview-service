package com.facishare.document.preview.cgi.utils;


import com.facishare.common.web.auth.FSAuthDecryptedResult;
import com.facishare.common.web.auth.FSAuthTicketDecryptor;
import com.facishare.common.web.util.WebUtil;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by liuq on 16/3/29.
 */
public class AuthHelper {

    static Logger logger = LoggerFactory.getLogger(AuthHelper.class);
    private static final String aesKey = "nirtHUNF/Ct8J7sf40VaIQui0N5r8gcbxGXKxRhu1C4=";
    private static final String aesIv = "jwNz4Ia8OHVpPyEXIQjJ2g==";
    private static FSAuthTicketDecryptor fsAuthTicketDecryptor = new FSAuthTicketDecryptor(aesKey, aesIv);

    public static EmployeeInfo getAuthinfo(HttpServletRequest request) {
        String cookieValue = getCookie(request);
        if (cookieValue.equals("")) {
            return null;
        }
        FSAuthDecryptedResult result = fsAuthTicketDecryptor.decryptAuthTicketString(cookieValue);
        if (!result.getStatus().equals(FSAuthDecryptedResult.FSAuthDescryptedStatus.Success)) {
            logger.warn("[authorizeByCookieValue] [fail] [result:{}] [cookieValue:{}]", result, cookieValue);
            return null;
        }
        String[] tokens = result.getPlainData().split("\\|");
        String enterpriseAccount = tokens[0];
        int enterpriseId = Integer.parseInt(tokens[1]);
        int employeeId = Integer.parseInt(tokens[2]);
        String employeeAccount = tokens[3];
        String employeeName = tokens[4];
        String employeeFullName = tokens[5];
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setEa(enterpriseAccount);
        employeeInfo.setEi(enterpriseId);
        employeeInfo.setEmployeeAccount(employeeAccount);
        employeeInfo.setEmployeeFullName(employeeFullName);
        employeeInfo.setEmployeeId(employeeId);
        employeeInfo.setEmployeeName(employeeName);
        return employeeInfo;
    }

    private static String getCookie(HttpServletRequest request) {
        Cookie cookie = WebUtil.getCookie(request, "FSAuthX");
        if (cookie == null) {
            cookie = WebUtil.getCookie(request, "FSAuthXC");
        }
        if (cookie == null) {
            logger.warn("[authorizeByCookieValue] [fail] [can't find FSAuthX/FsAuthXC cookie] [cookie:{}] [cookieInHeader:{}]", Arrays.toString(request.getCookies()), request.getHeader("cookie"));
            return "";
        } else
            return cookie.getValue();
    }
}
