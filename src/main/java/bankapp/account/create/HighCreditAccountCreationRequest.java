package bankapp.account.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HighCreditAccountCreationRequest  extends AccountCreationRequest {

    // 신용등급(1toA , 2toB , 3toC)
    private int grade ;

    // 생성자

    public HighCreditAccountCreationRequest(){ }

    public HighCreditAccountCreationRequest(int accountNumber , String customerName , double balance , int ratio , int grade , String accountType){
        super(accountNumber,customerName,balance , ratio , accountType);
        this.grade = grade ;
    }



}
