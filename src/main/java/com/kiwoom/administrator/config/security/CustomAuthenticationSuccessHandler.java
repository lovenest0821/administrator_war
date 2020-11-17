package com.kiwoom.administrator.config.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        RequestCache requestCache = new HttpSessionRequestCache();
        AtomicReference<String> redirectUrl = new AtomicReference<>("");
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if(savedRequest == null) {
            log.info("saveRequest is NULL");
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            authorities.forEach(role -> {
                switch (role.toString()) {
                    case "ROLE_CHK":
                        redirectUrl.set("/dashboard");
                        System.out.println("ROLE_CHK = " + role);
                        break;
                    case "ROLE_KDRM":
                        redirectUrl.set("/dashboard");
                        System.out.println("ROLE_KDRM = " + role);
                        break;
                    case "ROLE_EDU":
                        redirectUrl.set("/dashboard");
                        System.out.println("ROLE_EDU = " + role);
                        break;
                    case "ROLE_WM":
                        redirectUrl.set("/wm/dashboard");
                        System.out.println("ROLE_WM = " + role);
                        break;
                    default:
                        redirectUrl.set("/dashboard");
                        System.out.println("role = " + role);
                        break;
                }
            });
        } else {
            log.info("saveRequest is NOT null");
            log.info("RedirectUrl else = " + savedRequest.getRedirectUrl());
            if(savedRequest.getRedirectUrl().contains("error")){
                redirectUrl.set("/dashboard");
            } else {
                redirectUrl.set(savedRequest.getRedirectUrl());
            }
        }

        log.info("Username = " + authentication.getPrincipal());
        log.info("RedirectUrl = " + redirectUrl);
        redirectStrategy.sendRedirect(request, response, redirectUrl.get());
    }
}
