package bankapp.member.web.controller;

import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.account.service.open.primary.OpenPrimaryAccountService;
import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.model.Member;
import bankapp.member.request.signup.SignUpRequest;
import bankapp.member.service.signup.SignUpService;
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

/**
 * 회원 가입과 관련된 웹 요청을 처리하는 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/signup")
public class SignUpController {

    private final SignUpService signUpService;
    private final OpenPrimaryAccountService openPrimaryAccountService;
    @Autowired
    public SignUpController(SignUpService signUpService , OpenPrimaryAccountService openPrimaryAccountService) {
        this.signUpService = signUpService;
        this.openPrimaryAccountService = openPrimaryAccountService;
    }


    /**
     * 회원가입 폼 화면을 반환한다.
     * @param model 뷰에 전달할 모델 객체
     * @return 회원가입 폼 뷰의 논리적 이름
     */
    @GetMapping("")
    public String showSignUpForm(Model model){
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "member/signup/signupForm";
    }

    /**
     * 사용자가 입력한 정보로 회원가입을 처리하고, 첫 주거래 계좌를 개설한다.
     * @param signUpRequest 회원가입 폼에서 입력한 데이터 (DTO)
     * @param bindingResult 데이터 바인딩 및 검증 오류 객체
     * @return 성공 시 메인 페이지로 리다이렉트, 실패 시 회원가입 폼으로 이동
     */
    @PostMapping("")
    public String processSignUp(@Validated @ModelAttribute SignUpRequest signUpRequest , BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "member/signup/signupForm";
        }


        try {
            // TODO: 회원가입은 완료 , 계좌 생성은 안된 경우 대비할 것
            Member newMember = signUpService.signUp(signUpRequest);
            openPrimaryAccountService.openPrimaryAccount(new OpenPrimaryAccountRequest(newMember.getMemberId() , BigDecimal.ZERO,signUpRequest.getAccountNickname()));
        }catch (DuplicateUsernameException e){
            bindingResult.rejectValue("username" , "duplicate" , "중복된 ID 입니다.");
            return "member/signup/signupForm";
        }catch(PasswordMismatchException e){
            bindingResult.rejectValue("passwordConfirm" , "invalid" , "비밀번호가 일치하지 않습니다.");
            return "member/signup/signupForm";
        } catch (InvalidDepositAmountException | MemberNotFoundException e) {
            return "redirect:/error";
        }

        // TODO: 회원 가입 성공후 , 완료 되었다는 페이지로 redirect 하기
        return "redirect:/";
    }


}
