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
import bankapp.core.util.AccountNumberFormatter;
import bankapp.core.util.BankNameConverter;
import bankapp.member.exceptions.IncorrectPasswordException;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.model.Member;
import bankapp.member.service.check.MemberCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static bankapp.account.model.transfer.TransferStatus.*;

// TODO: 테스트 필요
@Slf4j
@Service
public class DefaultTransferService implements TransferService{

    private final PendingTransferDao pendingTransferDao;
    private final AccountService accountService;
    private final AccountCheckService accountCheckService;
    private final MemberCheckService  memberCheckService;
    private final PasswordEncoder passwordEncoder;
    private final long expirationMinutes;

    @Autowired
    public DefaultTransferService(PendingTransferDao pendingTransferDao,
                                  AccountService accountService,
                                  AccountCheckService accountCheckService,
                                  MemberCheckService memberCheckService,
                                  PasswordEncoder passwordEncoder,
                                  @Value("${transfer.expiration-minutes}") long expirationMinutes) {
        this.pendingTransferDao = pendingTransferDao;
        this.accountService = accountService;
        this.accountCheckService = accountCheckService;
        this.memberCheckService = memberCheckService;
        this.passwordEncoder = passwordEncoder;
        this.expirationMinutes = expirationMinutes;
    }


    @Override
    @Transactional
    public String processRecipient(TransferRecipientRequest transferRecipientRequest, Member loginMember)
            throws ExternalTransferNotSupportedException, RecipientAccountNotFoundException, SameAccountTransferException, PrimaryAccountNotFoundException{

        String toBankCode = transferRecipientRequest.getToBankCode();
        String toAccountNumber = AccountNumberFormatter.format(transferRecipientRequest.getToAccountNumber());
        String toName ;

        if (accountCheckService.isExternalBank(toBankCode)) {
            throw new ExternalTransferNotSupportedException("타행 이체는 현재 서비스 준비 중입니다.");
        }

        if (!accountCheckService.isAccountNumberExist(toAccountNumber)) {
            throw new RecipientAccountNotFoundException("수취인 계좌를 찾을 수 없습니다.");
        }

        PrimaryAccount fromAccount = accountCheckService.findPrimaryAccountByMemberId(loginMember.getMemberId());

        if (toAccountNumber.equals(fromAccount.getAccountNumber())) {
            throw new SameAccountTransferException("동일한 계좌로는 송금할 수 없습니다.");
        }


        try {
            toName = memberCheckService.findMemberByAccount(accountCheckService.findAccountByAccountNumber(toAccountNumber)).getName();
        } catch (AccountNotFoundException | MemberNotFoundException e){
            // TODO: MemberNotFound 는 정합성 문제
            throw new RecipientAccountNotFoundException("수취인 계좌를 찾을 수 없습니다.");
        }

        PendingTransfer pendingTransfer = new PendingTransfer();
        pendingTransfer.setSenderAccountId(fromAccount.getAccountId());
        pendingTransfer.setSenderMemberId(fromAccount.getMemberId());
        pendingTransfer.setStatus(PENDING_AMOUNT);
        pendingTransfer.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        pendingTransfer.setReceiverAccountNumber(toAccountNumber);
        pendingTransfer.setReceiverBankName(BankNameConverter.getBankNameByCode(toBankCode));
        pendingTransfer.setReceiverName(toName);

        pendingTransfer = pendingTransferDao.init(pendingTransfer);

        return pendingTransfer.getRequestId();
    }

    @Override
    @Transactional(readOnly = true)
    public PendingTransferResponse getPendingTransferResponse(String requestId) throws PendingTransferNotFoundException , PrimaryAccountNotFoundException{

        PendingTransfer pendingTransfer = pendingTransferDao.findById(requestId)
                .orElseThrow(() -> new PendingTransferNotFoundException("유효하지 않은 송금 요청입니다. ID: " + requestId));

        PrimaryAccount senderPrimaryAccount = accountCheckService.findPrimaryAccountByAccountId(pendingTransfer.getSenderAccountId());

        return PendingTransferResponse.from(pendingTransfer,senderPrimaryAccount);
    }

    @Override
    @Transactional
    public void processAmount(String requestId, TransferAmountRequest transferAmountRequest) throws InsufficientBalanceException, PendingTransferNotFoundException , InvalidAmountException , IllegalTransferStateException , IllegalStateException{

        BigDecimal amountToTransfer = transferAmountRequest.getAmount();

        if(amountToTransfer == null || amountToTransfer.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("송금액은 0보다 커야 합니다.");
        }

        PendingTransfer pendingTransfer = pendingTransferDao.findById(requestId)
                .orElseThrow(() -> new PendingTransferNotFoundException("유효하지 않은 송금 요청입니다. ID: " + requestId));

        if(pendingTransfer.getStatus() != PENDING_AMOUNT){
            throw new IllegalTransferStateException("금액을 입력할 수 있는 단계가 아닙니다.");
        }

        PrimaryAccount senderPrimaryAccount;
        try {
            senderPrimaryAccount = accountCheckService.findPrimaryAccountByAccountId(pendingTransfer.getSenderAccountId());
        } catch (PrimaryAccountNotFoundException e) {
            throw new IllegalStateException("송금 처리 중 심각한 내부 데이터 오류가 발생했습니다.");
        }

        if (senderPrimaryAccount.getBalance().compareTo(amountToTransfer) < 0) {
            throw new InsufficientBalanceException("계좌 잔액이 부족합니다.");
        }

        pendingTransfer.setAmount(amountToTransfer);
        pendingTransfer.setStatus(PENDING_MESSAGE);
        pendingTransfer.setUpdatedAt(LocalDateTime.now());
        pendingTransferDao.update(pendingTransfer);
    }

    @Override
    @Transactional
    public void processMessage(String requestId, TransferMessageRequest transferMessageRequest) throws PendingTransferNotFoundException ,IllegalTransferStateException {

        PendingTransfer pendingTransfer = pendingTransferDao.findById(requestId)
                .orElseThrow(() -> new PendingTransferNotFoundException("유효하지 않은 송금 요청입니다. ID: " + requestId));

        if(pendingTransfer.getStatus() != PENDING_MESSAGE){
            throw new IllegalTransferStateException("메시지를 입력할 수 있는 단계가 아닙니다.");
        }

        pendingTransfer.setMessage(transferMessageRequest.getMessage());
        pendingTransfer.setStatus(PENDING_AUTH);
        pendingTransfer.setUpdatedAt(LocalDateTime.now());
        pendingTransferDao.update(pendingTransfer);
    }

    @Override
    @Transactional
    public void executeTransfer(String requestId , TransferAuthRequest transferAuthRequest) throws PendingTransferNotFoundException , IllegalTransferStateException , IncorrectPasswordException{

        PendingTransfer pendingTransfer = pendingTransferDao.findById(requestId)
                .orElseThrow(() -> new PendingTransferNotFoundException("유효하지 않은 송금 요청입니다. ID: " + requestId));

        if(pendingTransfer.getStatus() != PENDING_AUTH){
            throw new IllegalTransferStateException("인증 단계가 아닙니다.");
        }

        verifyMemberPassword(pendingTransfer, transferAuthRequest);
        long senderLedgerId = debitFromSender(pendingTransfer);
        long creditLedgerId = creditToReceiver(pendingTransfer);
        recordTransferCompletion(pendingTransfer, senderLedgerId, creditLedgerId);


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
