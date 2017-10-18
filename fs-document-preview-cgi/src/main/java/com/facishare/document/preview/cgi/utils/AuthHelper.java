package com.facishare.document.preview.cgi.utils;


import com.facishare.asm.api.auth.AuthXC;
import com.facishare.asm.api.model.CookieToAuth;
import com.facishare.asm.api.service.ActiveSessionAuthorizeService;
import com.facishare.common.web.util.WebUtil;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.github.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by liuq on 16/3/29.
 */
@Slf4j
public class AuthHelper {
    @Autowired
    ActiveSessionAuthorizeService assService;

    public EmployeeInfo getAuthInfo(HttpServletRequest request) {
        String cookieValue = getCookie(request);
        if ("".equals(cookieValue)) {
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
            String uid = authXC.getEnterpriseAccount() + '.' + authXC.getEmployeeId();
            TraceContext.get().setUid(uid);
            MDC.put("userId", uid);
            return employeeInfo;
        } else {
          return null;
        }
    }

    public static String getCookie(HttpServletRequest request) {
        Cookie cookie = WebUtil.getCookie(request, "FSAuthXC");
        if (cookie == null) {
          cookie = WebUtil.getCookie(request, "FSAuthX");
        }
        if (cookie != null) {
          return cookie.getValue();
        }
        //log.warn("[authorizeByCookieValue] [fail] [can't find FSAuthX/FsAuthXC cookie] [cookie:{}] [cookieInHeader:{}]", Arrays.toString(request.getCookies()), request.getHeader("cookie"));
        return "";
    }
}
