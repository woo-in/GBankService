package bankapp.account.dao.ledger;

import bankapp.account.model.ledger.AccountLedger;

import java.util.Optional;

public interface AccountLedgerDao {

    /**
     * AccountLedger 객체를 데이터베이스에 저장합니다.
     * @Param accountledger 저장할 원장 객체
     * @return 데이터베이스에 의해 생성된 ledger_id, created_at이 포함된, 최종 저장된 객체
     */
    AccountLedger save(AccountLedger accountledger);


    /**
     * ledger_id를 사용하여 단일 원장 기록을 조회합니다.
     * @param ledgerId 조회할 원장 기록의 ID
     * @return 조회된 AccountLedger 객체를 담은 Optional
     */
    Optional<AccountLedger> findById(Long ledgerId);

}
