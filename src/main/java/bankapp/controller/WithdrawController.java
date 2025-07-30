package bankapp.controller;

import bankapp.request.withdraw.AccountWithdrawRequest;
import bankapp.exceptions.InsufficientFundsException;
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
@RequestMapping("/withdraw")
public class WithdrawController {
    private final BankAccountManager accountManager;

    @Autowired
    public WithdrawController(BankAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    // 출금 폼
    @GetMapping("")
    public String showWithdrawForm(){
        return "withdraw/form/withdraw";
    }

    // 출금
    @PostMapping("")
    public String processWithdraw(@ModelAttribute AccountWithdrawRequest accountWithdrawRequest){

        try {
            accountManager.withdraw(accountWithdrawRequest);
        }
        catch (IllegalArgumentException e) {
            // 입력값 에러
            return "withdraw/error-message/invalid-input-error";
        } catch (InsufficientFundsException e) {
            // 잔액 부족
            return "withdraw/error-message/insufficient-fund-error";
        } catch(InvalidAccountException e) {
            // 존재하지 않는 계좌
            return "withdraw/error-message/invalid-account-error";
        } catch(Exception e) {
            // 예기치 못한 에러
            return "withdraw/error-message/unexpected-error";
        }

        // 성공
        return "withdraw/success-message/success";

    }

}


