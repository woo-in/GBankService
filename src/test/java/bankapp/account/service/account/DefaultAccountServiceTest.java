package bankapp.account.service.account;

import bankapp.account.dao.account.AccountDao;
import bankapp.account.dao.ledger.AccountLedgerDao;
import bankapp.account.exceptions.AccountNotFoundException;
import bankapp.account.exceptions.InsufficientBalanceException;
import bankapp.account.exceptions.InvalidAmountException;
import bankapp.account.model.account.PrimaryAccount;
import bankapp.account.model.ledger.AccountLedger;
import bankapp.account.request.account.AccountTransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultAccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private AccountLedgerDao accountLedgerDao;

    @InjectMocks
    private DefaultAccountService accountService;

    private PrimaryAccount account;
    private AccountTransactionRequest request;
    private final Long ACCOUNT_ID = 1L;
    private final long LEDGER_ID = 100L;

    @BeforeEach
    void setUp() {
        // 테스트에서 공통으로 사용할 객체 초기화
        account = new PrimaryAccount();
        account.setAccountId(ACCOUNT_ID);
        account.setBalance(new BigDecimal("10000"));

        request = new AccountTransactionRequest(ACCOUNT_ID, new BigDecimal("1000"),"transfer");
    }

    @Nested
    @DisplayName("출금(debit) 기능 테스트")
    class DebitTests {

        @Test
        @DisplayName("성공: 모든 조건이 충족되면 출금이 성공하고 원장 ID를 반환한다")
        void debit_shouldSucceed_whenAllConditionsAreMet() {
            // given
            BigDecimal expectedBalanceAfter = new BigDecimal("9000");
            AccountLedger savedLedger = new AccountLedger();
            savedLedger.setLedgerId(LEDGER_ID);

            when(accountDao.findByIdForUpdate(ACCOUNT_ID)).thenReturn(Optional.of(account));
            when(accountLedgerDao.save(any(AccountLedger.class))).thenReturn(savedLedger);

            // when
            long resultLedgerId = accountService.debit(request);

            // then
            assertThat(resultLedgerId).isEqualTo(LEDGER_ID);
            // accountDao.setBalance가 정확한 계산 결과로 호출되었는지 검증
            verify(accountDao).setBalance(ACCOUNT_ID, expectedBalanceAfter);
            // accountLedgerDao.save가 한번 호출되었는지 검증
            verify(accountLedgerDao).save(any(AccountLedger.class));
        }

        @Test
        @DisplayName("실패: 거래 금액이 0보다 작으면 InvalidAmountException을 던진다")
        void debit_shouldThrowInvalidAmountException_whenAmountIsInvalid() {
            // given
            request.setAmount(BigDecimal.ZERO);

            // when & then
            assertThatThrownBy(() -> accountService.debit(request))
                    .isInstanceOf(InvalidAmountException.class);

            // DAO는 전혀 호출되지 않아야 함
            verifyNoInteractions(accountDao, accountLedgerDao);
        }

        @Test
        @DisplayName("실패: 계좌가 존재하지 않으면 AccountNotFoundException을 던진다")
        void debit_shouldThrowAccountNotFoundException_whenAccountNotFound() {
            // given
            when(accountDao.findByIdForUpdate(ACCOUNT_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> accountService.debit(request))
                    .isInstanceOf(AccountNotFoundException.class);
        }

        @Test
        @DisplayName("실패: 잔액이 부족하면 InsufficientBalanceException을 던진다")
        void debit_shouldThrowInsufficientBalanceException_whenBalanceIsInsufficient() {
            // given
            account.setBalance(new BigDecimal("500")); // 출금 요청 금액(1000)보다 적은 잔액
            when(accountDao.findByIdForUpdate(ACCOUNT_ID)).thenReturn(Optional.of(account));

            // when & then
            assertThatThrownBy(() -> accountService.debit(request))
                    .isInstanceOf(InsufficientBalanceException.class);

            // 잔액 변경이나 원장 기록은 일어나지 않아야 함
            verify(accountDao, never()).setBalance(anyLong(), any());
            verify(accountLedgerDao, never()).save(any());
        }
    }

    @Nested
    @DisplayName("입금(credit) 기능 테스트")
    class CreditTests {

        @Test
        @DisplayName("성공: 모든 조건이 충족되면 입금이 성공하고 원장 ID를 반환한다")
        void credit_shouldSucceed_whenAllConditionsAreMet() {
            // given
            BigDecimal expectedBalanceAfter = new BigDecimal("11000");
            AccountLedger savedLedger = new AccountLedger();
            savedLedger.setLedgerId(LEDGER_ID);

            when(accountDao.findByIdForUpdate(ACCOUNT_ID)).thenReturn(Optional.of(account));
            when(accountLedgerDao.save(any(AccountLedger.class))).thenReturn(savedLedger);

            // when
            long resultLedgerId = accountService.credit(request);

            // then
            assertThat(resultLedgerId).isEqualTo(LEDGER_ID);
            verify(accountDao).setBalance(ACCOUNT_ID, expectedBalanceAfter);
            verify(accountLedgerDao).save(any(AccountLedger.class));
        }

        @Test
        @DisplayName("실패: 거래 금액이 0보다 작으면 InvalidAmountException을 던진다")
        void credit_shouldThrowInvalidAmountException_whenAmountIsInvalid() {
            // given
            request.setAmount(new BigDecimal("-100"));

            // when & then
            assertThatThrownBy(() -> accountService.credit(request))
                    .isInstanceOf(InvalidAmountException.class);
            verifyNoInteractions(accountDao, accountLedgerDao);
        }

        @Test
        @DisplayName("실패: 계좌가 존재하지 않으면 AccountNotFoundException을 던진다")
        void credit_shouldThrowAccountNotFoundException_whenAccountNotFound() {
            // given
            when(accountDao.findByIdForUpdate(ACCOUNT_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> accountService.credit(request))
                    .isInstanceOf(AccountNotFoundException.class);
        }
    }
}