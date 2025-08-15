package bankapp.member.service.signup;

import bankapp.account.manager.AccountManager;
import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.member.dao.MemberDao;
import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.model.Member;
import bankapp.member.request.signup.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultSignUpService 테스트")
class DefaultSignUpServiceTest {

    @Mock
    private MemberDao memberDao;

    @Mock
    private AccountManager accountManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DefaultSignUpService signUpService;

    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {
        // 정상적인 회원가입 요청 객체 생성 (기본 생성자 + 세터 사용)
        signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("newUser");
        signUpRequest.setPassword("Password123!");
        signUpRequest.setPasswordConfirm("Password123!");
        signUpRequest.setName("신규사용자");
        signUpRequest.setAccountNickname("내주거래계좌");
    }


    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // given
        // 1. 중복된 아이디가 없는 상황을 가정 (findByUsername -> null 반환)
        when(memberDao.findByUsername("newUser")).thenReturn(null);

        // 2. 비밀번호가 암호화되면 "encodedPassword"가 될 것이라고 가정
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");

        // 3. 회원 정보가 저장되면, ID가 1L로 설정된 Member 객체가 반환된다고 가정
        Member savedMember = new Member();
        savedMember.setMemberId(1L);
        when(memberDao.insertMember(any(Member.class))).thenReturn(savedMember);

        // 4. 계좌 생성 메서드가 호출되면, 새로운 PrimaryAccount 객체를 반환하도록 수정
        when(accountManager.openPrimaryAccount(any(OpenPrimaryAccountRequest.class))).thenReturn(new PrimaryAccount());

        // when
        signUpService.signUp(signUpRequest);

        // then
        // 1. username 중복 검사를 1회 수행했는지 확인
        verify(memberDao, times(1)).findByUsername("newUser");

        // 2. 비밀번호 암호화를 1회 수행했는지 확인
        verify(passwordEncoder, times(1)).encode("Password123!");

        // 3. insertMember에 전달된 Member 객체를 캡처하여 값이 올바르게 설정되었는지 확인
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberDao, times(1)).insertMember(memberCaptor.capture());
        Member capturedMember = memberCaptor.getValue();
        assertThat(capturedMember.getUsername()).isEqualTo("newUser");
        assertThat(capturedMember.getPassword()).isEqualTo("encodedPassword");
        assertThat(capturedMember.getName()).isEqualTo("신규사용자");

        // 4. 계좌 생성 로직을 1회 호출했는지 확인
        verify(accountManager, times(1)).openPrimaryAccount(any());
    }

    @Test
    @DisplayName("중복된 아이디로 회원가입 시도 - DuplicateUsernameException 발생")
    void signUp_DuplicateUsername_ThrowsException() {
        // given
        // 이미 "existingUser"라는 아이디가 존재한다고 가정
        when(memberDao.findByUsername("existingUser")).thenReturn(new Member());
        signUpRequest.setUsername("existingUser");

        // when & then
        assertThatThrownBy(() -> signUpService.signUp(signUpRequest))
                .isInstanceOf(DuplicateUsernameException.class)
                .hasMessage("이미 존재하는 아이디입니다.");

        // 아이디 중복 검사 후 로직이 중단되었으므로, 다른 의존성 객체들은 호출되지 않아야 함
        verify(passwordEncoder, never()).encode(anyString());
        verify(memberDao, never()).insertMember(any(Member.class));
        verify(accountManager, never()).openPrimaryAccount(any());
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 일치하지 않을 경우 - PasswordMismatchException 발생")
    void signUp_PasswordMismatch_ThrowsException() {
        // given
        // 비밀번호와 비밀번호 확인이 다른 요청 객체 생성
        signUpRequest.setPasswordConfirm("MismatchedPassword123!");

        // when & then
        assertThatThrownBy(() -> signUpService.signUp(signUpRequest))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");

        // 비밀번호 검증 단계에서 실패했으므로, 어떤 의존성 객체도 호출되지 않아야 함
        verifyNoInteractions(memberDao, passwordEncoder, accountManager);
    }
}