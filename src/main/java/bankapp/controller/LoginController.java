package bankapp.controller;

import bankapp.exceptions.IncorrectPasswordException;
import bankapp.exceptions.UsernameNotFoundException;
import bankapp.model.member.Member;
import bankapp.request.login.LoginRequest;
import bankapp.service.BankAccountManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    private final BankAccountManager accountManager;

    @Autowired
    public LoginController(BankAccountManager accountManager) { this.accountManager = accountManager; }

    // 로그인 폼 처리
    @GetMapping("")
    public String showLoginForm(Model model){
        model.addAttribute("loginRequest", new LoginRequest());
        return "login/form/login";
    }

    // 처리
    @PostMapping("")
    public String processLogin(@Validated @ModelAttribute LoginRequest loginRequest , BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return "login/form/login";
        }

        // 아이디 비밀번호 검사후 객체 반환
        try{
            Member member = accountManager.login(loginRequest);
        }catch (UsernameNotFoundException e){
            bindingResult.rejectValue("username" , "invalid" , "존재하지 않는 아이디입니다.");
            return "login/form/login";
        } catch (IncorrectPasswordException e) {
            bindingResult.rejectValue("password" , "invalid" , "비밀번호가 일치하지 않습니다.");
            return "login/form/login";
        }

        
        // 이제 멤버를 사용해서 세션 처리 하면 된다 !
        // 임시 : 리다이렉트 (로그인 성공)
        return "redirect:/home";

    }

}
