package bankapp.account.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract public class BankAccount {

    
    private int accountNumber;
    private String customerName;
    private double balance;
    private int ratio;
    private String accountType;

    public BankAccount(){ }

    public BankAccount(int accountNumber , String customerName , double balance , int ratio , String accountType){
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = balance;
        this.ratio = ratio;
        this.accountType = accountType;
    }

    
}




