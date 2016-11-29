package com.facishare.document.preview.cgi.utils;


import com.facishare.asm.api.auth.AuthXC;
import com.facishare.asm.api.model.CookieToAuth;
import com.facishare.asm.api.service.ActiveSessionAuthorizeService;
import com.facishare.common.web.util.WebUtil;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by liuq on 16/3/29.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
public class AuthHelper {
    @Autowired
    ActiveSessionAuthorizeService assService;
    private static final Logger LOG = LoggerFactory.getLogger(AuthHelper.class);

    public EmployeeInfo getAuthInfo(HttpServletRequest request) {
        String cookieValue = getCookie(request);
        if (cookieValue.equals("")) {
            return null;
        }
        CookieToAuth.Argument arg = new CookieToAuth.Argument();
        arg.setCookie(cookieValue);
        String realIp = request.getHeader("X-Real-IP");
        arg.setIp(realIp);
        CookieToAuth.Result<AuthXC> result = assService.cookieToAuthXC(arg);
        if (result != null && result.getBody() != null) {
            AuthXC authXC = result.getBody();
            EmployeeInfo employeeInfo = new EmployeeInfo();
            employeeInfo.setEa(authXC.getEnterpriseAccount());
            employeeInfo.setEi(authXC.getEmployeeId());
            employeeInfo.setEmployeeAccount(authXC.getAccount());
            employeeInfo.setEmployeeFullName(authXC.getFullName());
            employeeInfo.setEmployeeId(authXC.getEmployeeId());
            employeeInfo.setEmployeeName(authXC.getName());
            return employeeInfo;
        } else
            return null;
    }

    private static String getCookie(HttpServletRequest request) {
        Cookie cookie = WebUtil.getCookie(request, "FSAuthX");
        if (cookie == null) {
            cookie = WebUtil.getCookie(request, "FSAuthXC");
        }
        if (cookie == null) {
            LOG.warn("[authorizeByCookieValue] [fail] [can't find FSAuthX/FsAuthXC cookie] [cookie:{}] [cookieInHeader:{}]", Arrays.toString(request.getCookies()), request.getHeader("cookie"));
            return "";
        } else
            return cookie.getValue();
    }
}
