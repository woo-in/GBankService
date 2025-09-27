package bankapp.member.repository;

import bankapp.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 메서드 이름만으로 쿼리를 자동 생성합니다.
    // SELECT * FROM MEMBER WHERE username = ?
    Optional<Member> findByUsername(String username);

    // SELECT COUNT(*) > 0 FROM MEMBER WHERE username = ?
    boolean existsByUsername(String username);

    // findByMemberId -> JpaRepository에 이미 findById 라는 이름으로 구현되어 있습니다.
    // existsByMemberId -> JpaRepository에 이미 existsById 라는 이름으로 구현되어 있습니다.
    // insertMember -> JpaRepository에 이미 save 라는 이름으로 구현되어 있습니다.
}