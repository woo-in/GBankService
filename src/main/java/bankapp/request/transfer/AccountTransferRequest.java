package bankapp.request.transfer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransferRequest {
    private Integer senderNumber;
    private Integer receiverNumber;
    private Double amount;

    public AccountTransferRequest() { }
    public AccountTransferRequest(Integer senderNumber , Integer receiverNumber , Double amount) {
        this.senderNumber = senderNumber;
        this.receiverNumber = receiverNumber;
        this.amount = amount;
    }
}
