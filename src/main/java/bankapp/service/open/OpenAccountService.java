package bankapp.service.open;

import bankapp.request.open.AccountCreationRequest;

public interface OpenAccountService {

    void openAccount(AccountCreationRequest accountCreationRequest) ;
    String getAccountType() ;

}

// 계좌 종류가 자주 바뀐다 .