package bankapp.account.exceptions;

public class ExternalTransferNotSupportedException extends RuntimeException {
    public ExternalTransferNotSupportedException(String message) {
        super(message);
    }
}
