package bankapp.model.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HighCreditAccount extends BankAccount {

    // 신용등급(1toA , 2toB , 3toC)
    private Integer grade ;



    // 생성자
    public HighCreditAccount(){ }
    public HighCreditAccount(int accountNumber , String customerName , double balance , int ratio , int grade , String accountType){
        super(accountNumber,customerName,balance , ratio , accountType);
        this.grade = grade ;
    }

}


