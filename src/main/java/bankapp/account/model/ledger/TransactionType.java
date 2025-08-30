package bankapp.account.model.ledger;

public enum TransactionType {
    DEBIT,       // 출금
    CREDIT,      // 입금
    TRANSFER,    // 이체
    INTEREST     // 이자
    // ... 추후 대출(LOAN) 등 추가 가능
}

