package bankapp.controller;


import bankapp.account.request.creation.NormalAccountCreationRequest;
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
@RequestMapping("")
public class BankController {

    private final BankAccountManager accountManager;

    @Autowired
    public BankController(BankAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    // 메뉴 출력
    @GetMapping("/menu")
    public String showMenu(){
        return "menu/menu";
    }

    // 계좌생성 workflow

    @GetMapping("/open/select")
    public String showAccountTypeSelection(){
        return "open/form/select-account-type";
    }

    @GetMapping("/open/normal")
    public String showNormalAccountForm(){
        return "open/form/open-normal-account";
    }

    @GetMapping("/open/highcredit")
    public String showHighCreditAccountForm(){
        return "open/form/open-highcredit-account";
    }

    @PostMapping("/open/normal")
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
}
