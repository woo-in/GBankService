package bankapp.account.repository;

import bankapp.account.model.account.PrimaryAccount;
import bankapp.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrimaryAccountRepository extends JpaRepository<PrimaryAccount, Long> {

    Optional<PrimaryAccount> findByMember(Member member);

}
