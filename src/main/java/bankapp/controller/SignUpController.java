package bankapp.controller;

import bankapp.exceptions.DuplicateUsernameException;
import bankapp.exceptions.PasswordMismatchException;
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

    // 회원가입 폼 처리
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
        try {
            accountManager.signUp(signUpRequest);
        }catch (DuplicateUsernameException e){
            bindingResult.rejectValue("username" , "duplicate" , "중복된 ID 입니다.");
            return "signup/form/signup";
        }catch(PasswordMismatchException e){
            bindingResult.rejectValue("passwordConfirm" , "invalid" , "비밀번호가 일치하지 않습니다.");
            return "signup/form/signup";
        }

        // 가입 완료 : 리다이렉트 새로 해야 함.
        return "redirect:/home";
    }


}
