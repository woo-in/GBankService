package bankapp.account.model.transfer;

public enum TransferStatus {
    PENDING_RECEIVER, // not use
    PENDING_AMOUNT,
    PENDING_MESSAGE,
    PENDING_AUTH,
    PENDING_TRANSFER,
    COMPLETED,
    FAILED,
    EXPIRED
}
