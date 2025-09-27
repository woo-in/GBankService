package bankapp.member.service.check;

import bankapp.account.model.account.Account;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.model.Member;
import bankapp.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit5에서 Mockito를 사용하기 위한 확장 기능
class DefaultMemberCheckServiceTest {

    @Mock // 가짜(Mock) MemberRepository 객체 생성
    private MemberRepository memberRepository;

    @InjectMocks // @Mock으로 생성된 가짜 객체를 주입할 대상
    private DefaultMemberCheckService memberCheckService;


    @Test
    @DisplayName("회원 ID가 존재할 때 isMemberIdExist는 true를 반환한다")
    void isMemberIdExist_WhenIdExists_ShouldReturnTrue() {
        // given - 준비
        Long memberId = 1L;
        when(memberRepository.existsById(memberId)).thenReturn(true);

        // when - 실행
        boolean result = memberCheckService.isMemberIdExist(memberId);

        // then - 검증
        assertTrue(result);
        verify(memberRepository).existsById(memberId); // existsById가 1L 인자로 호출되었는지 확인
    }

    @Test
    @DisplayName("회원 ID가 null일 때 isMemberIdExist는 false를 반환하고 DB를 조회하지 않는다")
    void isMemberIdExist_WhenIdIsNull_ShouldReturnFalse() {
        // given
        Long memberId = null;

        // when
        boolean result = memberCheckService.isMemberIdExist(memberId);

        // then
        assertFalse(result);
        verify(memberRepository, never()).existsById(any()); // existsById가 어떤 인자로도 호출되지 않았음을 확인
    }

    @Test
    @DisplayName("사용자 이름이 존재할 때 isUsernameExist는 true를 반환한다")
    void isUsernameExist_WhenUsernameExists_ShouldReturnTrue() {
        // given
        String username = "testuser";
        when(memberRepository.existsByUsername(username)).thenReturn(true);

        // when
        boolean result = memberCheckService.isUsernameExist(username);

        // then
        assertTrue(result);
        verify(memberRepository).existsByUsername(username);
    }

//    @Test
//    @DisplayName("유효한 계정 정보로 회원을 성공적으로 조회한다")
//    void findMemberByAccount_WithValidAccount_ShouldReturnMember() {
//        // given
//        Account account = new Account("111-222", 1L, "password");
//        Member member = new Member();
//        member.setMemberId(1L);
//        member.setUsername("testuser");
//
//        when(memberRepository.findById(account.getMemberId())).thenReturn(Optional.of(member));
//
//        // when
//        Member foundMember = memberCheckService.findMemberByAccount(account);
//
//        // then
//        assertNotNull(foundMember);
//        assertEquals(member.getMemberId(), foundMember.getMemberId());
//        verify(memberRepository).findById(account.getMemberId());
//    }

//    @Test
//    @DisplayName("계정에 해당하는 회원이 없을 때 MemberNotFoundException 예외를 던진다")
//    void findMemberByAccount_WhenMemberNotFound_ShouldThrowException() {
//        // given
//        Account account = new Account("111-222", 99L, "password"); // 존재하지 않는 ID
//        when(memberRepository.findById(account.getMemberId())).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(MemberNotFoundException.class, () -> {
//            memberCheckService.findMemberByAccount(account);
//        }, "계정에 해당하는 멤버를 찾을 수 없습니다.");
//
//        verify(memberRepository).findById(account.getMemberId());
//    }
//
//    @Test
//    @DisplayName("계정 정보가 null일 때 MemberNotFoundException 예외를 던진다")
//    void findMemberByAccount_WhenAccountIsNull_ShouldThrowException() {
//        // given
//        Account account = null;
//
//        // when & then
//        assertThrows(MemberNotFoundException.class, () -> {
//            memberCheckService.findMemberByAccount(account);
//        });
//
//        verify(memberRepository, never()).findById(any()); // DB 조회가 일어나면 안 됨
//    }
}