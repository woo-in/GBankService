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


@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {


    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) { this.loginService = loginService; }

    // 로그인 폼 처리
    @GetMapping("")
    public String showLoginForm(Model model){
        model.addAttribute("loginRequest", new LoginRequest());
        return "member/login/loginForm";
    }

    // 처리
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
