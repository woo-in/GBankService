package bankapp.request.deposit;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDepositRequest {

    private int accountNumber;
    private double amount;

    public AccountDepositRequest() { }
    public AccountDepositRequest(int accountNumber , double amount) {
        this.amount = amount;
        this.accountNumber = accountNumber;
    }
}