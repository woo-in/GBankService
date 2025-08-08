package bankapp.controller;

import bankapp.request.signup.SignUpRequest;
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
@RequestMapping("/signup")
public class SignUpController {

    private final BankAccountManager accountManager;

    @Autowired
    public SignUpController(BankAccountManager accountManager) { this.accountManager = accountManager;}

    // 로그인 처리 폼 조회
    @GetMapping("")
    public String showSignUpForm(Model model){
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "signup/form/signup";
    }

    // 처리
    @PostMapping("")
    public String processSignUp(@Validated @ModelAttribute SignUpRequest signUpRequest , BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            // 값 유효하지 않음
            return "signup/form/signup";
        }

        // 중복 검사 , 일치 검사 해야 함




        return "menu/menu";
    }


}
