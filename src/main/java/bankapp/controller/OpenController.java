package bankapp.controller;


import bankapp.request.open.HighCreditAccountCreationRequest;
import bankapp.request.open.NormalAccountCreationRequest;
import bankapp.exceptions.DuplicateAccountException;
import bankapp.service.BankAccountManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/open")
public class OpenController {

    private final BankAccountManager accountManager;

    @Autowired
    public OpenController(BankAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    // 계좌 종류 선택
    @GetMapping("/select")
    public String showAccountTypeSelection(){
        return "open/form/select-account-type";
    }

    // 보통 계좌 폼
    @GetMapping("/normal")
    public String showNormalAccountForm(){
        return "open/form/open-normal-account";
    }

    // 신뢰 계좌 폼
    @GetMapping("/highcredit")
    public String showHighCreditAccountForm(){
        return "open/form/open-highcredit-account";
    }

    // 보통 계좌 생성
    @PostMapping("/normal")
    public String processNormalAccountCreation(@ModelAttribute NormalAccountCreationRequest normalAccountCreationRequest){

        try{
            accountManager.openAccount(normalAccountCreationRequest);
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


}
