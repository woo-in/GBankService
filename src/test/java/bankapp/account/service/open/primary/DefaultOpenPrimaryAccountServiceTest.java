package bankapp.account.service.open.primary;

import bankapp.account.dao.account.AccountDao;
import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.account.model.account.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.account.service.open.component.AccountNumberGenerator;
import bankapp.account.service.open.component.AccountOpeningValidator;
import bankapp.member.exceptions.MemberNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultOpenPrimaryAccountServiceTest {

    @Mock
    private AccountDao accountDao;
    @Mock
    private AccountOpeningValidator validator;
    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private DefaultOpenPrimaryAccountService openPrimaryAccountService;

    @Test
    @DisplayName("계좌 개설 성공: 모든 의존성이 올바르게 호출되고 최종 계좌가 반환된다")
    void openPrimaryAccount_shouldSucceed_whenValidationPasses() {
        // given - 준비
        OpenPrimaryAccountRequest request = new OpenPrimaryAccountRequest(1L, new BigDecimal("10000"), "내 주계좌");
        String generatedAccountNumber = "110-250902-123456";

        // Mock 객체들의 행동 정의
        // 1. Validator는 예외 없이 정상 통과
        doNothing().when(validator).validate(request);
        // 2. Generator는 지정된 계좌번호를 반환
        when(accountNumberGenerator.generate()).thenReturn(generatedAccountNumber);
        // 3. DAO는 어떤 PrimaryAccount 객체든 받으면, 받은 그대로 반환
        when(accountDao.insertAccount(any(PrimaryAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when - 실행
        PrimaryAccount resultAccount = openPrimaryAccountService.openPrimaryAccount(request);

        // then - 검증
        // 1. 각 의존성들이 정확히 1번씩 호출되었는지 확인
        verify(validator, times(1)).validate(request);
        verify(accountNumberGenerator, times(1)).generate();
        verify(accountDao, times(1)).insertAccount(any(PrimaryAccount.class));

        // 2. DAO에 저장 요청된 Account 객체를 캡처하여 검증
        ArgumentCaptor<PrimaryAccount> accountCaptor = ArgumentCaptor.forClass(PrimaryAccount.class);
        verify(accountDao).insertAccount(accountCaptor.capture());
        PrimaryAccount capturedAccount = accountCaptor.getValue();

        assertThat(capturedAccount.getMemberId()).isEqualTo(request.getMemberId());
        assertThat(capturedAccount.getBalance()).isEqualTo(request.getBalance());
        assertThat(capturedAccount.getNickname()).isEqualTo(request.getNickname());
        assertThat(capturedAccount.getAccountNumber()).isEqualTo(generatedAccountNumber);
        assertThat(capturedAccount.getAccountType()).isEqualTo("PRIMARY");

        // 3. 최종 반환된 결과가 null이 아닌지 확인
        assertThat(resultAccount).isNotNull();
        assertThat(resultAccount.getAccountNumber()).isEqualTo(generatedAccountNumber);
    }

    @Test
    @DisplayName("계좌 개설 실패: Validator가 InvalidDepositAmountException을 던지면 그대로 전파한다")
    void openPrimaryAccount_shouldThrowException_whenValidatorFailsWithInvalidAmount() {
        // given
        OpenPrimaryAccountRequest request = new OpenPrimaryAccountRequest(1L, new BigDecimal("-100"), "잘못된 요청");
        // Validator가 특정 예외를 던지도록 설정
        doThrow(new InvalidDepositAmountException("입금액은 0원 이상이어야 합니다.")).when(validator).validate(request);

        // when & then
        assertThatThrownBy(() -> openPrimaryAccountService.openPrimaryAccount(request))
                .isInstanceOf(InvalidDepositAmountException.class);

        // Validator에서 실패했으므로, 다른 의존성들은 절대 호출되면 안 됨
        verify(accountNumberGenerator, never()).generate();
        verify(accountDao, never()).insertAccount(any());
    }

    @Test
    @DisplayName("계좌 개설 실패: Validator가 MemberNotFoundException을 던지면 그대로 전파한다")
    void openPrimaryAccount_shouldThrowException_whenValidatorFailsWithMemberNotFound() {
        // given
        OpenPrimaryAccountRequest request = new OpenPrimaryAccountRequest(99L, BigDecimal.ZERO, "없는 회원");
        doThrow(new MemberNotFoundException("memberId 99 not found.")).when(validator).validate(request);

        // when & then
        assertThatThrownBy(() -> openPrimaryAccountService.openPrimaryAccount(request))
                .isInstanceOf(MemberNotFoundException.class);

        // 마찬가지로 다른 의존성들은 호출되지 않아야 함
        verifyNoInteractions(accountNumberGenerator);
        verifyNoInteractions(accountDao);
    }
}