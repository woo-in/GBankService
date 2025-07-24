package bankapp.service.open;

import bankapp.account.create.AccountCreationRequest;

public interface OpenAccountService {

    void openAccount(AccountCreationRequest accountCreationRequest) ;
    String getAccountType() ;

}

// 계좌 종류가 자주 바뀐다 .