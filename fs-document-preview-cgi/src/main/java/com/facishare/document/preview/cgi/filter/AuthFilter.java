package com.facishare.document.preview.cgi.filter;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.utils.AuthHelper;
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
    @Autowired
    AuthHelper authHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request.setAttribute("sv","v1.1.2");
        String requestUri = request.getRequestURI().toLowerCase();
        if (requestUri.equals("/") || requestUri.contains(".js") || requestUri.contains(".svg") || requestUri.contains(".png") || requestUri.contains(".css") || requestUri.contains(".jpg") || requestUri.contains(".html")) {
            filterChain.doFilter(request, response);
        } else {
            EmployeeInfo employeeInfo = authHelper.getAuthinfo(request);
            if (employeeInfo == null) {
                String profile = System.getProperty("spring.profiles.active");

                if (!profile.equals("foneshare")) {
                    employeeInfo = new EmployeeInfo();
                    employeeInfo.setEa("7");
                    employeeInfo.setEmployeeId(1000);
                    request.setAttribute("Auth", employeeInfo);
                } else
                    response.setStatus(403);

            } else {
                request.setAttribute("Auth", employeeInfo);
            }
            filterChain.doFilter(request, response);
        }
    }
}
