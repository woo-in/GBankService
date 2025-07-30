package bankapp.request.find;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountFindRequest {

    private int accountNumber;

    public AccountFindRequest() { }
    public AccountFindRequest(int accountNumber) {
        this.accountNumber = accountNumber;
    }
}
