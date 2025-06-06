package bankapp.exceptions;

import java.io.Serial;

public class DuplicateAccountException extends RuntimeException{

	@Serial
	private static final long serialVersionUID = 1L;
	
	public DuplicateAccountException(String message) {
		super(message);
	}
}


