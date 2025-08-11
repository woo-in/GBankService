package bankapp.service.open;

import bankapp.account.request.open.AccountCreationRequest;
import bankapp.account.request.open.HighCreditAccountCreationRequest;
import bankapp.dao.BankAccountDao;
import bankapp.account.exceptions.DuplicateAccountException;
import bankapp.account.service.open.OpenHighCreditAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenHighCreditAccountServiceTest {

    @InjectMocks
    private OpenHighCreditAccountService openHighCreditAccountService; // 테스트할 실제 서비스

    @Mock
    private BankAccountDao bankAccountDao; // 목 객체(Dao)

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 목 객체 초기화
    }

    @Test
    void openAccount_WhenValidRequest_ShouldPass() {
        // Given: 유효한 요청 인스턴스를 생성
        HighCreditAccountCreationRequest request = new HighCreditAccountCreationRequest(
                12345, "John Doe", 5000.00, 5, 1, "highCreditAccount"
        );

        // 목 동작 설정
        when(bankAccountDao.isAccountExist(request.getAccountNumber())).thenReturn(false);

        // When: 메서드 호출
        assertDoesNotThrow(() -> openHighCreditAccountService.openAccount(request));

        // Then: Dao 메서드 호출 검증
        verify(bankAccountDao).isAccountExist(request.getAccountNumber());
        verify(bankAccountDao).insertHighCreditAccount(request);
    }

    @Test
    void openAccount_WhenNegativeBalance_ShouldThrowIllegalArgumentException() {
        // Given: 잘못된 요청 (음수 잔액 포함)
        HighCreditAccountCreationRequest invalidRequest = new HighCreditAccountCreationRequest(
                12345, "John Doe", -1000.00, 5, 1, "highCreditAccount"
        );

        // When & Then: 예외 발생 확인
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> openHighCreditAccountService.openAccount(invalidRequest)
        );
        assertEquals("negative number error", exception.getMessage());
        verify(bankAccountDao, never()).insertHighCreditAccount(any());
    }

    @Test
    void openAccount_WhenDuplicateAccount_ShouldThrowDuplicateAccountException() {
        // Given: 중복 계좌 요청
        HighCreditAccountCreationRequest duplicateRequest = new HighCreditAccountCreationRequest(
                12345, "John Doe", 5000.00, 5, 1, "highCreditAccount"
        );

        // 목 설정: 계좌가 이미 존재한다고 가정
        when(bankAccountDao.isAccountExist(duplicateRequest.getAccountNumber())).thenReturn(true);

        // When & Then: 중복 계좌 예외 확인
        DuplicateAccountException exception = assertThrows(
                DuplicateAccountException.class,
                () -> openHighCreditAccountService.openAccount(duplicateRequest)
        );
        assertEquals("Duplicate account number", exception.getMessage());
        verify(bankAccountDao).isAccountExist(duplicateRequest.getAccountNumber());
        verify(bankAccountDao, never()).insertHighCreditAccount(any());
    }

    @Test
    void openAccount_WhenInvalidRequestType_ShouldThrowIllegalArgumentException() {
        // Given: 잘못된 요청 타입
        AccountCreationRequest invalidRequest = mock(AccountCreationRequest.class); // HighCreditAccountCreationRequest가 아님

        // When & Then: 예외 확인
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> openHighCreditAccountService.openAccount(invalidRequest)
        );
        assertEquals("Invalid account type", exception.getMessage());
        verify(bankAccountDao, never()).isAccountExist(anyInt());
        verify(bankAccountDao, never()).insertHighCreditAccount(any());
    }
}