package bankapp.account.service.transfer.component;

import bankapp.account.exceptions.IllegalTransferStateException;
import bankapp.account.exceptions.PendingTransferNotFoundException;
import bankapp.account.model.transfer.PendingTransfer;
import bankapp.account.repository.PendingTransferRepository;
import org.springframework.stereotype.Component;

import static bankapp.account.model.transfer.TransferStatus.PENDING_AUTH;

@Component
public class TransferAuthValidator {

    private final PendingTransferRepository pendingTransferRepository;

    public TransferAuthValidator(PendingTransferRepository pendingTransferRepository) {
        this.pendingTransferRepository = pendingTransferRepository;
    }


    /**
     * 송금 인증 처리 요청의 유효성을 검증합니다.
     * 모든 검증 통과 시, 검증된 PendingTransfer 를 반환합니다.
     *
     * @param requestId          요청 ID
     * @throws PendingTransferNotFoundException 해당 requestId의 송금 정보를 찾을 수 없을 때
     * @throws IllegalTransferStateException  현재 송금 상태가 인증할 수 있는 단계가 아닐 때
     * @return 검증된 PendingTransfer
     */
    public PendingTransfer validate(String requestId) throws PendingTransferNotFoundException, IllegalTransferStateException  {
        PendingTransfer pendingTransfer = pendingTransferRepository.findById(requestId)
                .orElseThrow(() -> new PendingTransferNotFoundException("유효하지 않은 송금 요청입니다. ID: " + requestId));

        if (pendingTransfer.getStatus() != PENDING_AUTH) {
            throw new IllegalTransferStateException("메시지를 입력할 수 있는 단계가 아닙니다.");
        }

        return pendingTransfer;
    }
}
