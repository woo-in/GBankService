package bankapp.account.exceptions;

public class PrimaryAccountNotFoundException extends AccountNotFoundException{
    public PrimaryAccountNotFoundException(String message) {
        super(message);
    }
}
