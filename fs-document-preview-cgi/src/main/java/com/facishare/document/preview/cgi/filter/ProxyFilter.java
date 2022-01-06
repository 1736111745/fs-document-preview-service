package com.facishare.document.preview.cgi.filter;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;


@Slf4j
public class ProxyFilter implements Filter {
    private Set<String> eas = new HashSet<>();

    public void init(FilterConfig config) throws ServletException {
        ConfigFactory.getConfig("fs-dps-config", conf -> {
            String eaList = conf.get("proxy.ea.list");
            log.info("proxy.ea.list: {}",eaList);
            eas = new HashSet<>(Splitter.on(',').trimResults().omitEmptyStrings().splitToList(eaList));
        });
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws java.io.IOException, ServletException {
        EmployeeInfo employee = getEmployee(request);
        if (employee != null && eas.contains(employee.getEa())) {
                HttpServletRequest httpReq = (HttpServletRequest) request;
                String path = httpReq.getRequestURI().substring(httpReq.getContextPath().length());
            try {
                long startTime = System.currentTimeMillis();
                request.getRequestDispatcher("/proxy2k8s" + path).forward(request, response);
                long tc = System.currentTimeMillis() - startTime;
                log.info("proxy to k8s service success, path: {}, status:{}, ea: {}, time cost: {} ms",
                        path,((HttpServletResponse)response).getStatus(), employee.getEa(),tc);
            }catch (Exception e) {
                log.error("proxy to k8s service fail, path: {}, ea: {}",path, employee.getEa(),e);
                throw e;
            }
            return;
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
        log.info("filter destroy");
    }

    private EmployeeInfo getEmployee(ServletRequest request) {
        Object emp = request.getAttribute("Auth");
        if (emp instanceof EmployeeInfo) {
            return (EmployeeInfo) emp;
        }
        return null;
    }
}