package bankapp.account.service.transfer;

import bankapp.account.dao.transfer.PendingTransferDao;
import bankapp.account.exceptions.IllegalTransferStateException;
import bankapp.account.exceptions.PendingTransferNotFoundException;
import bankapp.account.exceptions.SameAccountTransferException;
import bankapp.account.model.account.PrimaryAccount;
import bankapp.account.model.transfer.PendingTransfer;
import bankapp.account.model.transfer.TransferStatus;
import bankapp.account.request.account.AccountTransactionRequest;
import bankapp.account.request.transfer.TransferAuthRequest;
import bankapp.account.request.transfer.TransferRecipientRequest;
import bankapp.account.response.transfer.PendingTransferResponse;
import bankapp.account.service.account.AccountService;
import bankapp.account.service.check.AccountCheckService;
import bankapp.account.service.transfer.component.*;
import bankapp.core.util.BankNameConverter;
import bankapp.member.exceptions.IncorrectPasswordException;
import bankapp.member.model.Member;
import bankapp.member.service.check.MemberCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static bankapp.account.model.transfer.TransferStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultTransferServiceTest {

    // 테스트 대상
    private DefaultTransferService transferService;

    // Mock 객체들
    @Mock private PendingTransferDao pendingTransferDao;
    @Mock private AccountService accountService;
    @Mock private AccountCheckService accountCheckService;
    @Mock private MemberCheckService memberCheckService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TransferRecipientValidator transferRecipientValidator;
    @Mock private TransferRecipientInfoFinder transferRecipientInfoFinder;
    @Mock private TransferAmountValidator transferAmountValidator;
    @Mock private TransferMessageValidator transferMessageValidator;
    @Mock private TransferAuthValidator transferAuthValidator;

    // 테스트에 사용될 Mock 객체들
    @Mock private Member loginMember;
    @Mock private PrimaryAccount fromAccount;

    @BeforeEach
    void setUp() {
        // @InjectMocks 대신 수동 주입 방식을 사용하므로 그대로 둡니다.
        transferService = new DefaultTransferService(
                pendingTransferDao, accountService, accountCheckService, memberCheckService,
                passwordEncoder, transferRecipientValidator, transferRecipientInfoFinder,
                transferAmountValidator, transferMessageValidator, transferAuthValidator,
                10L // expirationMinutes
        );

    }

    @Nested
    @DisplayName("수취인 정보 처리 (processRecipient)")
    class ProcessRecipientTests {
        @Test
        @DisplayName("성공: 모든 검증 통과 시 requestId 반환 및 상태 변경")
        void success_WhenAllConditionsMet_ReturnsRequestIdAndChangesStatus()  {
            // --- Arrange (준비) ---
            String toBankCode = "088";
            String toAccountNumber = "110-456-789012";
            long senderAccountId = 1L;
            String recipientName = "김받는";
            String expectedRequestId = "test-request-id-12345";

            TransferRecipientRequest request = new TransferRecipientRequest(toBankCode, toAccountNumber);

            // fromAccount Mock 객체가 getAccountId() 호출 시 senderAccountId를 반환하도록 설정
            when(fromAccount.getAccountId()).thenReturn(senderAccountId);

            // ValidationResult는 실제 객체로 생성해도 무방합니다. (데이터 전달용 객체)
            TransferRecipientValidator.ValidationResult validationResult = new TransferRecipientValidator.ValidationResult(fromAccount, toBankCode, toAccountNumber);

            // Mock 객체들의 행동 정의
            when(transferRecipientValidator.validate(request, loginMember)).thenReturn(validationResult);
            when(transferRecipientInfoFinder.findRecipientName(toAccountNumber)).thenReturn(recipientName);

            PendingTransfer initializedTransfer = new PendingTransfer();
            initializedTransfer.setRequestId(expectedRequestId);
            when(pendingTransferDao.init(any(PendingTransfer.class))).thenReturn(initializedTransfer);

            // --- Act (실행) ---
            String actualRequestId = transferService.processRecipient(request, loginMember);

            // --- Assert (검증) ---
            assertThat(actualRequestId).isEqualTo(expectedRequestId);

            ArgumentCaptor<PendingTransfer> captor = ArgumentCaptor.forClass(PendingTransfer.class);
            verify(pendingTransferDao, times(1)).init(captor.capture());
            PendingTransfer capturedTransfer = captor.getValue();

            assertThat(capturedTransfer.getStatus()).isEqualTo(TransferStatus.PENDING_AMOUNT);
            assertThat(capturedTransfer.getSenderAccountId()).isEqualTo(senderAccountId);
            assertThat(capturedTransfer.getReceiverAccountNumber()).isEqualTo(toAccountNumber);
            assertThat(capturedTransfer.getReceiverName()).isEqualTo(recipientName);
            assertThat(capturedTransfer.getReceiverBankName()).isEqualTo(BankNameConverter.getBankNameByCode(toBankCode));

            verify(transferRecipientValidator, times(1)).validate(request, loginMember);
            verify(transferRecipientInfoFinder, times(1)).findRecipientName(toAccountNumber);
        }

        @Test
        @DisplayName("실패: 송금 계좌와 수취 계좌가 동일하면 SameAccountTransferException 발생")
        void throwsException_WhenSenderAndRecipientAccountsAreSame()   {
            // --- Arrange (준비) ---
            // 1. 테스트에 사용할 임의의 입력 데이터
            String sameAccountNumber = "110-110-110110";
            TransferRecipientRequest request = new TransferRecipientRequest("088", sameAccountNumber);

            // 2. Validator가 특정 예외를 던지도록 설정
            // validate 메소드가 호출되면 SameAccountTransferException을 발생시키도록 설정합니다.
            String expectedErrorMessage = "자기 자신에게 송금할 수 없습니다.";
            doThrow(new SameAccountTransferException(expectedErrorMessage))
                    .when(transferRecipientValidator).validate(any(TransferRecipientRequest.class), any(Member.class));


            // --- Act & Assert (실행 및 검증) ---
            // processRecipient 메소드를 실행했을 때 특정 예외가 발생하는지 검증합니다.
            assertThatThrownBy(() -> transferService.processRecipient(request, loginMember))
                    .isInstanceOf(SameAccountTransferException.class) // 예외 타입 검증
                    .hasMessage(expectedErrorMessage); // 예외 메시지 검증


            // --- Verify (추가 검증) ---
            // 예외가 발생했으므로, 수취인 이름을 찾거나 DB에 저장하는 로직은 호출되지 않았어야 합니다.
            verify(transferRecipientInfoFinder, never()).findRecipientName(anyString());
            verify(pendingTransferDao, never()).init(any(PendingTransfer.class));
        }
    }

    @Nested
    @DisplayName("진행중인 송금 정보 조회 (getPendingTransferResponse)")
    class GetPendingTransferResponseTests {

        @Test
        @DisplayName("성공: 유효한 requestId로 조회 시 올바른 송금 정보 응답 반환")
        void success_WhenPendingTransferExists_ReturnsCorrectResponse()   {
            // --- Arrange (준비) ---
            // 1. 테스트에 사용할 데이터와 Mock 객체 준비
            String requestId = "test-request-id";
            long senderAccountId = 1L;

            PendingTransfer mockPendingTransfer = new PendingTransfer();
            mockPendingTransfer.setRequestId(requestId);
            mockPendingTransfer.setSenderAccountId(senderAccountId);
            mockPendingTransfer.setReceiverAccountNumber("110-456-789012");
            mockPendingTransfer.setReceiverName("김받는");
            mockPendingTransfer.setAmount(new BigDecimal("50000"));
            mockPendingTransfer.setMessage("생일 축하해");
            mockPendingTransfer.setStatus(PENDING_AUTH);

            PrimaryAccount mockSenderAccount = new PrimaryAccount();
            mockSenderAccount.setAccountNumber("110-123-456789");

            // 2. Mock 객체들의 행동 정의
            // DAO가 Optional<PendingTransfer>를 반환하도록 설정
            when(pendingTransferDao.findById(requestId)).thenReturn(java.util.Optional.of(mockPendingTransfer));
            // AccountCheckService가 PrimaryAccount를 반환하도록 설정
            when(accountCheckService.findPrimaryAccountByAccountId(senderAccountId)).thenReturn(mockSenderAccount);

            // --- Act (실행) ---
            // 테스트 대상 메소드 호출
            PendingTransferResponse response = transferService.getPendingTransferResponse(requestId);

            // --- Assert (검증) ---
            // 1. 반환된 DTO가 null이 아닌지 확인
            assertThat(response).isNotNull();

            // 2. DTO의 각 필드가 Mock 객체의 데이터와 일치하는지 검증

            assertThat(response.getSenderAccountNumber()).isEqualTo(mockSenderAccount.getAccountNumber());
            assertThat(response.getReceiverAccountNumber()).isEqualTo(mockPendingTransfer.getReceiverAccountNumber());
            assertThat(response.getReceiverName()).isEqualTo(mockPendingTransfer.getReceiverName());
            assertThat(response.getAmount()).isEqualTo(mockPendingTransfer.getAmount());
            assertThat(response.getMessage()).isEqualTo(mockPendingTransfer.getMessage());

            // 3. 의존하는 Mock 객체들의 메소드가 정확히 1번씩 호출되었는지 검증
            verify(pendingTransferDao, times(1)).findById(requestId);
            verify(accountCheckService, times(1)).findPrimaryAccountByAccountId(senderAccountId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 requestId로 조회 시 PendingTransferNotFoundException 발생")
        void throwsException_WhenRequestIdNotFound() {
            // --- Arrange (준비) ---
            String invalidRequestId = "invalid-id";
            // DAO가 빈 Optional을 반환하도록 설정
            when(pendingTransferDao.findById(invalidRequestId)).thenReturn(java.util.Optional.empty());

            // --- Act & Assert (실행 및 검증) ---
            assertThatThrownBy(() -> transferService.getPendingTransferResponse(invalidRequestId))
                    .isInstanceOf(PendingTransferNotFoundException.class);

            // --- Verify (추가 검증) ---
            // PendingTransfer를 찾지 못했으므로, accountCheckService는 호출되지 않았어야 함
            verify(accountCheckService, never()).findPrimaryAccountByAccountId(anyLong());
        }
    }

    
    @Nested
    @DisplayName("송금 실행 (executeTransfer)")
    class ExecuteTransferTests {

        @Test
        @DisplayName("실패: 송금 상태가 PENDING_AUTH가 아니면 IllegalTransferStateException 발생")
        void throwsException_WhenStatusIsNotPendingAuth() {
            // --- Arrange (준비) ---
            String requestId = "test-request-id";
            TransferAuthRequest authRequest = new TransferAuthRequest("any-password");

            // 1. Validator가 IllegalTransferStateException을 던지도록 설정
            // 이 테스트의 핵심: validate 단계에서 예외가 발생하는 상황을 시뮬레이션합니다.
            String expectedErrorMessage = "송금 인증 단계가 아닙니다.";
            when(transferAuthValidator.validate(requestId))
                    .thenThrow(new IllegalTransferStateException(expectedErrorMessage));

            // --- Act & Assert (실행 및 검증) ---
            // executeTransfer 메소드 실행 시 예상된 예외가 발생하는지 검증합니다.
            assertThatThrownBy(() -> transferService.executeTransfer(requestId, authRequest))
                    .isInstanceOf(IllegalTransferStateException.class)
                    .hasMessage(expectedErrorMessage);

            // --- Verify (추가 검증) ---
            // 2. 예외가 발생했으므로, 이후의 어떤 로직도 실행되지 않았음을 검증합니다.
            // private 메소드는 직접 검증할 수 없으므로, private 메소드 내부에서 호출하는
            // public 메소드들이 호출되지 않았음을 확인하여 간접적으로 검증합니다.

            // verifyMemberPassword() 관련 로직 미호출 검증
            verify(memberCheckService, never()).findMemberByAccount(any());
            verify(passwordEncoder, never()).matches(anyString(), anyString());

            // debitFromSender() 및 creditToReceiver() 관련 로-직 미호출 검증
            verify(accountService, never()).debit(any());
            verify(accountService, never()).credit(any());

            // recordTransferCompletion() 관련 로직 미호출 검증
            verify(pendingTransferDao, never()).update(any(PendingTransfer.class));
        }

        @Test
        @DisplayName("실패: 비밀번호가 일치하지 않으면 IncorrectPasswordException 발생")
        void throwsException_WhenPasswordIsInvalid() {
            // --- Arrange (준비) ---
            String requestId = "test-request-id-auth-step";
            String wrongPassword = "wrong-password";
            String encodedPasswordFromDB = "encoded-password-from-db";

            TransferAuthRequest authRequest = new TransferAuthRequest(wrongPassword);

            // 1. 테스트에 사용할 Mock 객체들 준비
            PendingTransfer mockPendingTransfer = new PendingTransfer();
            mockPendingTransfer.setSenderAccountId(1L);

            PrimaryAccount mockSenderAccount = new PrimaryAccount();
            Member mockSenderMember = new Member();
            mockSenderMember.setPassword(encodedPasswordFromDB);

            // 2. Mock 객체들의 행동 정의
            // Validator는 통과했다고 가정
            when(transferAuthValidator.validate(requestId)).thenReturn(mockPendingTransfer);

            // 비밀번호 검증에 필요한 Member 객체를 찾을 수 있도록 설정
            when(accountCheckService.findPrimaryAccountByAccountId(anyLong())).thenReturn(mockSenderAccount);
            when(memberCheckService.findMemberByAccount(mockSenderAccount)).thenReturn(mockSenderMember);

            // ⭐️ 이 테스트의 핵심: passwordEncoder가 비밀번호 불일치(false)를 반환하도록 설정
            when(passwordEncoder.matches(wrongPassword, encodedPasswordFromDB)).thenReturn(false);


            // --- Act & Assert (실행 및 검증) ---
            // executeTransfer 실행 시 IncorrectPasswordException이 발생하는지 검증
            assertThatThrownBy(() -> transferService.executeTransfer(requestId, authRequest))
                    .isInstanceOf(IncorrectPasswordException.class)
                    .hasMessage("비밀번호가 일치하지 않습니다.");


            // --- Verify (추가 검증) ---
            // 비밀번호 검증에 실패했으므로, 실제 이체 로직은 호출되지 않았음을 검증
            verify(accountService, never()).debit(any());
            verify(accountService, never()).credit(any());
            verify(pendingTransferDao, never()).update(any(PendingTransfer.class));
        }

        @Test
        @DisplayName("성공: 모든 검증 통과 시 송금을 완료하고 상태를 COMPLETED로 변경")
        void success_WhenAllStepsAreValid_CompletesTransfer() {
            // --- Arrange (준비) ---
            String requestId = "test-request-id-success";
            String correctPassword = "correct-password";
            long senderAccountId = 1L;
            long receiverAccountId = 2L;
            long senderLedgerId = 101L;
            long receiverLedgerId = 102L;

            TransferAuthRequest authRequest = new TransferAuthRequest(correctPassword);

            // 1. Mock 객체 데이터 준비
            PendingTransfer mockPendingTransfer = new PendingTransfer();
            mockPendingTransfer.setSenderAccountId(senderAccountId);
            mockPendingTransfer.setReceiverAccountNumber("110-987-654321");
            mockPendingTransfer.setAmount(new BigDecimal("10000"));
            // debit/credit 내부에서 상태를 한번 더 체크하므로, 상태를 PENDING_TRANSFER로 미리 설정
            mockPendingTransfer.setStatus(PENDING_TRANSFER);

            PrimaryAccount mockSenderAccount = new PrimaryAccount();
            PrimaryAccount mockReceiverAccount = new PrimaryAccount();
            mockReceiverAccount.setAccountId(receiverAccountId);
            Member mockSenderMember = new Member();
            mockSenderMember.setPassword("encoded-password");

            // 2. Mock 객체들의 행동을 순서대로 정의
            // (1) 인증 검증 통과
            when(transferAuthValidator.validate(requestId)).thenReturn(mockPendingTransfer);
            // (2) 비밀번호 검증 통과
            when(accountCheckService.findPrimaryAccountByAccountId(senderAccountId)).thenReturn(mockSenderAccount);
            when(memberCheckService.findMemberByAccount(mockSenderAccount)).thenReturn(mockSenderMember);
            when(passwordEncoder.matches(correctPassword, "encoded-password")).thenReturn(true);
            // (3) 출금 성공
            when(accountService.debit(any(AccountTransactionRequest.class))).thenReturn(senderLedgerId);
            // (4) 입금 성공
            when(accountCheckService.findAccountByAccountNumber(mockPendingTransfer.getReceiverAccountNumber())).thenReturn(mockReceiverAccount);
            when(accountService.credit(any(AccountTransactionRequest.class))).thenReturn(receiverLedgerId);


            // --- Act (실행) ---
            // 예외가 발생하지 않아야 하므로 assertDoesNotThrow 사용
            assertDoesNotThrow(() -> transferService.executeTransfer(requestId, authRequest));


            // --- Verify (검증) ---
            // 1. 최종적으로 DB에 업데이트되는 PendingTransfer 객체를 캡처
            ArgumentCaptor<PendingTransfer> captor = ArgumentCaptor.forClass(PendingTransfer.class);
            // verifyMemberPassword와 recordTransferCompletion에서 총 2번 호출됨
            verify(pendingTransferDao, times(2)).update(captor.capture());

            // 2. 캡처된 마지막 객체(recordTransferCompletion에서 넘어온)의 상태를 검증
            PendingTransfer finalTransferState = captor.getValue();
            assertThat(finalTransferState.getStatus()).isEqualTo(COMPLETED);
            assertThat(finalTransferState.getSenderLedgerId()).isEqualTo(senderLedgerId);
            assertThat(finalTransferState.getReceiverLedgerId()).isEqualTo(receiverLedgerId);

            // 3. 주요 서비스들이 정확히 한 번씩 호출되었는지 검증
            verify(passwordEncoder, times(1)).matches(anyString(), anyString());
            verify(accountService, times(1)).debit(any(AccountTransactionRequest.class));
            verify(accountService, times(1)).credit(any(AccountTransactionRequest.class));
        }


    }












}