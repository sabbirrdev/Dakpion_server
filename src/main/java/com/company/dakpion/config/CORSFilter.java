package com.company.dakpion.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)


public class CORSFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers",
                "X-Requested-With, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Headers");

        if (req.getMethod().equalsIgnoreCase("POST") ||
                req.getMethod().equalsIgnoreCase("GET") ||
                req.getMethod().equalsIgnoreCase("PUT") ||
                req.getMethod().equalsIgnoreCase("OPTIONS") ||
                req.getMethod().equalsIgnoreCase("DELETE")) {

            if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                chain.doFilter(request, response);
            }

        } else {
            String responseToClient = "{\n\"status\": 405,\n\"error\": \"Method Not Allowed\"\n}";
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            res.getWriter().write(responseToClient);
            res.getWriter().flush();
        }


    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
