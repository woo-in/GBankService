package bankapp.request.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionSearchRequest {

    private int accountNumber;

    public TransactionSearchRequest() { }
    public TransactionSearchRequest(int accountNumber) {
        this.accountNumber = accountNumber;
    }

}
