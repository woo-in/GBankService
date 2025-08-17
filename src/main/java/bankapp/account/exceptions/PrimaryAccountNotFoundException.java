package bankapp.account.exceptions;

public class PrimaryAccountNotFoundException extends RuntimeException{
    public PrimaryAccountNotFoundException(String message) {
        super(message);
    }
}
