package bankapp.account.repository;

import bankapp.account.model.ledger.AccountLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLedgerRepository extends JpaRepository<AccountLedger, Long> {
}
