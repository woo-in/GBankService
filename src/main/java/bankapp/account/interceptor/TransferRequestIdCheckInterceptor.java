package bankapp.account.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class TransferRequestIdCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        String requestId = (String) session.getAttribute("requestId");
        if (requestId == null) {
            response.sendRedirect("/transfer/new");
            return false;
        }

        return true;
    }

}

