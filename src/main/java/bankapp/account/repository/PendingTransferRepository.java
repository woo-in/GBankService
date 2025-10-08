package bankapp.account.repository;

import bankapp.account.model.transfer.PendingTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingTransferRepository extends JpaRepository<PendingTransfer, String> {
}
