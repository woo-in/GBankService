package bankapp.account.service.transfer;

import bankapp.account.dao.transfer.PendingTransferDao;
import bankapp.account.exceptions.*;
import bankapp.account.model.account.PrimaryAccount;
import bankapp.account.model.transfer.PendingTransfer;
import bankapp.account.request.account.AccountTransactionRequest;
import bankapp.account.request.transfer.TransferAmountRequest;
import bankapp.account.request.transfer.TransferAuthRequest;
import bankapp.account.request.transfer.TransferMessageRequest;
import bankapp.account.request.transfer.TransferRecipientRequest;
import bankapp.account.response.transfer.PendingTransferResponse;
import bankapp.account.service.account.AccountService;
import bankapp.account.service.check.AccountCheckService;
import bankapp.account.service.transfer.component.*;
import bankapp.core.util.BankNameConverter;
import bankapp.member.exceptions.IncorrectPasswordException;
import bankapp.member.model.Member;
import bankapp.member.service.check.MemberCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static bankapp.account.model.transfer.TransferStatus.*;

// TODO: 테스트 필요
@Slf4j
@Service
public class DefaultTransferService implements TransferService {

    private final PendingTransferDao pendingTransferDao;

    private final AccountService accountService;
    private final AccountCheckService accountCheckService;
    private final MemberCheckService memberCheckService;

    private final PasswordEncoder passwordEncoder;

    private final TransferRecipientValidator transferRecipientValidator;
    private final TransferRecipientInfoFinder transferRecipientInfoFinder;
    private final TransferAmountValidator transferAmountValidator;
    private final TransferMessageValidator transferMessageValidator;
    private final TransferAuthValidator transferAuthValidator;

    private final long expirationMinutes;

    @Autowired
    public DefaultTransferService(PendingTransferDao pendingTransferDao,
                                  AccountService accountService,
                                  AccountCheckService accountCheckService,
                                  MemberCheckService memberCheckService,
                                  PasswordEncoder passwordEncoder,
                                  TransferRecipientValidator transferRecipientValidator,
                                  TransferRecipientInfoFinder transferRecipientInfoFinder,
                                  TransferAmountValidator transferAmountValidator,
                                  TransferMessageValidator transferMessageValidator,
                                  TransferAuthValidator transferAuthValidator,
                                  @Value("${transfer.expiration-minutes}") long expirationMinutes) {
        this.pendingTransferDao = pendingTransferDao;
        this.accountService = accountService;
        this.accountCheckService = accountCheckService;
        this.memberCheckService = memberCheckService;
        this.passwordEncoder = passwordEncoder;
        this.transferRecipientValidator = transferRecipientValidator;
        this.transferRecipientInfoFinder = transferRecipientInfoFinder;
        this.transferAmountValidator = transferAmountValidator;
        this.transferMessageValidator = transferMessageValidator;
        this.transferAuthValidator = transferAuthValidator;
        this.expirationMinutes = expirationMinutes;
    }


    @Override
    @Transactional
    public String processRecipient(TransferRecipientRequest transferRecipientRequest, Member loginMember)
            throws ExternalTransferNotSupportedException, RecipientAccountNotFoundException, SameAccountTransferException, PrimaryAccountNotFoundException {

        TransferRecipientValidator.ValidationResult validationResult = transferRecipientValidator.validate(transferRecipientRequest, loginMember);

        // TODO : 왜 값이 바뀌는지 ?
        log.info(validationResult.getToAccountNumber());
        log.info(validationResult.getToBankCode());

        String recipientName = transferRecipientInfoFinder.findRecipientName(validationResult.getToAccountNumber());

        PendingTransfer pendingTransfer = initializePendingTransfer(validationResult ,recipientName);

        return pendingTransfer.getRequestId();
    }


    @Override
    @Transactional(readOnly = true)
    public PendingTransferResponse getPendingTransferResponse(String requestId) throws PendingTransferNotFoundException, PrimaryAccountNotFoundException {

        PendingTransfer pendingTransfer = pendingTransferDao.findById(requestId)
                .orElseThrow(() -> new PendingTransferNotFoundException("유효하지 않은 송금 요청입니다. ID: " + requestId));

        PrimaryAccount senderPrimaryAccount = accountCheckService.findPrimaryAccountByAccountId(pendingTransfer.getSenderAccountId());

        return PendingTransferResponse.from(pendingTransfer, senderPrimaryAccount);
    }

    @Override
    @Transactional
    public void processAmount(String requestId, TransferAmountRequest transferAmountRequest) throws InsufficientBalanceException, PendingTransferNotFoundException, InvalidAmountException, IllegalTransferStateException, IllegalStateException {

        PendingTransfer pendingTransfer = transferAmountValidator.validate(requestId,transferAmountRequest);

        applyAmountUpdatePendingTransfer(pendingTransfer, transferAmountRequest);
    }

    @Override
    @Transactional
    public void processMessage(String requestId, TransferMessageRequest transferMessageRequest) throws PendingTransferNotFoundException, IllegalTransferStateException {

       PendingTransfer pendingTransfer = transferMessageValidator.validate(requestId);

       applyMessageUpdatePendingTransfer(pendingTransfer ,  transferMessageRequest);
    }

    @Override
    @Transactional
    public void executeTransfer(String requestId, TransferAuthRequest transferAuthRequest) throws PendingTransferNotFoundException, IllegalTransferStateException, IncorrectPasswordException {

        PendingTransfer pendingTransfer = transferAuthValidator.validate(requestId);

        verifyMemberPassword(pendingTransfer, transferAuthRequest);
        long senderLedgerId = debitFromSender(pendingTransfer);
        long creditLedgerId = creditToReceiver(pendingTransfer);
        recordTransferCompletion(pendingTransfer, senderLedgerId, creditLedgerId);
    }




    private PendingTransfer initializePendingTransfer(TransferRecipientValidator.ValidationResult result , String recipientName) {
        PendingTransfer pendingTransfer = new PendingTransfer();
        pendingTransfer.setSenderAccountId(result.getFromAccount().getAccountId());
        pendingTransfer.setSenderMemberId(result.getFromAccount().getMemberId());
        pendingTransfer.setStatus(PENDING_AMOUNT);
        pendingTransfer.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        pendingTransfer.setReceiverAccountNumber(result.getToAccountNumber());
        pendingTransfer.setReceiverBankName(BankNameConverter.getBankNameByCode(result.getToBankCode()));
        pendingTransfer.setReceiverName(recipientName);

        return pendingTransferDao.init(pendingTransfer);
    }
    private void applyAmountUpdatePendingTransfer(PendingTransfer pendingTransfer, TransferAmountRequest transferAmountRequest) {
        pendingTransfer.setAmount(transferAmountRequest.getAmount());
        pendingTransfer.setStatus(PENDING_MESSAGE);
        pendingTransfer.setUpdatedAt(LocalDateTime.now());
        pendingTransferDao.update(pendingTransfer);
    }
    private void applyMessageUpdatePendingTransfer(PendingTransfer pendingTransfer, TransferMessageRequest transferMessageRequest) {
        pendingTransfer.setMessage(transferMessageRequest.getMessage());
        pendingTransfer.setStatus(PENDING_AUTH);
        pendingTransfer.setUpdatedAt(LocalDateTime.now());
        pendingTransferDao.update(pendingTransfer);
    }
    private void verifyMemberPassword(PendingTransfer pendingTransfer, TransferAuthRequest transferAuthRequest) throws IncorrectPasswordException{
        Member senderMember = memberCheckService.findMemberByAccount(accountCheckService.findPrimaryAccountByAccountId(pendingTransfer.getSenderAccountId()));
        if(!passwordEncoder.matches(transferAuthRequest.getPassword(), senderMember.getPassword())) {
            throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
        }

        pendingTransfer.setStatus(PENDING_TRANSFER);
        pendingTransfer.setUpdatedAt(LocalDateTime.now());
        pendingTransferDao.update(pendingTransfer);

    }
    private long debitFromSender(PendingTransfer pendingTransfer) {
        if(pendingTransfer.getStatus() != PENDING_TRANSFER){
            throw new IllegalTransferStateException("거래 단계가 아닙니다.");
        }

        return accountService.debit(new AccountTransactionRequest(
                pendingTransfer.getSenderAccountId(),
                pendingTransfer.getAmount(),
                "transfer"));
    }
    private long creditToReceiver(PendingTransfer pendingTransfer) {
        if(pendingTransfer.getStatus() != PENDING_TRANSFER){
            throw new IllegalTransferStateException("거래 단계가 아닙니다.");
        }

        long receiverAccountId = accountCheckService.findAccountByAccountNumber(pendingTransfer.getReceiverAccountNumber()).getAccountId();

        return accountService.credit(new AccountTransactionRequest(
                receiverAccountId,
                pendingTransfer.getAmount(),
                "transfer"
        ));
    }
    private void recordTransferCompletion(PendingTransfer pendingTransfer, long senderLedgerId, long receiverLedgerId) {

        pendingTransfer.setStatus(COMPLETED);
        pendingTransfer.setUpdatedAt(LocalDateTime.now());
        pendingTransfer.setSenderLedgerId(senderLedgerId);
        pendingTransfer.setReceiverLedgerId(receiverLedgerId);

        pendingTransferDao.update(pendingTransfer);

    }

}
