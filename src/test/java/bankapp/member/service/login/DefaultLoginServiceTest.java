//package bankapp.member.service.login;
//
//import bankapp.member.dao.MemberDao;
//import bankapp.member.exceptions.IncorrectPasswordException;
//import bankapp.member.exceptions.UsernameNotFoundException;
//import bankapp.member.model.Member;
//import bankapp.member.request.login.LoginRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("DefaultLoginService 테스트")
//class DefaultLoginServiceTest {
//
//    @Mock
//    private MemberDao memberDao;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private DefaultLoginService loginService;
//
//    private LoginRequest loginRequest;
//    private Member mockMember;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트에 사용할 요청 객체 생성
//        loginRequest = new LoginRequest();
//        loginRequest.setUsername("testuser");
//        loginRequest.setPassword("password123");
//
//        // Mock Member 객체 생성
//        mockMember = new Member();
//        mockMember.setMemberId(1L);
//        mockMember.setUsername("testuser");
//        mockMember.setPassword("encodedPassword"); // 암호화된 비밀번호
//        mockMember.setName("테스트유저");
//    }
//
//    @Test
//    @DisplayName("로그인 성공")
//    void login_Success() {
//        // given - 준비
//        // 1. username으로 memberDao를 조회하면 mockMember를 반환하도록 설정
//        when(memberDao.findByUsername("testuser")).thenReturn(mockMember);
//        // 2. passwordEncoder.matches가 true를 반환하도록 설정 (비밀번호 일치)
//        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
//
//        // when - 실행
//        Member result = loginService.login(loginRequest);
//
//        // then - 검증
//        assertThat(result).isNotNull();
//        assertThat(result.getUsername()).isEqualTo("testuser");
//        assertThat(result.getName()).isEqualTo("테스트유저");
//
//        // memberDao와 passwordEncoder의 메서드가 각각 1번씩 호출되었는지 검증
//        verify(memberDao, times(1)).findByUsername("testuser");
//        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 아이디로 로그인 시도 - UsernameNotFoundException 발생")
//    void login_UserNotFound_ThrowsException() {
//        // given
//        // username으로 memberDao를 조회하면 null을 반환하도록 설정
//        when(memberDao.findByUsername("nonexistentuser")).thenReturn(null);
//        loginRequest.setUsername("nonexistentuser");
//
//        // when & then
//        // UsernameNotFoundException이 발생하는지 검증
//        assertThatThrownBy(() -> loginService.login(loginRequest))
//                .isInstanceOf(UsernameNotFoundException.class)
//                .hasMessage("존재하지 않는 아이디입니다.");
//
//        // memberDao.findByUsername은 1번 호출되어야 함
//        verify(memberDao, times(1)).findByUsername("nonexistentuser");
//        // 비밀번호 검증 로직은 실행되지 않아야 함
//        verify(passwordEncoder, never()).matches(anyString(), anyString());
//    }
//
//    @Test
//    @DisplayName("잘못된 비밀번호로 로그인 시도 - IncorrectPasswordException 발생")
//    void login_IncorrectPassword_ThrowsException() {
//        // given
//        // 1. username으로 memberDao를 조회하면 mockMember를 반환하도록 설정
//        when(memberDao.findByUsername("testuser")).thenReturn(mockMember);
//        // 2. passwordEncoder.matches가 false를 반환하도록 설정 (비밀번호 불일치)
//        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
//        loginRequest.setPassword("wrongpassword");
//
//        // when & then
//        // IncorrectPasswordException이 발생하는지 검증
//        assertThatThrownBy(() -> loginService.login(loginRequest))
//                .isInstanceOf(IncorrectPasswordException.class)
//                .hasMessage("비밀번호가 일치하지 않습니다.");
//
//        // memberDao와 passwordEncoder의 메서드가 각각 1번씩 호출되었는지 검증
//        verify(memberDao, times(1)).findByUsername("testuser");
//        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
//    }
//
//    @Test
//    @DisplayName("로그인 요청이 null일 경우 - NullPointerException 발생")
//    void login_NullRequest_ThrowsException() {
//        // when & then
//        // loginRequest가 null일 때 NullPointerException이 발생하는지 검증
//        assertThatThrownBy(() -> loginService.login(null))
//                .isInstanceOf(NullPointerException.class);
//
//        // 어떤 mock 객체도 호출되지 않아야 함
//        verifyNoInteractions(memberDao, passwordEncoder);
//    }
//}