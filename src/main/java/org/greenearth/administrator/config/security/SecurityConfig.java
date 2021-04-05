package org.greenearth.administrator.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    /**
     *
     * @param web
     * PathRequest.toStaticResources().atCommonLocation() 의 경우
     * StaticResourceLocation.class enum 클래스에 정의된 경로만 제외된다.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(PathRequest.toH2Console())
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        * SecurityContextPersistenceFilter.class 후에 CustomSessionRepositoryFilter() 를 추가한 이유
        * Redis 와 같은 Memory DB로 세션을 관리할 경우 기본적으로 Set 설정으로 되어 있다.
        * Set 으로 설정 되어 있을 경우, SecurityContext 가 setter 에 의해 동기화 되지 않는다.
        * Tomcat 의 경우 Heap 영역 안에 SecurityContext 가 존재하기 때문에 동기화가 되지만,
        * Memory DB 의 경우. 반드시, setAttribute 를 사용하여 SecurityContext 를 업데이트 해 주어야 한다.
        * SecurityConfig 의 .addFilterAfter(customSessionRepositoryFilter(), SecurityContextPersistenceFilter.class) 추가
        * */
        http.addFilterAfter(customSessionRepositoryFilter(), SecurityContextPersistenceFilter.class)
                .addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;

        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/password-init", "/email-by-passwordInit", "/uploadEx", "/uploadAjax").permitAll()
                .mvcMatchers("/chk/**").hasRole("CHK")
                .mvcMatchers("/kdream/**").hasRole("KDRM")
                .mvcMatchers("/edu/**").hasRole("EDU")
                .mvcMatchers("/wm/**").hasRole("WM")
                .anyRequest().authenticated()
        ;

        http.formLogin()
                .loginPage("/login").permitAll()
        ;

        http.logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
        ;

        http.exceptionHandling().accessDeniedPage("/login");

        http.authenticationProvider(customAuthenticationProvider);

        /*
         * clicking Hijacking Attack 보안이슈로 기본적으로 X-Frame-Options 기본설정은 DENY 로 되어 있다.
         * deny() 설정 할 경우 <frame> <iframe> <embed> 를 사용할 수 없다.
         * sameOrigin() 설정 할 경우 같은 도메인에서만 사용 가능하다.
         * disable() 설정 할 경우 X-Frame-Options 를 제한없이 사용 가능하다.
         * 위의 세가지 방법으로 기본 설정 후 addHeaderWriter()를 사용하여 WhiteList 방식으로 추가할 수 있다.
         */
        http.headers().frameOptions().sameOrigin();
    }

    /**
     * 1. filter.setAuthenticationManager(authenticationManager()) 를 선언해주는 이유
     * - 위 내용을 추가해 주지 않을 경우 authenticationManager 가 등록되지 않아 인증이 정상적으로 동작하지 않는다.
     * 2. 상단의 http.formLogin()에 successHandler(), failureHandler() 설정시 정상 동장하지 않는다.
     * 대신에 customUsernamePasswordAuthenticationFilter() 에 setAuthenticationFailureHandler() 와
     * setAuthenticationSuccessHandler()를 설정해 주면 정상 동작한다.
     * filter.setAuthenticationFailureHandler() 를 설정해 주는 이유
     * UsernamePasswordAuthenticationFilter 를 상속 받아 일부 method 만 수정하여 filter 를 기존 필터들 사이에 넣었지만
     * 로그인 실패시 에러 메시지가 화면에 출력되지 않는 현상 발견.
     * 원인 : SimpleUrlAuthenticationFailureHandler.class 의 onAuthenticationFailure() method 에서
     * defaultUrl 이 null 로 설정되어 있어 redirect 안되어 발생하는 현상.
     * 해결방안 : 로그인 실패시 처리하는 AuthenticationFailureHandler 를 Custom 하여 아래와 같이 filter 에 정의.
    ** */
    public UsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationFailureHandler(customAuthenticationFailureHandler());
        filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler());
        return filter;
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public CustomSessionRepositoryFilter customSessionRepositoryFilter() {
        return new CustomSessionRepositoryFilter();
    }

    /**
     * Logout의 invalidateHttpSession 이 정상동작하지 않아 로그인이 불가한 현상이 발생함.
     * HttpSessionEventPublisher 는 session clustering 환경에서 Spring Security 가 전달받게 하기 위해 필요함
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisherServletListenerRegistrationBean() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }
}
