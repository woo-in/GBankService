package bankapp.account.service.check;

import bankapp.account.model.PrimaryAccount;

public interface CheckService {

    boolean isAccountNumberExist(String accountNumber);
    PrimaryAccount findPrimaryAccountByMemberId(Long memberId);



}
