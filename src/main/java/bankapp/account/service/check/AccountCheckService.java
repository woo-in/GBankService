package bankapp.account.service.check;

import bankapp.account.model.Account;
import bankapp.account.model.PrimaryAccount;

public interface AccountCheckService {

    boolean isAccountNumberExist(String accountNumber);
    PrimaryAccount findPrimaryAccountByMemberId(Long memberId);
    Account findAccountByAccountNumber(String accountNumber);
    boolean isExternalBank(String bankCode);

}
