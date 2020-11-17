package com.kiwoom.administrator.config.security;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username = request.getParameter("username");
        String accessIp = request.getRemoteAddr();

        return username + "|" + accessIp;
    }
}
