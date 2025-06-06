package bankapp.account;

public class NormalAccount extends BankAccount{

    // 생성자
    public NormalAccount(int accountNumber , String customerName , double balance , int ratio , String accountType){
        super(accountNumber,customerName,balance,ratio , accountType);
    }

}


