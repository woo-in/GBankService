package bankapp.account.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public abstract class Account {

    protected Long accountId;
    protected Long memberId;
    protected String accountNumber;
    protected BigDecimal balance;
    protected String accountType;
    protected String nickname;
    protected LocalDateTime createdAt;

}
