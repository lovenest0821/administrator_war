package org.greenearth.administrator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CertificationInterceptor certificationInterceptor;

    // Interceptor 추가시 제외될 URI 패턴 등록도 같이 등록을 해줘야 302 응답코드를 안 볼 수 있다.
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(certificationInterceptor)
                .excludePathPatterns("/", "/login", "/password-init", "/email-by-passwordInit", "/node_modules/**"
                        , "/css/**", "/img/**", "/js/**", "/html/**", "/favicon.ico", "/index.html", "/error");
    }
}
