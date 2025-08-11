package bankapp.member.web.controller;

import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.manager.MemberManager;
import bankapp.member.request.signup.SignUpRequest;
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

    private final MemberManager memberManager;

    @Autowired
    public SignUpController(MemberManager memberManager) { this.memberManager = memberManager; }

    // 회원가입 폼 처리
    @GetMapping("")
    public String showSignUpForm(Model model){
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "member/signup/signupForm";
    }

    // 처리
    @PostMapping("")
    public String processSignUp(@Validated @ModelAttribute SignUpRequest signUpRequest , BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            // 값 유효하지 않음
            return "member/signup/signupForm";
        }

        // 중복 검사 , 일치 검사 해야 함
        try {
            memberManager.signUp(signUpRequest);
        }catch (DuplicateUsernameException e){
            bindingResult.rejectValue("username" , "duplicate" , "중복된 ID 입니다.");
            return "member/signup/signupForm";
        }catch(PasswordMismatchException e){
            bindingResult.rejectValue("passwordConfirm" , "invalid" , "비밀번호가 일치하지 않습니다.");
            return "member/signup/signupForm";
        }

        // 가입 완료 : 리다이렉트 새로 해야 함.
        return "redirect:/home";
    }


}
