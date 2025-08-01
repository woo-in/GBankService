package bankapp.controller;


import bankapp.request.open.HighCreditAccountCreationRequest;
import bankapp.request.open.NormalAccountCreationRequest;
import bankapp.exceptions.DuplicateAccountException;
import bankapp.service.BankAccountManager;
import bankapp.validator.open.NormalAccountCreationRequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/open")
public class OpenController {

    private final BankAccountManager accountManager;
    private final NormalAccountCreationRequestValidator normalAccountCreationRequestValidator;

    @Autowired
    public OpenController(BankAccountManager accountManager) {
        this.accountManager = accountManager;
        this.normalAccountCreationRequestValidator = new NormalAccountCreationRequestValidator();
    }





    // 바인더 초기화
    @InitBinder
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(normalAccountCreationRequestValidator);
    }


    // 계좌 종류 선택
    @GetMapping("/select")
    public String showAccountTypeSelection(){
        return "open/form/select-account-type";
    }

    // 보통 계좌 폼
    @GetMapping("/normal")
    public String showNormalAccountForm(Model model){
        // 왜 ?
        model.addAttribute("normalAccountCreationRequest", new NormalAccountCreationRequest());
        return "open/form/open-normal-account";
    }


    // 신뢰 계좌 폼
    @GetMapping("/highcredit")
    public String showHighCreditAccountForm(){
        return "open/form/open-highcredit-account";
    }

    // 보통 계좌 생성
    @PostMapping("/normal")
    public String processNormalAccountCreation(@Validated  @ModelAttribute NormalAccountCreationRequest normalAccountCreationRequest , BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            // 값 유효하지 않음
            return "open/form/open-normal-account";
        }

        try{
            // 입금 처리
            accountManager.openAccount(normalAccountCreationRequest);
        }
        catch(DuplicateAccountException e) {
            // 존재하는 계좌 -> 입력 폼으로 돌아가기
            bindingResult.rejectValue("accountNumber" , "duplicate" , "중복된 계좌번호 입니다.");
            return "open/form/open-normal-account";
        }
        catch(Exception e) {
            // 예기치 못한 에러 -> menu 로 돌아가기
            return "open/error-message/unexpected-error";
        }
        // 성공
        return "redirect:/open/success";
    }

    // 신뢰 계좌 생성
    @PostMapping("/highcredit")
    public String processHighCreditAccountCreation(@ModelAttribute HighCreditAccountCreationRequest highCreditAccountCreationRequest){

        try{
            accountManager.openAccount(highCreditAccountCreationRequest);
        }
        catch (IllegalArgumentException e){
            // 입력값 에러
            return "open/error-message/invalid-input-error";
        }
        catch(DuplicateAccountException e) {
            // 존재하는 계좌
            return "open/error-message/duplicate-account-error";
        }
        catch(Exception e) {
            // 예기치 못한 에러
            return "open/error-message/unexpected-error";
        }

        // 성공
        return "open/success-message/success";
    }

    // 성공 페이지 추가
    @GetMapping("/success")
    public String showSuccessMessage(){
        return "open/success-message/success";
    }




}
