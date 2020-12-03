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

/**
  * Redis 와 같은 Memory DB로 세션을 관리할 경우 기본적으로 Set 설정으로 되어 있다.
  * Set 으로 설정 되어 있을 경우, SecurityContext 가 setter 에 의해 동기화 되지 않는다.
  * Tomcat 의 경우 Heap 영역 안에 SecurityContext 가 존재하기 때문에 동기화가 되지만,
  * Memory DB 의 경우. 반드시, setAttribute 를 사용하여 SecurityContext 를 업데이트 해 주어야 한다.
  * SecurityConfig 의 .addFilterAfter(customSessionRepositoryFilter(), SecurityContextPersistenceFilter.class) 추가
  */
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
