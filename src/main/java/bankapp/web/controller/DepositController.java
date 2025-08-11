package bankapp.web.controller;

import bankapp.request.deposit.AccountDepositRequest;
import bankapp.exceptions.InvalidAccountException;
import bankapp.service.BankAccountManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


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
            return "redirect:/deposit/error/invalid-input-error";
        }
        catch(InvalidAccountException e) {
            // 존재하지 않는 계좌
            return "redirect:/deposit/error/invalid-account-error";
        }
        catch(Exception e) {
            // 예기치 못한 에러
            return "redirect:/deposit/error/unexpected-error";
        }

        // 성공
        return "redirect:/deposit/success/success?amount=" + accountDepositRequest.getAmount();
    }

    @GetMapping("/success/success")
    public String depositSuccessPage(@RequestParam("amount") double amount , Model model){
        AccountDepositRequest accountDepositRequest = new AccountDepositRequest(-1, amount);
        model.addAttribute("accountDepositRequest", accountDepositRequest);
        return "deposit/success-message/success";
    }

    @GetMapping("/error/invalid-input-error")
    public String depositInvalidInputErrorPage(){
        return "deposit/error-message/invalid-input-error";
    }

    @GetMapping("/error/invalid-account-error")
    public String depositInvalidAccountErrorPage(){
        return "deposit/error-message/invalid-account-error";
    }

    @GetMapping("/error/unexpected-error")
    public String depositUnexpectedErrorPage(){
        return "deposit/error-message/unexpected-error";
    }
}
