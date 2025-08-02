package bankapp.request.open;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HighCreditAccountCreationRequest  extends AccountCreationRequest {

    // 신용등급(1toA , 2toB , 3toC)
    private Integer grade ;

    // 생성자

    public HighCreditAccountCreationRequest(){ }

    public HighCreditAccountCreationRequest(Integer accountNumber , String customerName , Double balance , Integer ratio , Integer grade , String accountType){
        super(accountNumber,customerName,balance , ratio , accountType);
        this.grade = grade ;
    }



}
