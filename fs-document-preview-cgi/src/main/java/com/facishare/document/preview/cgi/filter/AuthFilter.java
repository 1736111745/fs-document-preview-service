package com.facishare.document.preview.cgi.filter;

import com.facishare.common.web.util.WebUtil;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.ShareTokenParamInfo;
import com.facishare.document.preview.cgi.utils.AuthHelper;
import com.facishare.document.preview.cgi.utils.ShareTokenUtil;
import com.facishare.document.preview.cgi.utils.UrlParametersHelper;
import com.fxiaoke.common.Guard;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by liuq on 16/8/15.
 */
@Slf4j
@Component
public class AuthFilter extends OncePerRequestFilter {
  @Autowired
  AuthHelper authHelper;
  @ReloadableProperty("authTempKey")
  private String authTempKey = "~]Ec5SrXX<.557uf";

  private @Context
  HttpServletRequest request;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    request.setAttribute("sv", "v5.3.4");
    String requestUri = request.getRequestURI().toLowerCase();
    if (ignoreAuth(request)) {
      filterChain.doFilter(request, response);
    } else {
      EmployeeInfo employeeInfo = null;
      String shareToken = UrlParametersHelper.safeGetRequestParameter(request, "shareToken");
      ShareTokenParamInfo shareTokenParamInfo;
      if (!Strings.isNullOrEmpty(shareToken)) {
        shareTokenParamInfo = ShareTokenUtil.convertToken2ParamInfo(shareToken);
        log.info("find shareToken arg:{},convertToken2ParamInfo result:{}",shareTokenParamInfo);
        if (shareTokenParamInfo != null) {
          employeeInfo=new EmployeeInfo();
          employeeInfo.setEa(shareTokenParamInfo.getEa());
          employeeInfo.setEmployeeId(shareTokenParamInfo.getEmployeeId());
        }
      }
      if (employeeInfo != null) {
        request.setAttribute("Auth", employeeInfo);
      } else {
        employeeInfo = authHelper.getAuthInfo(request);
        if (employeeInfo != null) {
          request.setAttribute("Auth", employeeInfo);
        } else {
          //检测下是否带有shareToken
          //检测下临时cookie
          String cookieStr = request.getHeader("Cookie");
          log.info("cookie string:{}", cookieStr);
          Cookie cookie = WebUtil.getCookie(request, "auth_temp");
          if (cookie != null) {
            log.info("get auth_temp,value:{}", cookie.getValue());
            Guard guard = new Guard(authTempKey);
            try {
              String ea = guard.decode(cookie.getValue());
              employeeInfo = new EmployeeInfo();
              employeeInfo.setEa(ea);
              employeeInfo.setEmployeeId(1000);
              request.setAttribute("Auth", employeeInfo);
            } catch (Exception e) {
              log.warn("requestUri:{},is invalid auth", requestUri);
              response.setStatus(403);
              return;
            }
          } else {
            log.warn("can't get auth_temp!", requestUri);
            String profile = System.getProperty("spring.profiles.active");
            if (!profile.equals("foneshare")) {
              employeeInfo = new EmployeeInfo();
              employeeInfo.setEa("2");
              employeeInfo.setEmployeeId(1000);
              request.setAttribute("Auth", employeeInfo);
            } else {
              log.warn("requestUri:{},is invalid auth", requestUri);
              response.setStatus(403);
              return;
            }
          }
        }
      }
      filterChain.doFilter(request, response);
    }
  }

  /*
  忽略auth认证
   */
  private static boolean ignoreAuth(HttpServletRequest request) {
    String requestUri = request.getRequestURI().toLowerCase();
    String ctx = request.getContextPath();
    if (requestUri.startsWith(ctx + "/open/") || requestUri.startsWith(ctx + "/restful/") ||
      requestUri.startsWith(ctx + "/share/") || requestUri.equals(ctx + "/") || requestUri.contains(".js") ||
      requestUri.contains(".svg") || requestUri.contains(".png") || requestUri.contains(".css") ||
      requestUri.contains(".jpg") || requestUri.contains(".htm") || requestUri.contains("ping")) {
      return true;
    } else {
      return false;
    }
  }
}
