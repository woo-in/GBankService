package bankapp.controller;

import bankapp.account.deposit.AccountDepositRequest;
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
@RequestMapping("/deposit")
public class DepositController {

    private final BankAccountManager accountManager;

    @Autowired
    public DepositController(BankAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    // 입금 폼
    @GetMapping("")
    public String showDepositForm(){
        return "deposit/form/deposit";
    }

    // 입금
    @PostMapping("")
    public String processDeposit(@ModelAttribute AccountDepositRequest accountDepositRequest){


        try {
            accountManager.deposit(accountDepositRequest);
        }
        catch (IllegalArgumentException e) {
            // 입력값 에러
            return "deposit/error-message/invalid-input-error";
        }
        catch(InvalidAccountException e) {
            // 존재하지 않는 계좌
            return "deposit/error-message/invalid-account-error";
        }
        catch(Exception e) {
            // 예기치 못한 에러
            return "deposit/error-message/unexpected-error";
        }

        // 성공
        return "deposit/success-message/success";
    }





}
