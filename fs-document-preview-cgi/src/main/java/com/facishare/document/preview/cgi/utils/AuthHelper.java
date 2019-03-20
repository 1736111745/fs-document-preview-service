package com.facishare.document.preview.cgi.utils;


import com.facishare.asm.api.auth.AuthXC;
import com.facishare.asm.api.model.CookieToAuth;
import com.facishare.asm.api.service.ActiveSessionAuthorizeService;
import com.facishare.common.web.util.WebUtil;
import com.facishare.converter.EIEAConverter;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.enterprise.common.constant.LinkType;
import com.facishare.enterprise.common.result.Result;
import com.github.trace.TraceContext;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.fs.enterprise.relation.outapi.service.AuthService;
import org.fs.enterprise.relation.outapi.vo.Identifier;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

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

  public EmployeeInfo getAuthInfo(HttpServletRequest request) {
    EmployeeInfo employeeInfo = null;
    String outappid = UrlParametersHelper.safeGetRequestParameter(request, "outappid");
    if (!Strings.isNullOrEmpty(outappid)) {
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

  //下游的企业换成上游的企业
  public EmployeeInfo getAuthInfoForOpen(HttpServletRequest request, String appid) {
    EmployeeInfo employeeInfo = null;
    String erInfo = WebUtil.getCookie(request, "ERInfo") == null ? "" : WebUtil.getCookie(request, "ERInfo").getValue();
    String crInfo = WebUtil.getCookie(request, "CRInfo") == null ? "" : WebUtil.getCookie(request, "CRInfo").getValue();
    if (!Strings.isNullOrEmpty(erInfo) || !Strings.isNullOrEmpty(crInfo)) {
      Integer type = LinkType.ER.getType();
      String cookie = erInfo;
      if (Strings.isNullOrEmpty(erInfo)) {
        type = LinkType.CR.getType();
        cookie = crInfo;
      }
      Result<Identifier> result = authService.getIdentifier(type, cookie, appid, "fsc-uploadforopen");
      if (result != null && result.isSuccess()) {
        int ei = result.getData().getUpstreamEi();
        long employeeId = result.getData().getOwnerId();
        String ea = eieaConverter.enterpriseIdToAccount(ei);
        employeeInfo = new EmployeeInfo();
        employeeInfo.setEa(ea);
        employeeInfo.setEi(ei);
        employeeInfo.setEmployeeId((int) employeeId);
      }
    }
    return employeeInfo;
  }

}
