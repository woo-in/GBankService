package bankapp.account.model.ledger;

import java.math.BigDecimal;

public enum TransactionType {
    DEBIT("출금") {
        @Override
        public BigDecimal calculate(BigDecimal balance, BigDecimal amount) {
            return balance.subtract(amount);
        }
    },
    CREDIT("입금") {
        @Override
        public BigDecimal calculate(BigDecimal balance, BigDecimal amount) {
            return balance.add(amount);
        }
    };

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public abstract BigDecimal calculate(BigDecimal balance, BigDecimal amount);
}


