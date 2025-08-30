package bankapp.account.exceptions;

public class PendingTransferNotFoundException extends RuntimeException {
    public PendingTransferNotFoundException(String message) {
        super(message);
    }
}
