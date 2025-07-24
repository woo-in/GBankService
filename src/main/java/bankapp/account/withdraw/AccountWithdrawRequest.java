package bankapp.account.withdraw;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountWithdrawRequest {
    private int accountNumber;
    private double amount;

    public AccountWithdrawRequest() { }
    public AccountWithdrawRequest(int accountNumber , double amount) {
        this.amount = amount;
        this.accountNumber = accountNumber;
    }

}
