package bankapp.account.transfer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransferRequest {
    private int senderNumber;
    private int receiverNumber;
    private double amount;

    public AccountTransferRequest() { }
    public AccountTransferRequest(int senderNumber , int receiverNumber , double amount) {
        this.senderNumber = senderNumber;
        this.receiverNumber = receiverNumber;
        this.amount = amount;
    }
}
