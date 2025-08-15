package bankapp.member.web.controller;

import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.account.manager.AccountManager;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.manager.MemberManager;
import bankapp.member.model.Member;
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

import java.math.BigDecimal;

@Slf4j
@Controller
@RequestMapping("/signup")
public class SignUpController {

    private final MemberManager memberManager;
    private final AccountManager accountManager;

    @Autowired
    public SignUpController(MemberManager memberManager , AccountManager accountManager) {
        this.memberManager = memberManager;
        this.accountManager = accountManager;
    }

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

        // 새 회원 추가
        Member newMember ;
        try {
            newMember = memberManager.signUp(signUpRequest);
            accountManager.openPrimaryAccount(new OpenPrimaryAccountRequest(newMember.getMemberId() , BigDecimal.ZERO,signUpRequest.getAccountNickname()));
        }catch (DuplicateUsernameException e){
            bindingResult.rejectValue("username" , "duplicate" , "중복된 ID 입니다.");
            return "member/signup/signupForm";
        }catch(PasswordMismatchException e){
            bindingResult.rejectValue("passwordConfirm" , "invalid" , "비밀번호가 일치하지 않습니다.");
            return "member/signup/signupForm";
        }catch(InvalidDepositAmountException e) {
            return "redirect:/error";
        }catch(MemberNotFoundException e) {
            return "redirect:/error";
        }

        return "redirect:/";
    }


}
