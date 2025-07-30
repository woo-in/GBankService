package bankapp.service.deposit;


import bankapp.request.deposit.AccountDepositRequest;

public interface DepositService {

    void deposit(AccountDepositRequest accountDepositRequest) ;

}
