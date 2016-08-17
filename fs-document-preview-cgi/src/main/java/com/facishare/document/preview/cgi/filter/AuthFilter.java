package com.facishare.document.preview.cgi.filter;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.utils.AuthHelper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuq on 16/8/15.
 */
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        if (request.getRequestURI().equals("/")) {
            filterChain.doFilter(request, response);
        }
        else {
            EmployeeInfo employeeInfo = AuthHelper.getAuthinfo(request);
            if (employeeInfo == null) {
                response.setStatus(403);
                return;
            } else {
                request.setAttribute("Auth", employeeInfo);
                filterChain.doFilter(request, response);
            }
        }
    }
}
