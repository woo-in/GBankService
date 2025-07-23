package bankapp.account.request.creation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreationRequest {

    private int accountNumber;
    private String customerName;
    private double balance;
    private int ratio;
    private String accountType;

    public AccountCreationRequest(){ }

    public AccountCreationRequest(int accountNumber , String customerName , double balance , int ratio , String accountType){
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = balance;
        this.ratio = ratio;
        this.accountType = accountType;
    }



}
