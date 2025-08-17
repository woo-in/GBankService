package bankapp.account.manager;

import bankapp.account.exceptions.PrimaryAccountNotFoundException;
import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.account.service.open.primary.OpenPrimaryAccountService;
import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.account.service.check.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class AccountManager {

    private final OpenPrimaryAccountService openPrimaryAccountService;
    private final CheckService checkService;


    @Autowired
    public AccountManager(OpenPrimaryAccountService openPrimaryAccountService , CheckService checkService) {
        this.openPrimaryAccountService = openPrimaryAccountService;
        this.checkService = checkService;
    }

    public PrimaryAccount openPrimaryAccount(OpenPrimaryAccountRequest openPrimaryAccountRequest) throws MemberNotFoundException , InvalidDepositAmountException{
        return openPrimaryAccountService.openPrimaryAccount(openPrimaryAccountRequest);
    }

    public boolean isAccountNumberExist(String accountNumber){
        return checkService.isAccountNumberExist(accountNumber);
    }

    public PrimaryAccount findPrimaryAccountByMemberId(Long memberId) throws PrimaryAccountNotFoundException {
        return checkService.findPrimaryAccountByMemberId(memberId);
    }


}
