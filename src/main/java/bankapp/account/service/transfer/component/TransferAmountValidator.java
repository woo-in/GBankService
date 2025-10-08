package bankapp.account.service.transfer.component;

import bankapp.account.exceptions.*;
import bankapp.account.model.account.Account;
import bankapp.account.model.transfer.PendingTransfer;
import bankapp.account.repository.PendingTransferRepository;
import bankapp.account.request.transfer.TransferAmountRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

import static bankapp.account.model.transfer.TransferStatus.*;

@Component
public class TransferAmountValidator {

    private final PendingTransferRepository pendingTransferRepository;

    public TransferAmountValidator(PendingTransferRepository pendingTransferRepository) {
        this.pendingTransferRepository = pendingTransferRepository;
    }

    /**
     * 송금 금액 처리 요청의 유효성을 검증합니다.
     * 모든 검증 통과 시, 검증된 PendingTransfer 를 반환합니다.
     *
     * @param requestId          요청 ID
     * @param transferAmountRequest 송금 금액
     *
     * @throws InvalidAmountException         송금액이 유효하지 않을 때 (0원 이하)
     * @throws PendingTransferNotFoundException 해당 requestId의 송금 정보를 찾을 수 없을 때
     * @throws IllegalTransferStateException  현재 송금 상태가 금액을 입력할 수 있는 단계가 아닐 때
     * @throws InsufficientBalanceException   출금 계좌의 잔액이 송금액보다 부족할 때
     * @throws IllegalStateException          데이터 정합성 문제 등 예기치 않은 서버 내부 오류 발생 시
     * @return 검증된 PendingTransfer
     */
    public PendingTransfer validate(String requestId, TransferAmountRequest transferAmountRequest) throws InvalidAmountException , PendingTransferNotFoundException , IllegalTransferStateException ,InsufficientBalanceException, IllegalStateException {

        BigDecimal amountToTransfer = transferAmountRequest.getAmount();

        if (amountToTransfer == null || amountToTransfer.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("송금액은 0보다 커야 합니다.");
        }

        PendingTransfer pendingTransfer = pendingTransferRepository.findById(requestId)
                .orElseThrow(() -> new PendingTransferNotFoundException("유효하지 않은 송금 요청입니다. ID: " + requestId));

        if (pendingTransfer.getStatus() != PENDING_AMOUNT) {
            throw new IllegalTransferStateException("금액을 입력할 수 있는 단계가 아닙니다.");
        }


        Account senderAccount = Optional.ofNullable(pendingTransfer.getSenderAccount())
                .orElseThrow(() -> new IllegalStateException("송금 처리 중 심각한 내부 데이터 오류가 발생했습니다."));

        if (senderAccount.getBalance().compareTo(amountToTransfer) < 0) {
            throw new InsufficientBalanceException("계좌 잔액이 부족합니다.");
        }

        return pendingTransfer;

    }
}
