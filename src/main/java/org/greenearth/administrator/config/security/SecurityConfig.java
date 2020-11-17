package org.greenearth.administrator.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/password-init", "/email-by-passwordInit").permitAll()
                .mvcMatchers("/chk/**").hasRole("CHK")
                .mvcMatchers("/kdream/**").hasRole("KDRM")
                .mvcMatchers("/edu/**").hasRole("EDU")
                .mvcMatchers("/wm/**").hasRole("WM")
                .anyRequest().authenticated()
        ;

        http.formLogin()
                .loginPage("/login").permitAll()
        ;

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1)
        ;

        http.exceptionHandling().accessDeniedPage("/login");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    /**
     * 1. filter.1setAuthenticationManager(authentcationManager()) 를 선언해주는 이유
     * - 위 내용을 추가해 주지 않을 경우 authenticationManager가 등록되지 않아 인증이 정상적으로 동작하지 않는다.
     * 2. 상단의 http.formLogin()에 successHandler(), failureHandler() 설정시 정상 동장하지 않는다.
     * 대신에 customUsernamePasswordAuthenticationFilter() 에 setAuthenticationFailureHandler() 와
     * setAuthenticationSuccessHandler()를 설정해 주면 정상 동작한다.
     * filter.setAuthenticationFailureHandler() 를 설정해 주는 이유
     * UsernamePasswordAuthenticationFilter 를 상속 받아 일부 method 만 수정하여 filter를 기존 필터를 사이에 넣었지만
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
}
