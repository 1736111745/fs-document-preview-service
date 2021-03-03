package com.facishare.document.preview.cgi.utils;


import com.facishare.asm.api.auth.AuthXC;
import com.facishare.asm.api.model.CookieToAuth;
import com.facishare.asm.api.service.ActiveSessionAuthorizeService;
import com.facishare.common.web.util.WebUtil;
import com.facishare.converter.EIEAConverter;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.fxiaoke.common.Guard;
import com.fxiaoke.enterpriserelation.arg.AuthWithoutEaArg;
import com.fxiaoke.enterpriserelation.common.HeaderObj;
import com.fxiaoke.enterpriserelation.common.RestResult;
import com.fxiaoke.enterpriserelation.result.AuthUserResult;
import com.fxiaoke.enterpriserelation.service.AuthService;
import com.github.autoconf.ConfigFactory;
import com.github.trace.TraceContext;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuq on 16/3/29.
 */
@Slf4j
public class AuthHelper {
  @Autowired
  ActiveSessionAuthorizeService assService;

  @Autowired
  AuthService authService;
  @Autowired
  EIEAConverter eieaConverter;
  private String biFileEncryptKey = "";

  @PostConstruct
  public void init() {
    ConfigFactory.getInstance().getConfig("fs-bi-file-config", config -> {
      biFileEncryptKey = config.get("biFileEncryptKey");
    });
  }

  public EmployeeInfo getAuthInfo(HttpServletRequest request) {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    EmployeeInfo employeeInfo = null;
    String outappid = getOutAppId(request);
    if (!Strings.isNullOrEmpty(outappid) || path.contains(":")) {
      employeeInfo = getAuthInfoForOpen(request, outappid);
    }
    if (employeeInfo != null) {
      return employeeInfo;
    } else {
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
        employeeInfo = new EmployeeInfo();
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
  }

  public static String getCookie(HttpServletRequest request) {
    Cookie cookie = WebUtil.getCookie(request, "FSAuthXC");
    if (cookie == null) {
      cookie = WebUtil.getCookie(request, "FSAuthX");
    }
    if (cookie != null) {
      return cookie.getValue();
    }
    return "";
  }


  public EmployeeInfo getOAuthInfo(String cookie, String appId) {
    AuthWithoutEaArg arg = new AuthWithoutEaArg();
    arg.setErInfo(cookie);
    arg.setLinkAppId(appId);
    RestResult<AuthUserResult> result = authService.authWithoutEa(HeaderObj.newInstance(appId, null, null, null), arg);
    if (!result.isSuccess()) {
      log.warn("get the auth message error,appId :{}", appId);
      return null;
    }
    EmployeeInfo employeeInfo = new EmployeeInfo();
    String ea = result.getData().getUpstreamEa();
    int employeeId = 1000;
    employeeInfo.setEa(ea);
    employeeInfo.setEmployeeId(employeeId);
    return employeeInfo;
  }

  //下游的企业换成上游的企业
  public EmployeeInfo getAuthInfoForOpen(HttpServletRequest request, String appid) {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    if (path.contains(":")) {
      EmployeeInfo employeeInfo = new EmployeeInfo();
      String[] appIdAndNpath = path.split(":");
      String encryptEI = appIdAndNpath[0];
      com.fxiaoke.common.Guard guard = new Guard(biFileEncryptKey);
      try {
        int ei = Integer.parseInt(guard.decode(encryptEI));
        String ea = eieaConverter.enterpriseIdToAccount(ei);
        employeeInfo.setEa(ea);
        employeeInfo.setEmployeeId(1000);
        employeeInfo.setEi(ei);
      } catch (Exception e) {
        log.error("decrypt ei happened exception", e);
      }
      return employeeInfo;
    } else {
      EmployeeInfo employeeInfo = null;
      String erInfo =
        WebUtil.getCookie(request, "ERInfo") == null ? "" : WebUtil.getCookie(request, "ERInfo").getValue();
      String crInfo =
        WebUtil.getCookie(request, "CRInfo") == null ? "" : WebUtil.getCookie(request, "CRInfo").getValue();
      if (!Strings.isNullOrEmpty(erInfo) || !Strings.isNullOrEmpty(crInfo)) {
        String cookie = erInfo;
        if (Strings.isNullOrEmpty(erInfo)) {
          cookie = crInfo;
        }
        employeeInfo = getOAuthInfo(cookie, appid);
      }
      return employeeInfo;
    }
  }

  private String getOutAppId(HttpServletRequest request) {
    String outappid = UrlParametersHelper.safeGetRequestParameter(request, "outappid");
    if (Strings.isNullOrEmpty(outappid)) {
      outappid = request.getHeader("fs-out-appid");
    }
    return outappid;
  }
}
