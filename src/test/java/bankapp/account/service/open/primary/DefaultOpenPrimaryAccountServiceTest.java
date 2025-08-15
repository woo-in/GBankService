package bankapp.account.service.open.primary;

import bankapp.account.dao.AccountDao;
import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.manager.MemberManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultOpenPrimaryAccountService 테스트")
class DefaultOpenPrimaryAccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private MemberManager memberManager;

    @InjectMocks
    private DefaultOpenPrimaryAccountService openPrimaryAccountService;

    private OpenPrimaryAccountRequest request;

    @BeforeEach
    void setUp() {
        request = new OpenPrimaryAccountRequest();
        request.setMemberId(1L);
        request.setNickname("내 첫 계좌");
        request.setBalance(new BigDecimal("10000"));
    }

    @Test
    @DisplayName("계좌 개설 성공")
    void openPrimaryAccount_Success() {
        // given
        // 1. 멤버가 존재한다고 가정
        when(memberManager.isMemberIdExist(1L)).thenReturn(true);

        // 2. DAO가 계좌 정보를 저장하면, 완성된 PrimaryAccount 객체를 반환한다고 가정
        PrimaryAccount savedAccount = new PrimaryAccount();
        savedAccount.setAccountId(100L);
        savedAccount.setMemberId(1L);
        savedAccount.setNickname("내 첫 계좌");
        savedAccount.setBalance(new BigDecimal("10000"));
        savedAccount.setAccountType("PRIMARY");
        savedAccount.setAccountNumber("110-250815-123456"); // 예시 번호
        when(accountDao.insertAccount(any(PrimaryAccount.class))).thenReturn(savedAccount);

        // when
        PrimaryAccount resultAccount = openPrimaryAccountService.openPrimaryAccount(request);

        // then
        // 1. 멤버 존재 여부를 1회 확인했는지 검증
        verify(memberManager, times(1)).isMemberIdExist(1L);

        // 2. DAO에 전달된 PrimaryAccount 객체를 캡처하여 값 검증
        ArgumentCaptor<PrimaryAccount> accountCaptor = ArgumentCaptor.forClass(PrimaryAccount.class);
        verify(accountDao, times(1)).insertAccount(accountCaptor.capture());
        PrimaryAccount capturedAccount = accountCaptor.getValue();

        assertThat(capturedAccount.getMemberId()).isEqualTo(1L);
        assertThat(capturedAccount.getNickname()).isEqualTo("내 첫 계좌");
        assertThat(capturedAccount.getBalance()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(capturedAccount.getAccountType()).isEqualTo("PRIMARY");
        assertThat(capturedAccount.getAccountNumber()).isNotNull().containsPattern("^\\d{3}-\\d{6}-\\d{6}$"); // 계좌번호 형식 검증

        // 3. 서비스가 DAO에서 반환된 객체를 그대로 반환하는지 검증
        assertThat(resultAccount).isEqualTo(savedAccount);
    }

    @Test
    @DisplayName("존재하지 않는 멤버 ID로 계좌 개설 시도 - MemberNotFoundException 발생")
    void openPrimaryAccount_MemberNotFound_ThrowsException() {
        // given
        // 멤버가 존재하지 않는다고 가정
        when(memberManager.isMemberIdExist(1L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> openPrimaryAccountService.openPrimaryAccount(request))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("memberId 1not found.");

        // 멤버가 없으므로 DAO는 호출되지 않아야 함
        verify(accountDao, never()).insertAccount(any(PrimaryAccount.class));
    }

    @Test
    @DisplayName("음수 금액으로 계좌 개설 시도 - InvalidDepositAmountException 발생")
    void openPrimaryAccount_InvalidDeposit_ThrowsException() {
        // given

        // 1. 요청 객체에 음수 금액 설정
        request.setBalance(new BigDecimal("-1000"));

        // when & then
        assertThatThrownBy(() -> openPrimaryAccountService.openPrimaryAccount(request))
                .isInstanceOf(InvalidDepositAmountException.class)
                .hasMessage("입금액은 0원 이상이어야 합니다.");

        // 금액이 잘못되었으므로 DAO는 호출되지 않아야 함
        verify(accountDao, never()).insertAccount(any(PrimaryAccount.class));
    }
}
