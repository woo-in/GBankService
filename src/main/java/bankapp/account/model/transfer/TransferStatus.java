package bankapp.account.model.transfer;

import bankapp.account.exceptions.IllegalTransferStateException;
import bankapp.account.exceptions.PendingTransferNotFoundException;
import bankapp.member.exceptions.IncorrectPasswordException;

public enum TransferStatus {
    PENDING_AMOUNT,
    PENDING_MESSAGE,
    PENDING_AUTH,
    PENDING_TRANSFER,

    EXPIRED ,
    COMPLETED,
    FAILED
}


//@throws PendingTransferNotFoundException 주어진 requestId에 해당하는 송금 요청이 없을 경우
//@throws IllegalTransferStateException 인증 단계가 맞지 않을 경우
//@throws IncorrectPasswordException 입력된 계좌 비밀번호가 실제 비밀번호와 일치하지 않을 경우







