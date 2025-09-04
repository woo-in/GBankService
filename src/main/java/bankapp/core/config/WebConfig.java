package bankapp.core.config;

import bankapp.account.interceptor.TransferRequestIdCheckInterceptor;
import bankapp.member.interceptor.LoginCheckerInterceptor;
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
                .excludePathPatterns("/error");

        registry.addInterceptor(new TransferRequestIdCheckInterceptor())
                .order(2)
                .addPathPatterns("/transfer/amount" , "/transfer/message" , "/transfer/auth" , "/transfer/execute");
    }

}
