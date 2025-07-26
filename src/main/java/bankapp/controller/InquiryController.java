package bankapp.controller;

import org.springframework.ui.Model;
import bankapp.account.find.AccountFindRequest;
import bankapp.account.model.BankAccount;
import bankapp.exceptions.InvalidAccountException;
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
@RequestMapping("/inquiry")
public class InquiryController {

    private final BankAccountManager accountManager;

    @Autowired
    public InquiryController(BankAccountManager accountManager) { this.accountManager = accountManager; }

    // 조회 폼
    @GetMapping("")
    public String showInquiryForm(){
        return "inquiry/form/input";
    }

    @PostMapping("")
    public String processInquiry(@ModelAttribute AccountFindRequest accountFindRequest , Model model) {

        BankAccount found;
        try {
            found = accountManager.findAccount(accountFindRequest);
        }
        catch(InvalidAccountException e) {
            // 존재하지 않는 계좌
            return "inquiry/error-message/invalid-account-error";
        }
        catch(Exception e) {
            // 예기치 못한 에러
            return "inquiry/error-message/unexpected-error";
        }

        // 성공
        model.addAttribute("account", found);
        return "inquiry/success-message/success";
    }

}
