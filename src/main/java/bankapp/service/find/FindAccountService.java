package bankapp.service.find;

import bankapp.request.find.AccountFindRequest;
import bankapp.model.account.BankAccount;


public interface FindAccountService {

    BankAccount findAccount(AccountFindRequest accountFindRequest) ;

}
