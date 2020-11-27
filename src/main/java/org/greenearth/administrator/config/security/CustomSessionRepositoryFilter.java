package org.greenearth.administrator.config.security;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomSessionRepositoryFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String springSecurityContextKey = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

        filterChain.doFilter(servletRequest, servletResponse);

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession httpSession = request.getSession(false);
        if(httpSession != null) {
            Object securityContext = httpSession.getAttribute(springSecurityContextKey);
            if(securityContext != null) {
                httpSession.setAttribute(springSecurityContextKey, securityContext);
            }
        }
    }
}
