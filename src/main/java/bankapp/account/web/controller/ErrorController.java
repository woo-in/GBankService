package bankapp.account.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 애플리케이션 전역에서 발생하는 HTTP 오류를 처리하는 컨트롤러.
 * Spring Boot의 기본 오류 처리 메커니즘에 의해 맵핑된다.
 */
@Controller
@RequestMapping("/error")
public class ErrorController {

    // TODO: 유저가 URL 을 잘못 입력한 경우는 다르게 처리해야 하지 않을까 ?

    /**
     * 사용자에게 보여줄 기본 오류 페이지 화면을 반환한다.
     * @return 오류 페이지 뷰의 논리적 이름
     */
    @GetMapping
    public String showErrorPage(){
        return "account/home/error";
    }


}
