package bankapp.service;

import bankapp.account.request.creation.AccountCreationRequest;
import bankapp.account.request.creation.HighCreditAccountCreationRequest;
import bankapp.account.request.creation.NormalAccountCreationRequest;
import bankapp.exceptions.DuplicateAccountException;
import bankapp.service.open.OpenAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BankAccountManagerTest {

    @InjectMocks
    private BankAccountManager bankAccountManager; // 테스트 대상

    @Mock
    private OpenAccountService highCreditAccountService; // Mock OpenHighCreditAccountService

    @Mock
    private OpenAccountService normalAccountService; // Mock OpenNormalAccountService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 각 서비스의 "getAccountType()" 반환값 설정
        when(highCreditAccountService.getAccountType()).thenReturn("highCreditAccount");
        when(normalAccountService.getAccountType()).thenReturn("NormalAccount");

        // BankAccountManager가 내부적으로 서비스 맵을 생성하도록 리스트를 전달
        bankAccountManager = new BankAccountManager(
                List.of(highCreditAccountService, normalAccountService),
                null, null, null, null
        );
    }

    @Test
    void openAccount_WithHighCreditRequest_ShouldCallHighCreditService() throws DuplicateAccountException {
        // Given: HighCreditAccountCreationRequest 생성
        HighCreditAccountCreationRequest request = new HighCreditAccountCreationRequest(
                12345, "John Doe", 5000.00, 5, 1, "highCreditAccount"
        );

        // When: BankAccountManager의 openAccount 호출
        bankAccountManager.openAccount(request);

        // Then: HighCreditAccountService의 openAccount 호출 확인
        verify(highCreditAccountService, times(1)).openAccount(request);

        // NormalAccountService는 호출하지 않아야 함
        verify(normalAccountService, never()).openAccount(any(AccountCreationRequest.class));
    }

    @Test
    void openAccount_WithNormalAccountRequest_ShouldCallNormalAccountService() throws DuplicateAccountException {
        // Given: NormalAccountCreationRequest 생성
        NormalAccountCreationRequest request = new NormalAccountCreationRequest(
                54321, "Jane Doe", 3000.00, 3, "NormalAccount"
        );

        // When: BankAccountManager의 openAccount 호출
        bankAccountManager.openAccount(request);

        // Then: NormalAccountService의 openAccount 호출 확인
        verify(normalAccountService, times(1)).openAccount(request);

        // HighCreditAccountService는 호출하지 않아야 함
        verify(highCreditAccountService, never()).openAccount(any(AccountCreationRequest.class));
    }

    @Test
    void openAccount_WithInvalidRequest_ShouldThrowIllegalArgumentException() {
        // Given: 잘못된 계좌 타입의 요청 생성
        AccountCreationRequest invalidRequest = mock(AccountCreationRequest.class);
        when(invalidRequest.getAccountType()).thenReturn("InvalidAccount");

        // When & Then: 예외 확인
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankAccountManager.openAccount(invalidRequest)
        );
        assertEquals("Invalid account type", exception.getMessage());

        // 어떤 서비스도 호출되면 안 됨
        verify(highCreditAccountService, never()).openAccount(any());
        verify(normalAccountService, never()).openAccount(any());
    }
}