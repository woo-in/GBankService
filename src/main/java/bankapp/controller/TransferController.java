package bankapp.controller;

import bankapp.account.transfer.AccountTransferRequest;
import bankapp.exceptions.InsufficientFundsException;
import bankapp.exceptions.InvalidAccountException;
import bankapp.service.BankAccountManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/transfer")
public class TransferController {
    private final BankAccountManager accountManager;

    @Autowired
    public TransferController(BankAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    // 송금 폼
    @GetMapping("")
    public String showTransferForm(){
        return "transfer/form/transfer";
    }

    // 송금
    @PostMapping("")
    public String processTransfer(@ModelAttribute AccountTransferRequest accountTransferRequest , Model model) {

        try {
            accountManager.transfer(accountTransferRequest);
        } catch (IllegalArgumentException e) {
            // 입력값 에러
            return "transfer/error-message/invalid-input-error";
        } catch (InsufficientFundsException e) {
            // 잔액 부족
            return "transfer/error-message/insufficient-fund-error";
        } catch (InvalidAccountException e) {
            // 존재하지 않는 계좌
            if (e.getRole() == InvalidAccountException.Role.SENDER) {
                model.addAttribute("errorMessage", "송금 계좌 ID 가 유효하지 않습니다.");
                return "transfer/error-message/invalid-account-error";
            } else if (e.getRole() == InvalidAccountException.Role.RECEIVER) {
                model.addAttribute("errorMessage", "수취 계좌 ID 가 유효하지 않습니다.");
                return "transfer/error-message/invalid-account-error";
            } else {
                return "transfer/error-message/invalid-account-error";
            }
        } catch (Exception e) {
            // 예기치 못한 에러
            return "transfer/error-message/unexpected-error";
        }

        // 성공
        return "transfer/success-message/success";

    }

}
