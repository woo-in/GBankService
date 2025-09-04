package bankapp.member.dao;

import bankapp.member.model.Member;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

/**
 * 회원(Member) 데이터에 접근하는 데이터 접근 객체(DAO)의 명세를 정의합니다.
 */
public interface MemberDao {

    /**
     * 신규 회원 정보를 데이터베이스에 저장합니다.
     *
     * @param member 저장할 회원 정보 (ID는 null 상태여야 함)
     * @return 데이터베이스에 의해 생성된 ID가 포함된 완전한 회원 엔티티
     */
    Member insertMember(@NotNull(message = "insertMember(Member) cannot be null") Member member);

    /**
     * 사용자 아이디(username)를 사용하여 회원을 조회합니다.
     *
     * @param username 조회할 사용자 아이디
     * @return 회원이 존재할 경우 해당 Member 객체를 담은 Optional, 존재하지 않을 경우 비어있는 Optional
     */
    Optional<Member> findByUsername(String username);

    /**
     * 회원의 고유 ID(PK)를 사용하여 회원을 조회합니다.
     *
     * @param memberId 조회할 회원의 고유 ID
     * @return 회원이 존재할 경우 해당 Member 객체를 담은 Optional, 존재하지 않을 경우 비어있는 Optional
     */
    Optional<Member> findByMemberId(Long memberId);

    /**
     * member 식별자의 존재 여부를 확인합니다.
     * @param memberId 회원 ID
     * @return 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByMemberId(Long memberId);

    /**
     * user 사용 아이디 존재 여부를 확인합니다.
     * @param username user 사용 아이디
     * @return 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByUsername(String username);

}
