package bankapp.account.dao.ledger;

import bankapp.account.model.ledger.AccountLedger;

public interface AccountLedgerDao {

    /**
     * AccountLedger 객체를 데이터베이스에 저장합니다.
     *
     * @Param 저장할 원장 객체
     * @return 데이터베이스에 의해 생성된 ledger_id, created_at이 포함된, 최종 저장된 객체
     */
    AccountLedger save(AccountLedger accountledger);

}
