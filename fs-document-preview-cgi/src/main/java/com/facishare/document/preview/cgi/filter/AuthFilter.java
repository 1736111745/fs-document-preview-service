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
  @ReloadableProperty("staticFileVersion")
  private String staticFileVersion = "v5.3.30";

  private @Context
  HttpServletRequest request;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    request.setAttribute("sv", staticFileVersion);
    String requestUri = request.getRequestURI().toLowerCase();
    if (ignoreAuth(request)) {
      filterChain.doFilter(request, response);
      return;
    } else {
      EmployeeInfo employeeInfo = null;
      String shareToken = UrlParametersHelper.safeGetRequestParameter(request, "shareToken");
      ShareTokenParamInfo shareTokenParamInfo;
      if (!Strings.isNullOrEmpty(shareToken)) {
        shareTokenParamInfo = ShareTokenUtil.convertToken2ParamInfo(shareToken);
        log.info("find shareToken arg:{},convertToken2ParamInfo result:{}", shareTokenParamInfo);
        if (shareTokenParamInfo != null) {
          employeeInfo = new EmployeeInfo();
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
          //?????????????????????shareToken
          //???????????????cookie
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
            String profile = System.getProperty("spring.profiles.active");
            if (!profile.contains("foneshare")) {
              employeeInfo = new EmployeeInfo();
              employeeInfo.setEa("75138");
              employeeInfo.setEmployeeId(1000);
              request.setAttribute("Auth", employeeInfo);
              //?????????cookie
              //String authCookieStr="FSAuthX=0G60A8NRRG400012qn7OLB6uLrqNwPyFvqaIbS7hPSz5bEUo2JrvReHzbzL1xzKoxWY2odJxfy8060ILdxEFfVlqzbhjz4RS3bzdyltFmviyrJLWdcuj6z2lscxq2QKzycZwAM8jDxjxkOS7paFZ85CbJy7eeiorJfCgjwdp9nboGWIhGFZIW7jKeaAqeILjv0azXdklRbRUH6cNCHDFvSr2MP8bY7p90hpWHzT";
              Cookie authCookie = new Cookie("FSAuthX",
                "0G60A8NRRG400012qn7OLB6uLrqNwPyFvqaIbS7hPSz5bEUo2JrvReHzbzL1xzKoxWY2odJxfy8060ILdxEFfVlqzbhjz4RS3bzdyltFmviyrJLWdcuj6z2lscxq2QKzycZwAM8jDxjxkOS7paFZ85CbJy7eeiorJfCgjwdp9nboGWIhGFZIW7jKeaAqeILjv0azXdklRbRUH6cNCHDFvSr2MP8bY7p90hpWHzT");
              authCookie.setDomain("ceshi112.com");
              authCookie.setPath("/");
              response.addCookie(authCookie);
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
  ??????auth??????
   */
  private static boolean ignoreAuth(HttpServletRequest request) {
    String requestUri = request.getRequestURI().toLowerCase();
    String ctx = request.getContextPath();
    if (requestUri.contains(".js") || requestUri.contains(".svg") || requestUri.contains(".png") || requestUri.contains(".css") ||
      requestUri.contains(".jpg") || requestUri.contains(".htm")) {
      String ea = request.getParameter("ea");
      if (!Strings.isNullOrEmpty(ea)) {
        log.info("static resource ea:{}",ea);
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setEa(ea);
        request.setAttribute("Auth", employeeInfo);
      }
      return true;
    }
    if (requestUri.startsWith(ctx + "/open/") || requestUri.startsWith(ctx + "/restful/") || requestUri.startsWith(ctx + "/share/") ||
      requestUri.equals(ctx + "/") || requestUri.contains("ping") || requestUri.contains("/task/") || requestUri.contains("test")) {
      return true;
    } else {
      return false;
    }
  }
}
