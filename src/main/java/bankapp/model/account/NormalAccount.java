package bankapp.model.account;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormalAccount extends BankAccount {


    // 생성자
    public NormalAccount() {}
    public NormalAccount(int accountNumber , String customerName , double balance , int ratio , String accountType){
        super(accountNumber,customerName,balance,ratio , accountType);
    }

}


