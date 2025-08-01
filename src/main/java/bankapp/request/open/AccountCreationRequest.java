package bankapp.request.open;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreationRequest {

    private Integer accountNumber;
    private String customerName;
    private Double balance;
    private Integer ratio;
    private String accountType;

    public AccountCreationRequest(){ }

    public AccountCreationRequest(Integer accountNumber , String customerName , Double balance , Integer ratio , String accountType){
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = balance;
        this.ratio = ratio;
        this.accountType = accountType;
    }



}
