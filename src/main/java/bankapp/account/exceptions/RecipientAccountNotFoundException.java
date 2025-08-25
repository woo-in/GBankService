package bankapp.account.exceptions;

public class RecipientAccountNotFoundException extends RuntimeException {
    public RecipientAccountNotFoundException(String message) {
        super(message);
    }
}
