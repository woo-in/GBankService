package bankapp.service.find;

import bankapp.account.find.AccountFindRequest;
import bankapp.account.model.BankAccount;


public interface FindAccountService {

    BankAccount findAccount(AccountFindRequest accountFindRequest) ;

}
