package bankapp.account.repository;

import bankapp.account.model.account.Account;
import bankapp.account.model.account.PrimaryAccount;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // 계좌번호로 계좌 존재 여부 확인
    boolean existsByAccountNumber(String accountNumber);


    // 계좌번호로 계좌 정보 조회
    Optional<Account> findByAccountNumber(String accountNumber);

    // todo : primaryaccount repository 따로 만들고 그곳에 두기
    // memberId로 주계좌(PrimaryAccount) 정보 조회
    @Query("SELECT pa FROM PrimaryAccount pa WHERE pa.member.memberId = :memberId")
    Optional<PrimaryAccount> findPrimaryAccountByMemberId(@Param("memberId") Long memberId);


    // accountId로 주계좌(PrimaryAccount) 정보 조회
    @Query("SELECT a FROM PrimaryAccount a WHERE a.accountId = :accountId")
    Optional<PrimaryAccount> findPrimaryAccountByAccountId(@Param("accountId") Long accountId);


    /**
     * 비관적 쓰기 잠금(PESSIMISTIC_WRITE)을 사용하여 계좌 정보를 조회합니다.
     * 다른 트랜잭션이 해당 레코드에 접근하는 것을 막아 데이터 정합성을 보장합니다.
     * 'javax.persistence.lock.timeout' 힌트는 잠금 대기 시간을 설정합니다 (예: 3초).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    Optional<Account> findById(Long id);
}
