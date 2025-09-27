package bankapp.account.model.ledger;

import bankapp.account.request.account.AccountTransactionRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountLedger {

    private Long ledgerId;
    private Long accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime createdAt;


    /**
     * AccountTransactionRequest DTO와 추가 정보를 바탕으로 AccountLedger 객체를 생성합니다.
     * @param accountTransactionRequest 사용자 요청 DTO
     * @param type 거래 유형
     * @param balanceAfter 거래 후 잔액
     * @return 생성된 AccountLedger 객체
     */
    public static AccountLedger from(AccountTransactionRequest accountTransactionRequest, TransactionType type, BigDecimal balanceAfter) {
        AccountLedger ledger = new AccountLedger();
        ledger.setAccountId(accountTransactionRequest.getAccountId());
        ledger.setAmount(accountTransactionRequest.getAmount());
        ledger.setDescription(accountTransactionRequest.getDescription());
        ledger.setTransactionType(type);
        ledger.setBalanceAfter(balanceAfter);
        return ledger;
    }


}
