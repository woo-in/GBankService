package bankapp.service.withdraw;


import bankapp.request.withdraw.AccountWithdrawRequest;

public interface WithdrawService {
     void withdraw(AccountWithdrawRequest accountWithdrawRequest);
}
