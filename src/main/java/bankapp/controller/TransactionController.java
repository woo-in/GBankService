package bankapp.controller;


import bankapp.request.find.AccountFindRequest;
import bankapp.model.account.BankAccount;
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
@RequestMapping("/transaction")
public class TransactionController {

    private final BankAccountManager accountManager;

    @Autowired
    public TransactionController(BankAccountManager accountManager) { this.accountManager = accountManager; }

    // 조회 폼
    @GetMapping("")
    public String showTransactionForm(){
        return "transaction/form/input";
    }

    // 처리
    @PostMapping("")
    public String processTransaction(@ModelAttribute AccountFindRequest accountFindRequest , Model model) {

        BankAccount found;
        try{
            found = accountManager.findAccount(accountFindRequest);
        }
        catch(InvalidAccountException e) {
            // 존재하지 않는 계좌
            return "transaction/error-message/invalid-account-error";
        }
        catch(Exception e) {
            // 예기치 못한 에러
            return "transaction/error-message/unexpected-error";
        }

        // 성공
        // 계좌 id 바탕으로 거래내역 가져오는 서비스 함수
        // model 에 최근 10개의 거래내역을 넣어주어야 함.
        return "transaction/success-message/success";
    }






}
