package bankapp.account.dao.account;

import bankapp.account.model.account.Account;
import bankapp.account.model.account.PrimaryAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 계좌(Account) 데이터에 접근하는 데이터 접근 객체(DAO)의 명세를 정의합니다.
 */
public interface AccountDao {

    /**
     * 신규 계좌 정보를 데이터베이스에 저장합니다.
     * 제네릭을 사용하여 PrimaryAccount 등 Account의 모든 자식 타입을 처리할 수 있습니다.
     *
     * @param account 저장할 계좌 정보 (ID는 null 상태여야 함)
     * @param <T> Account를 상속받는 구체적인 계좌 타입
     * @return 데이터베이스에 의해 생성된 ID가 포함된 완전한 계좌 엔티티
     */
    <T extends Account> T insertAccount(T account);

    /**
     * 계좌번호를 사용하여 계좌를 조회합니다.
     *
     * @param accountNumber 조회할 계좌번호
     * @return 계좌가 존재할 경우 해당 Account 객체를 담은 Optional, 존재하지 않을 경우 비어있는 Optional
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * 계좌 고유 ID를 사용하여 계좌를 조회합니다.
     *
     * @param accountId 조회할 계좌 ID
     * @return 계좌가 존재할 경우 해당 Account 객체를 담은 Optional, 존재하지 않을 경우 비어있는 Optional
     */
    Optional<Account> findByAccountId(Long accountId);

    /**
     * 이 메서드는 SELECT 쿼리에 FOR UPDATE 절을 추가하여,
     * 조회된 로우(row)에 배타적 잠금(Exclusive Lock)을 설정합니다.
     * 이를 통해 다른 트랜잭션이 해당 로우를 동시에 수정하는 것을 방지할 수 있습니다.
     * <p>
     * <b>주의:</b> 이 메서드는 반드시 @Transactional이 선언된 서비스 메서드 내에서 호출되어야만
     * 트랜잭션이 끝날 때까지 잠금이 유지됩니다.
     */
    Optional<Account> findByIdForUpdate(Long accountId);

    /**
     * 회원의 고유 ID를 사용하여 해당 회원이 소유한 모든 계좌 목록을 조회합니다.
     *
     * @param memberId 계좌 목록을 조회할 회원의 고유 ID
     * @return 해당 회원의 계좌 리스트. 계좌가 없을 경우 비어있는 리스트를 반환합니다.
     */
    List<Account> findByMemberId(Long memberId);


    /**
     * 주어진 계좌번호에 해당하는 계좌의 존재 여부를 확인합니다.
     *
     * @param accountNumber 계좌 목록을 조회할 계좌번호
     * @return 계좌가 있는지 여부 반환
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * 특정 회원의 주계좌(PRIMARY) 정보를 조회 , 반환합니다.
     *
     * @param memberId 주 계좌를 조회할 회원의 고유 ID
     * @return 계좌가 존재할 경우 해당 PrimaryAccount 객체를 담은 Optional, 존재하지 않을 경우 비어있는 Optional
     */
    Optional<PrimaryAccount> findPrimaryAccountByMemberId(Long memberId) ;



    /**
     * 특정 계좌의 주계좌(PRIMARY) 정보를 조회 , 반환합니다.
     *
     * @param accountId 주 계좌를 조회할 회원의 고유 ID
     * @return 계좌가 존재할 경우 해당 PrimaryAccount 객체를 담은 Optional, 존재하지 않을 경우 비어있는 Optional
     */
    Optional<PrimaryAccount> findPrimaryAccountByAccountId(Long accountId) ;


    /**
     * 계좌 잔액을 지정된 값으로 설정합니다. (절대적 변경: balance = ?)
     * 서비스단에서 비관적 잠금 후, 계산된 최종 값을 저장할 때 사용됩니다.
     * @param accountId 계좌 ID
     * @param newBalance 설정할 새로운 최종 잔액
     */
    void setBalance(Long accountId, BigDecimal newBalance);





}
