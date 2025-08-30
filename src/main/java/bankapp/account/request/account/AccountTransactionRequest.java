package bankapp.account.request.account;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTransactionRequest {
    private Long accountId;
    private BigDecimal amount;
    private String description;

    public AccountTransactionRequest(Long accountId, BigDecimal amount, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
    }
}
