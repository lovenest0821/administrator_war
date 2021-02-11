package org.greenearth.administrator.config.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
            SecurityContext securityContext = (SecurityContext) httpSession.getAttribute(springSecurityContextKey);
            if(securityContext != null) {
                /*
                 * 여기 프로젝트에는 없지만, AccountController 에서 2차 인증(SMS) 후 setter 로 Account VO 의 값을
                 * 변경(false -> true)하였지만, infiniCache 에서 아래의 setAttribute 를 통해 업데이트되지 않는 현상을 발견하였다.
                 * 이유를 찾던 중, securityContext 의 principle 에서 Account 를 꺼내 위에서 변경한 값을 확인해 보았으나,
                 * AccountController 에서 setter 를 통해 변경한 값(true)이 아닌 기본값(false)이 저장되어 있었다.
                 *
                 * 2021.01.15 - SecurityContextHolder 안에 있는 값이 변경된 걸 확인하고 해당 SecurityContext 를 꺼내
                 * 아래 setAttribute 로 저장하니 Interceptor 에서 값이 변경된 것이 확인되어 정상적으로 동작하는 것을 확인하였다.
                 * Debug 를 통해 정확한 이유를 찾아보아야 겠지만, 위 getAttribute 를 통해 없는 securityContext 는
                 * 값이 변경되기전의 securityContext 일 것으로 추측된다. 이뉴는 AccountController 에서 변경한
                 * Account object 의 경우 securityContext 에서 꺼냈지만, 해당값을 setAttribute 를 통해 servletRequest 에
                 * 저장해 준 적은 없다. 저장하지 않은 상태에서 securityContext 를 servletRequest 에서 다시 꺼내 저장을 하게되면
                 * 변경되기 이전의 securityContext 가 저장 된다고 추측된다.
                 *
                 * 2021.02.10 - 어플리케이션 실행 후 로그인 시 로그인이 끊기는 현상 발견. 또한, 로그인 후 개발자 도구를 실행시키면
                 * 로그인이 풀리는 현상을 발견하여 Debugging 한 결과 SecurityContextHolder 내의 Authentication 객체가 Null 인
                 * 경우가 발생하여 로그인이 해제되는 현상을 발견하였다. 로그인 후에는 해당 객체가 Null이 아닐 때만 Session에
                 * setAttribute 되도록 수정함.
                 *
                 * InfiniCache 에서 세션 저장소 업데이트 모드를 set 으로 사용함에 따른 처리
                 * [default]
                 *  SessionAttribute 를 get 이후 해당 데이터가 객체 Reference 로 제어되는 경우 객체의 변경은 지속적으로 적용된다.
                 *  그러나 명시적인 setAttribute 가 수행되지 않으면 캐시 서버에는 해당 변경 내용이 반영되지 않는다.
                 *  따라서, InfiniCache 에 SecurityContext 를 갱신하기 위해 setAttribute 를 호출하도록 한다.
                 *
                 *  - get
                 *    서블릿 요청 endAccess 시점에 현재까지의 변경 내역이 저장된 임시 저장 영역을 검사하여 일괄적으로 서버에 저장할 수 있다.
                 *    이 경우 실제 변경이 수행되지 않은 데이터에 대해서도 강제로 서버 갱신이 발생하게 되므로 불필요한 컴퓨팅 자원 낭비를 가져올 수 있다.
                 *  - set(default)
                 *    데이터의 저장은 반드시 사용자에 의해 setAttribute 가 수행되어야 저장되는 모드로 사용자 로직에서 저장이 필요한 시점에 명시적으로 호출해 주어야 한다.
                 */
                if(SecurityContextHolder.getContext().getAuthentication() != null){
                    securityContext = SecurityContextHolder.getContext();
                }
                httpSession.setAttribute(springSecurityContextKey, securityContext);
            }
        }
    }
}
