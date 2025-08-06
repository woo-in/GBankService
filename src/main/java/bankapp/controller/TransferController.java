package bankapp.controller;

import bankapp.request.transfer.AccountTransferRequest;
import bankapp.exceptions.InsufficientFundsException;
import bankapp.exceptions.InvalidAccountException;
import bankapp.service.BankAccountManager;
import bankapp.validator.transfer.AccountTransferRequestValidator;
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
@RequestMapping("/transfer")
public class TransferController {

    private final BankAccountManager accountManager;
    // 두 계좌가 같은지 다른지 비교하기 위해 필요함.
    private final AccountTransferRequestValidator accountTransferRequestValidator;

    @Autowired
    public TransferController(BankAccountManager accountManager) {
        this.accountManager = accountManager;
        this.accountTransferRequestValidator = new AccountTransferRequestValidator();
    }

    @InitBinder("accountTransferRequest")
    public void accountTransferRequestBinder(WebDataBinder binder){
        binder.addValidators(accountTransferRequestValidator);
    }


    // 송금 폼
    @GetMapping("")
    public String showTransferForm(Model model){
        model.addAttribute("accountTransferRequest", new AccountTransferRequest());
        return "transfer/form/transfer";
    }

    // 송금
    @PostMapping("")
    public String processTransfer(@Validated @ModelAttribute AccountTransferRequest accountTransferRequest , BindingResult bindingResult , Model model) {

        if (bindingResult.hasErrors()) {
            // 값 유효하지 않음
            return "transfer/form/transfer";
        }

        try {
            // 송금 처리
            accountManager.transfer(accountTransferRequest);
        }
        catch (InvalidAccountException e) {
            // 존재하지 않는 계좌
            if (e.getRole() == InvalidAccountException.Role.SENDER) {
                bindingResult.rejectValue("senderNumber" , "invalid" ,"송금 계좌 ID 가 유효하지 않습니다." );
                return "transfer/form/transfer";
            } else if (e.getRole() == InvalidAccountException.Role.RECEIVER) {
                bindingResult.rejectValue("receiverNumber" , "invalid" ,"수취 계좌 ID 가 유효하지 않습니다." );
                return "transfer/form/transfer";
            } else {
                return "transfer/error-message/unexpected-error";
            }
        }
        catch (InsufficientFundsException e) {
            // 잔액 부족
            bindingResult.rejectValue("amount" , "invalid" , "잔액이 부족합니다.");
            return "transfer/form/transfer";
        }
        catch (Exception e) {
            // 예기치 못한 에러
            return "transfer/error-message/unexpected-error";
        }

        // 성공
        return "transfer/success-message/success";

    }

}
