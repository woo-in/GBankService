package bankapp.account.model.ledger;

import bankapp.account.model.account.Account;
import bankapp.account.request.account.AccountTransactionRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class AccountLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerId;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "account_id" , nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private String description;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;



    /**
     * AccountTransactionRequest DTO와 추가 정보를 바탕으로 AccountLedger 객체를 생성합니다.
     * @param accountTransactionRequest 사용자 요청 DTO
     * @param type 거래 유형
     * @param balanceAfter 거래 후 잔액
     * @return 생성된 AccountLedger 객체
     */
    public static AccountLedger from(Account account , AccountTransactionRequest accountTransactionRequest, TransactionType type, BigDecimal balanceAfter) {
        return new AccountLedger(account ,type , accountTransactionRequest.getAmount() , balanceAfter, accountTransactionRequest.getDescription());
    }


    public AccountLedger(Account account, TransactionType transactionType, BigDecimal amount, BigDecimal balanceAfter, String description) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public AccountLedger(Account account, TransactionType transactionType, BigDecimal amount, BigDecimal balanceAfter) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

}
