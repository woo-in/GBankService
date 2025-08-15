package bankapp.account.response;


import bankapp.account.model.PrimaryAccount;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountResponse {

    private String accountNumber;
    private BigDecimal balance;
    private String accountType;

    // 직접 생성 막음
    private AccountResponse() { }

    public static AccountResponse from(PrimaryAccount account){
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setAccountType(account.getAccountType());
        return accountResponse;
    }




}
