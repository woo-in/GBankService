package bankapp.account.dao;

import bankapp.account.model.Account;

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
     * 회원의 고유 ID를 사용하여 해당 회원이 소유한 모든 계좌 목록을 조회합니다.
     *
     * @param memberId 계좌 목록을 조회할 회원의 고유 ID
     * @return 해당 회원의 계좌 리스트. 계좌가 없을 경우 비어있는 리스트를 반환합니다.
     */
    List<Account> findByMemberId(Long memberId);
}
