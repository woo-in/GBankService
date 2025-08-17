package bankapp.account.exceptions;

public class UnsupportedAccountTypeException extends RuntimeException{

    public UnsupportedAccountTypeException(String message) {
        super(message);
    }

    public UnsupportedAccountTypeException(String message, Throwable cause) {
        super(message, cause);
    }


}
