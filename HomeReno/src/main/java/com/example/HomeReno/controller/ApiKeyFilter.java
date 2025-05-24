package com.example.HomeReno.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class ApiKeyFilter implements Filter {

    @Value("${api.key}")
    private String apiKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException{

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestApiKey = httpRequest.getHeader("X-API-KEY");

        if (apiKey.equals(requestApiKey)) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invaild API Key");
        }
    }
}
