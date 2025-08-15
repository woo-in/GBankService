package bankapp.account.manager;

import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.account.service.open.primary.OpenPrimaryAccountService;
import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.member.exceptions.MemberNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class AccountManager {

    private final OpenPrimaryAccountService openPrimaryAccountService;

    @Autowired
    public AccountManager(OpenPrimaryAccountService openPrimaryAccountService){
        this.openPrimaryAccountService = openPrimaryAccountService;
    }

    public PrimaryAccount openPrimaryAccount(OpenPrimaryAccountRequest openPrimaryAccountRequest) throws MemberNotFoundException , InvalidDepositAmountException{
        return openPrimaryAccountService.openPrimaryAccount(openPrimaryAccountRequest);
    }


}
