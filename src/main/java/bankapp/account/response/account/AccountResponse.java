package bankapp.account.response.account;


import bankapp.account.model.account.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {

    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private String nickname;
    private LocalDateTime createdAt;


    // 직접 생성 막음
    private AccountResponse() { }

    public static AccountResponse from(Account account){
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setAccountType(account.getAccountType());
        accountResponse.setNickname(account.getNickname());
        accountResponse.setCreatedAt(account.getCreatedAt());
        return accountResponse;
    }




}
