package bankapp.account.exceptions;

public class IllegalTransferStateException extends RuntimeException {
    public IllegalTransferStateException(String message) {
        super(message);
    }
}
