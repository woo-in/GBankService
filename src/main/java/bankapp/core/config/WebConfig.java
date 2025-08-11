package bankapp.core.config;

import bankapp.core.interceptor.LoginCheckerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new LoginCheckerInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/signup");
        // 로그인과 회원가입 제외 모두 세션 체크
    }
}
