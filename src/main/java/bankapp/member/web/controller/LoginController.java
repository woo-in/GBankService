package bankapp.member.web.controller;

import bankapp.core.common.SessionConst;
import bankapp.member.exceptions.IncorrectPasswordException;
import bankapp.member.exceptions.UsernameNotFoundException;
import bankapp.member.model.Member;
import bankapp.member.request.login.LoginRequest;
import bankapp.member.service.login.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 사용자 로그인과 관련된 웹 요청을 처리하는 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;
    @Autowired
    public LoginController(LoginService loginService) { this.loginService = loginService; }

    /**
     * 로그인 폼 화면을 반환한다.
     * @param model 뷰에 전달할 모델 객체
     * @return 로그인 폼 뷰의 논리적 이름
     */
    @GetMapping("")
    public String showLoginForm(Model model){
        model.addAttribute("loginRequest", new LoginRequest());
        return "member/login/loginForm";
    }

    /**
     * 사용자가 입력한 아이디와 비밀번호로 로그인을 처리한다.
     *
     * @param loginRequest  로그인 폼에서 입력한 데이터 (DTO)
     * @param bindingResult 데이터 바인딩 및 검증 오류 객체
     * @param redirectURL   로그인 성공 시 리다이렉트할 URL (지정하지 않으면 메인 페이지로 이동)
     * @param request       HTTP 세션을 얻기 위한 HttpServletRequest 객체
     * @return 로그인 성공 시 redirectURL로 리다이렉트, 실패 시 로그인 폼으로 이동
     */
    @PostMapping("")
    public String processLogin(@Validated @ModelAttribute LoginRequest loginRequest
                                ,BindingResult bindingResult
                                ,@RequestParam(defaultValue = "/") String redirectURL
                                ,HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "member/login/loginForm";
        }

        try{
            Member member = loginService.login(loginRequest);
            HttpSession session = request.getSession(true);
            session.setAttribute(SessionConst.LOGIN_MEMBER, member);
            return "redirect:" + redirectURL;
        }catch (UsernameNotFoundException e){
            bindingResult.rejectValue("username" , "invalid" , "존재하지 않는 아이디입니다.");
            return "member/login/loginForm";
        }catch (IncorrectPasswordException e) {
            bindingResult.rejectValue("password" , "invalid" , "비밀번호가 일치하지 않습니다.");
            return "member/login/loginForm";
        }

    }
}
