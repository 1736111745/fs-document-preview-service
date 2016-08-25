package com.facishare.document.preview.cgi.filter;

import com.facishare.document.preview.cgi.controller.PreviewController;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.utils.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuq on 16/8/15.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(PreviewController.class);
    @Autowired
    AuthHelper authHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/")||request.getRequestURI().toLowerCase().contains(".js")) {
            filterChain.doFilter(request, response);
        }
        else {
            EmployeeInfo employeeInfo = authHelper.getAuthinfo(request);
            if (employeeInfo == null) {
                LOG.info("url:{}",request.getRequestURI());
                response.getWriter().println("访问受限:无登录信息!");
                return;
            } else {
                request.setAttribute("Auth", employeeInfo);
                filterChain.doFilter(request, response);
            }
        }
    }
}
