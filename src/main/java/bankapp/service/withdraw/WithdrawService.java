package bankapp.service.withdraw;


import bankapp.account.withdraw.AccountWithdrawRequest;

public interface WithdrawService {
     void withdraw(AccountWithdrawRequest accountWithdrawRequest);
}
