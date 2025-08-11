package bankapp.account.model;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Account {

    private Long accountId;
    private Long memberId;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private String nickname;
    private LocalDateTime createdAt;

    public Account() { }
    public Account(Long accountId, Long memberId, String accountNumber, BigDecimal balance, String accountType, String nickname, LocalDateTime createdAt) {
        this.accountId = accountId;
        this.memberId = memberId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}
