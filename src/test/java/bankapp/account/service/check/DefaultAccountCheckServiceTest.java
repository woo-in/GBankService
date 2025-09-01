package bankapp.account.service.check;

import bankapp.account.dao.account.AccountDao;
import bankapp.account.exceptions.AccountNotFoundException;
import bankapp.account.exceptions.PrimaryAccountNotFoundException;
import bankapp.account.model.account.Account;
import bankapp.account.model.account.PrimaryAccount;
import bankapp.account.model.account.SavingsAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static bankapp.core.common.BankCode.SHINHAN_BANK;
import static bankapp.core.common.BankCode.WOOIN_BANK;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AccountCheckService의 구현체인 DefaultAccountCheckService를 테스트합니다.
 * AccountDao는 Mockito를 사용하여 Mock(가짜) 객체로 대체합니다.
 */
@ExtendWith(MockitoExtension.class)
class DefaultAccountCheckServiceTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private DefaultAccountCheckService accountCheckService;

    // 1. isAccountNumberExist() 테스트
    // =================================================================

    @Test
    @DisplayName("계좌번호가 존재할 때 true를 반환해야 한다")
    void isAccountNumberExist_shouldReturnTrue_whenAccountNumberExists() {
        // given
        String existingAccountNumber = "123-456-789";
        when(accountDao.existsByAccountNumber(existingAccountNumber)).thenReturn(true);

        // when
        boolean result = accountCheckService.isAccountNumberExist(existingAccountNumber);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("계좌번호가 존재하지 않을 때 false를 반환해야 한다")
    void isAccountNumberExist_shouldReturnFalse_whenAccountNumberDoesNotExist() {
        // given
        String nonExistingAccountNumber = "000-000-000";
        when(accountDao.existsByAccountNumber(nonExistingAccountNumber)).thenReturn(false);

        // when
        boolean result = accountCheckService.isAccountNumberExist(nonExistingAccountNumber);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("계좌번호가 null이거나 비어있으면 DAO 호출 없이 false를 반환해야 한다")
    void isAccountNumberExist_shouldReturnFalse_forInvalidInput() {
        // when
        boolean resultForNull = accountCheckService.isAccountNumberExist(null);
        boolean resultForEmpty = accountCheckService.isAccountNumberExist("  ");

        // then
        assertThat(resultForNull).isFalse();
        assertThat(resultForEmpty).isFalse();
        verifyNoInteractions(accountDao);
    }


    // 2. findPrimaryAccountByMemberId() 테스트
    // =================================================================

    @Test
    @DisplayName("회원 ID로 주계좌를 성공적으로 찾아야 한다")
    void findPrimaryAccountByMemberId_shouldReturnAccount_whenFound() {
        // given
        Long memberId = 1L;
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setMemberId(memberId);
        when(accountDao.findPrimaryAccountByMemberId(memberId)).thenReturn(Optional.of(primaryAccount));

        // when
        PrimaryAccount foundAccount = accountCheckService.findPrimaryAccountByMemberId(memberId);

        // then
        assertThat(foundAccount).isEqualTo(primaryAccount);
    }

    @Test
    @DisplayName("회원 ID로 주계좌를 찾지 못하면 PrimaryAccountNotFoundException을 던져야 한다")
    void findPrimaryAccountByMemberId_shouldThrowException_whenNotFound() {
        // given
        Long memberId = 99L;
        when(accountDao.findPrimaryAccountByMemberId(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountCheckService.findPrimaryAccountByMemberId(memberId))
                .isInstanceOf(PrimaryAccountNotFoundException.class);
    }

    // 3. findPrimaryAccountByAccountId() 테스트 (수정된 로직 기준)
    // =================================================================
    @Test
    @DisplayName("계좌 ID로 주계좌를 성공적으로 찾아야 한다")
    void findPrimaryAccountByAccountId_shouldReturnPrimaryAccount_whenFound() {
        // given
        Long accountId = 1L;
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountId(accountId);
        when(accountDao.findPrimaryAccountByAccountId(accountId)).thenReturn(Optional.of(primaryAccount));

        // when
        PrimaryAccount foundAccount = accountCheckService.findPrimaryAccountByAccountId(accountId);

        // then
        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount).isInstanceOf(PrimaryAccount.class);
        assertThat(foundAccount.getAccountId()).isEqualTo(accountId);
    }

    @Test
    @DisplayName("계좌 ID에 해당하는 계좌가 없으면 AccountNotFoundException을 던져야 한다")
    void findPrimaryAccountByAccountId_shouldThrowAccountNotFoundException_whenAccountNotFound() {
        // given
        Long nonExistingAccountId = 99L;
        when(accountDao.findPrimaryAccountByAccountId(nonExistingAccountId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountCheckService.findPrimaryAccountByAccountId(nonExistingAccountId))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    @DisplayName("계좌를 찾았지만 주계좌가 아니면 PrimaryAccountNotFoundException을 던져야 한다")
    void findPrimaryAccountByAccountId_shouldThrowPrimaryAccountNotFoundException_whenAccountIsNotPrimary() {
        // given
        Long accountId = 2L;
        Account regularAccount = new SavingsAccount();
        regularAccount.setAccountId(accountId);
        when(accountDao.findPrimaryAccountByAccountId(accountId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountCheckService.findPrimaryAccountByAccountId(accountId))
                .isInstanceOf(PrimaryAccountNotFoundException.class);
    }


    // 4. findAccountByAccountNumber() 테스트
    // =================================================================

    @Test
    @DisplayName("계좌번호로 계좌를 성공적으로 찾아야 한다")
    void findAccountByAccountNumber_shouldReturnAccount_whenFound() {
        // given
        String accountNumber = "111-222-333";
        Account account = new SavingsAccount();
        account.setAccountNumber(accountNumber);
        when(accountDao.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        // when
        Account foundAccount = accountCheckService.findAccountByAccountNumber(accountNumber);

        // then
        assertThat(foundAccount).isEqualTo(account);
    }

    @Test
    @DisplayName("계좌번호로 계좌를 찾지 못하면 AccountNotFoundException을 던져야 한다")
    void findAccountByAccountNumber_shouldThrowException_whenNotFound() {
        // given
        String accountNumber = "111-222-333";
        when(accountDao.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountCheckService.findAccountByAccountNumber(accountNumber))
                .isInstanceOf(AccountNotFoundException.class);
    }


    // 5. findAccountByAccountId() 테스트
    // =================================================================

    @Test
    @DisplayName("계좌 ID로 계좌를 성공적으로 찾아야 한다")
    void findAccountByAccountId_shouldReturnAccount_whenFound() {
        // given
        Long accountId = 10L;
        Account account = new SavingsAccount();
        account.setAccountId(accountId);
        when(accountDao.findByAccountId(accountId)).thenReturn(Optional.of(account));

        // when
        Account foundAccount = accountCheckService.findAccountByAccountId(accountId);

        // then
        assertThat(foundAccount).isEqualTo(account);
    }

    @Test
    @DisplayName("계좌 ID로 계좌를 찾지 못하면 AccountNotFoundException을 던져야 한다")
    void findAccountByAccountId_shouldThrowException_whenNotFound() {
        // given
        Long accountId = 99L;
        when(accountDao.findByAccountId(accountId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountCheckService.findAccountByAccountId(accountId))
                .isInstanceOf(AccountNotFoundException.class);
    }


    // 6. isExternalBank() 테스트
    // =================================================================

    @Test
    @DisplayName("외부 은행 코드를 입력하면 true를 반환해야 한다")
    void isExternalBank_shouldReturnTrue_forExternalBankCode() {
        // given

        // when
        boolean result = accountCheckService.isExternalBank(SHINHAN_BANK);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("내부 은행 코드(WOOIN_BANK)를 입력하면 false를 반환해야 한다")
    void isExternalBank_shouldReturnFalse_forInternalBankCode() {
        // given


        // when
        boolean result = accountCheckService.isExternalBank(WOOIN_BANK);

        // then
        assertThat(result).isFalse();
    }
}