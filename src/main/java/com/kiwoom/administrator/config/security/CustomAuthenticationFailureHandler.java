package com.kiwoom.administrator.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        saveException(request, exception);

        String defaultFailureUrl = "/login?error";
        request.setAttribute("message", exception.getMessage());
        redirectStrategy.sendRedirect(request, response, defaultFailureUrl);
    }

    protected final void saveException(HttpServletRequest request, AuthenticationException exception) {
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
    }
}
