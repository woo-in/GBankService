package bankapp.core.interceptor;

import bankapp.core.common.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request , HttpServletResponse response, Object handler) throws Exception{

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();

        if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
                // 미인증 사용자 요청
                // 로그인 후 그전 URL 바로 접근할 수 있도록 쿼리도 같이 보냄
                response.sendRedirect("/login?redirectUrl=" + requestURI);
                return false;
        }

        return true;
    }

}
