//package bankapp.account.service.transfer;
//
//import bankapp.account.dao.transfer.PendingTransferDao;
//import bankapp.account.exceptions.*;
//import bankapp.account.model.account.PrimaryAccount;
//import bankapp.account.model.transfer.PendingTransfer;
//import bankapp.account.model.transfer.TransferStatus;
//import bankapp.account.request.transfer.TransferAmountRequest;
//import bankapp.account.request.transfer.TransferRecipientRequest;
//import bankapp.account.response.transfer.PendingTransferResponse;
//import bankapp.account.service.check.AccountCheckService;
//import bankapp.core.util.AccountNumberFormatter;
//import bankapp.member.exceptions.MemberNotFoundException;
//import bankapp.member.model.Member;
//import bankapp.member.service.check.MemberCheckService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("DefaultTransferService 단위 테스트")
//class DefaultTransferServiceTest {
//
//    @Mock
//    private PendingTransferDao pendingTransferDao;
//    @Mock
//    private AccountCheckService accountCheckService;
//    @Mock
//    private MemberCheckService memberCheckService;
//
//    // expirationMinutes는 10분으로 고정하여 테스트
//    private final long expirationMinutes = 10L;
//
//    private DefaultTransferService transferService;
//
//    // 테스트 실행 전 @Value 값을 수동으로 주입
//    @BeforeEach
//    void setUp() {
//        transferService = new DefaultTransferService(pendingTransferDao, accountCheckService, memberCheckService, expirationMinutes);
//    }
//
//    // 테스트에 사용될 공통 객체들
//    private Member loginMember;
//    private PrimaryAccount senderAccount;
//    private PrimaryAccount recipientAccount;
//    private Member recipientMember;
//    private TransferRecipientRequest recipientRequest;
//
//    @BeforeEach
//    void commonSetup() {
//        loginMember = new Member();
//        loginMember.setMemberId(1L);
//        loginMember.setName("김송금");
//
//        senderAccount = new PrimaryAccount();
//        senderAccount.setAccountId(10L);
//        senderAccount.setMemberId(1L);
//        senderAccount.setAccountNumber("111-111-1111");
//        senderAccount.setBalance(BigDecimal.valueOf(100000));
//
//        recipientAccount = new PrimaryAccount();
//        recipientAccount.setAccountNumber("222-222-2222");
//
//        recipientMember = new Member();
//        recipientMember.setName("이수취");
//
//        recipientRequest = new TransferRecipientRequest();
//        recipientRequest.setToBankCode("088");
//        recipientRequest.setToAccountNumber("2222222222");
//
//    }
//
//    @Nested
//    @DisplayName("processRecipient: 수취인 정보 처리")
//    class ProcessRecipientTest {
//
//        @Test
//        @DisplayName("성공: 모든 검증을 통과하고 PendingTransfer 객체를 생성한 뒤 requestId를 반환한다")
//        void shouldSucceedAndReturnRequestId() throws Exception {
//            // given
//            String formattedAccountNumber = AccountNumberFormatter.format(recipientRequest.getToAccountNumber());
//            given(accountCheckService.isExternalBank(recipientRequest.getToBankCode())).willReturn(false);
//            given(accountCheckService.isAccountNumberExist(formattedAccountNumber)).willReturn(true);
//            given(accountCheckService.findPrimaryAccountByMemberId(loginMember.getMemberId())).willReturn(senderAccount);
//            given(accountCheckService.findAccountByAccountNumber(formattedAccountNumber)).willReturn(recipientAccount);
//            given(memberCheckService.findMemberByAccount(recipientAccount)).willReturn(recipientMember);
//
//            PendingTransfer pendingTransfer = new PendingTransfer();
//            String expectedRequestId = UUID.randomUUID().toString();
//            pendingTransfer.setRequestId(expectedRequestId);
//            given(pendingTransferDao.init(any(PendingTransfer.class))).willReturn(pendingTransfer);
//
//            // when
//            String requestId = transferService.processRecipient(recipientRequest, loginMember);
//
//            // then
//            assertThat(requestId).isEqualTo(expectedRequestId);
//
//            // pendingTransferDao.init()에 전달된 PendingTransfer 객체를 캡처하여 검증
//            ArgumentCaptor<PendingTransfer> captor = ArgumentCaptor.forClass(PendingTransfer.class);
//            verify(pendingTransferDao).init(captor.capture());
//            PendingTransfer captured = captor.getValue();
//
//            assertThat(captured.getSenderAccountId()).isEqualTo(senderAccount.getAccountId());
//            assertThat(captured.getSenderMemberId()).isEqualTo(loginMember.getMemberId());
//            assertThat(captured.getReceiverAccountNumber()).isEqualTo(formattedAccountNumber);
//            assertThat(captured.getReceiverName()).isEqualTo(recipientMember.getName());
//            assertThat(captured.getStatus()).isEqualTo(TransferStatus.PENDING_AMOUNT);
//            assertThat(captured.getExpiresAt()).isAfter(LocalDateTime.now());
//        }
//
//        @Test
//        @DisplayName("실패: 타행 이체는 ExternalTransferNotSupportedException 예외를 던진다")
//        void shouldThrow_ExternalTransferNotSupportedException_ForExternalBank() {
//            // given
//            given(accountCheckService.isExternalBank(recipientRequest.getToBankCode())).willReturn(true);
//
//            // when & then
//            assertThrows(ExternalTransferNotSupportedException.class,
//                    () -> transferService.processRecipient(recipientRequest, loginMember));
//            verify(pendingTransferDao, never()).init(any());
//        }
//
//        @Test
//        @DisplayName("실패: 수취인 계좌가 존재하지 않으면 RecipientAccountNotFoundException 예외를 던진다")
//        void shouldThrow_RecipientAccountNotFoundException_WhenAccountNotExist() {
//            // given
//            String formattedAccountNumber = AccountNumberFormatter.format(recipientRequest.getToAccountNumber());
//            given(accountCheckService.isExternalBank(recipientRequest.getToBankCode())).willReturn(false);
//            given(accountCheckService.isAccountNumberExist(formattedAccountNumber)).willReturn(false);
//
//            // when & then
//            assertThrows(RecipientAccountNotFoundException.class,
//                    () -> transferService.processRecipient(recipientRequest, loginMember));
//        }
//
//        @Test
//        @DisplayName("실패: 보내는 계좌와 받는 계좌가 동일하면 SameAccountTransferException 예외를 던진다")
//        void shouldThrow_SameAccountTransferException_ForSameAccount() throws PrimaryAccountNotFoundException {
//            // given
//            recipientRequest.setToAccountNumber("1111111111"); // 보내는 사람 계좌와 동일하게 설정
//            String formattedAccountNumber = AccountNumberFormatter.format(recipientRequest.getToAccountNumber());
//            given(accountCheckService.isExternalBank(recipientRequest.getToBankCode())).willReturn(false);
//            given(accountCheckService.isAccountNumberExist(formattedAccountNumber)).willReturn(true);
//            given(accountCheckService.findPrimaryAccountByMemberId(loginMember.getMemberId())).willReturn(senderAccount);
//
//            // when & then
//            // 문제 : 계좌가 동일해도 넘어감.
//            assertThrows(SameAccountTransferException.class,
//                    () -> transferService.processRecipient(recipientRequest, loginMember));
//        }
//
//        @Test
//        @DisplayName("실패: 수취인 계좌에 해당하는 멤버를 찾지 못하면 RecipientAccountNotFoundException 예외를 던진다")
//        void shouldThrow_RecipientAccountNotFoundException_WhenMemberNotFound() throws Exception {
//            // given
//            String formattedAccountNumber = AccountNumberFormatter.format(recipientRequest.getToAccountNumber());
//            given(accountCheckService.isExternalBank(recipientRequest.getToBankCode())).willReturn(false);
//            given(accountCheckService.isAccountNumberExist(formattedAccountNumber)).willReturn(true);
//            given(accountCheckService.findPrimaryAccountByMemberId(loginMember.getMemberId())).willReturn(senderAccount);
//            given(accountCheckService.findAccountByAccountNumber(formattedAccountNumber)).willReturn(recipientAccount);
//            given(memberCheckService.findMemberByAccount(recipientAccount)).willThrow(MemberNotFoundException.class); // 멤버를 못 찾는 상황
//
//            // when & then
//            assertThrows(RecipientAccountNotFoundException.class,
//                    () -> transferService.processRecipient(recipientRequest, loginMember));
//        }
//    }
//
//    @Nested
//    @DisplayName("getPendingTransferResponse: 송금 정보 조회")
//    class GetPendingTransferResponseTest {
//
//        @Test
//        @DisplayName("성공: 유효한 requestId로 송금 정보를 조회하고 DTO로 변환하여 반환한다")
//        void shouldReturnPendingTransferResponse() throws Exception {
//            // given
//            String requestId = UUID.randomUUID().toString();
//            PendingTransfer pendingTransfer = new PendingTransfer();
//            pendingTransfer.setSenderAccountId(senderAccount.getAccountId());
//            pendingTransfer.setReceiverName("이수취");
//            pendingTransfer.setReceiverBankName("신한은행");
//            pendingTransfer.setReceiverAccountNumber("222-222-2222");
//
//            given(pendingTransferDao.findById(requestId)).willReturn(Optional.of(pendingTransfer));
//            given(accountCheckService.findPrimaryAccountByAccountId(senderAccount.getAccountId())).willReturn(senderAccount);
//
//            // when
//            PendingTransferResponse response = transferService.getPendingTransferResponse(requestId);
//
//            // then
//            assertThat(response.getSenderAccountNumber()).isEqualTo(senderAccount.getAccountNumber());
//            assertThat(response.getReceiverName()).isEqualTo(pendingTransfer.getReceiverName());
//            assertThat(response.getReceiverBankName()).isEqualTo(pendingTransfer.getReceiverBankName());
//            assertThat(response.getReceiverAccountNumber()).isEqualTo(pendingTransfer.getReceiverAccountNumber());
//        }
//
//        @Test
//        @DisplayName("실패: 존재하지 않는 requestId로 조회 시 PendingTransferNotFoundException 예외를 던진다")
//        void shouldThrow_PendingTransferNotFoundException_WhenRequestIdNotExist() {
//            // given
//            String invalidRequestId = "invalid-id";
//            given(pendingTransferDao.findById(invalidRequestId)).willReturn(Optional.empty());
//
//            // when & then
//            assertThrows(PendingTransferNotFoundException.class,
//                    () -> transferService.getPendingTransferResponse(invalidRequestId));
//        }
//    }
//
//    @Nested
//    @DisplayName("processAmount: 송금 금액 처리")
//    class ProcessAmountTest {
//
//        private String requestId;
//        private TransferAmountRequest amountRequest;
//        private PendingTransfer pendingTransfer;
//
//        @BeforeEach
//        void setup() {
//            requestId = UUID.randomUUID().toString();
//            amountRequest = new TransferAmountRequest();
//            amountRequest.setAmount(BigDecimal.valueOf(50000));
//
//            pendingTransfer = new PendingTransfer();
//            pendingTransfer.setRequestId(requestId);
//            pendingTransfer.setSenderAccountId(senderAccount.getAccountId());
//            pendingTransfer.setStatus(TransferStatus.PENDING_AMOUNT); // 금액 입력 단계
//        }
//
//        @Test
//        @DisplayName("성공: 잔액이 충분하고 모든 조건이 맞으면 PendingTransfer의 금액과 상태를 업데이트한다")
//        void shouldUpdateAmountAndStatusOnSuccess() throws Exception {
//            // given
//            senderAccount.setBalance(BigDecimal.valueOf(100000)); // 충분한 잔액
//            given(pendingTransferDao.findById(requestId)).willReturn(Optional.of(pendingTransfer));
//            given(accountCheckService.findPrimaryAccountByAccountId(senderAccount.getAccountId())).willReturn(senderAccount);
//
//            // when
//            transferService.processAmount(requestId, amountRequest);
//
//            // then
//            ArgumentCaptor<PendingTransfer> captor = ArgumentCaptor.forClass(PendingTransfer.class);
//            verify(pendingTransferDao).update(captor.capture());
//            PendingTransfer updated = captor.getValue();
//
//            assertThat(updated.getAmount()).isEqualTo(amountRequest.getAmount());
//            assertThat(updated.getStatus()).isEqualTo(TransferStatus.PENDING_AUTH);
//            assertThat(updated.getUpdatedAt()).isNotNull();
//        }
//
//        @Test
//        @DisplayName("실패: 송금액이 0 이하면 InvalidAmountException 예외를 던진다")
//        void shouldThrow_InvalidAmountException_ForZeroOrLessAmount() {
//            // given
//            amountRequest.setAmount(BigDecimal.ZERO);
//
//            // when & then
//            assertThrows(InvalidAmountException.class,
//                    () -> transferService.processAmount(requestId, amountRequest));
//            verify(pendingTransferDao, never()).update(any());
//        }
//
//        @Test
//        @DisplayName("실패: 현재 송금 단계가 PENDING_AMOUNT가 아니면 IllegalTransferStateException 예외를 던진다")
//        void shouldThrow_IllegalTransferStateException_WhenStatusIsNotPendingAmount() {
//            // given
//            pendingTransfer.setStatus(TransferStatus.PENDING_AUTH); // 이미 금액 입력 단계가 아님
//            given(pendingTransferDao.findById(requestId)).willReturn(Optional.of(pendingTransfer));
//
//            // when & then
//            assertThrows(IllegalTransferStateException.class,
//                    () -> transferService.processAmount(requestId, amountRequest));
//        }
//
//        @Test
//        @DisplayName("실패: 출금 계좌의 잔액이 부족하면 InsufficientBalanceException 예외를 던진다")
//        void shouldThrow_InsufficientBalanceException_WhenBalanceIsLow() throws PrimaryAccountNotFoundException {
//            // given
//            senderAccount.setBalance(BigDecimal.valueOf(40000)); // 잔액 부족
//            given(pendingTransferDao.findById(requestId)).willReturn(Optional.of(pendingTransfer));
//            given(accountCheckService.findPrimaryAccountByAccountId(senderAccount.getAccountId())).willReturn(senderAccount);
//
//            // when & then
//            assertThrows(InsufficientBalanceException.class,
//                    () -> transferService.processAmount(requestId, amountRequest));
//        }
//
//        @Test
//        @DisplayName("실패: 송금자의 주계좌를 찾을 수 없는 데이터 정합성 오류 시 IllegalStateException 예외를 던진다")
//        void shouldThrow_IllegalStateException_WhenPrimaryAccountIsMissing() throws PrimaryAccountNotFoundException {
//            // given
//            given(pendingTransferDao.findById(requestId)).willReturn(Optional.of(pendingTransfer));
//            // 주계좌를 찾을 수 없는 심각한 내부 오류 상황을 가정
//            given(accountCheckService.findPrimaryAccountByAccountId(senderAccount.getAccountId()))
//                    .willThrow(PrimaryAccountNotFoundException.class);
//
//            // when & then
//            assertThrows(IllegalStateException.class,
//                    () -> transferService.processAmount(requestId, amountRequest));
//        }
//    }
//}