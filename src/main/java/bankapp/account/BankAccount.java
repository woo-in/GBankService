package bankapp.account;

abstract public class BankAccount {

    
    final private int accountNumber;
    final private String customerName;
    final private double balance;
    final private int ratio;
    final private String accountType;

    public BankAccount(int accountNumber , String customerName , double balance , int ratio , String accountType){
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = balance;
        this.ratio = ratio;
        this.accountType = accountType;
    }

    // getter
    public int getAccountNumber(){
        return accountNumber;
    }
    public String getCustomerName(){
        return customerName;
    }
    public double getBalance(){
        return balance;
    }
    public int getRatio() { return ratio; }
    public String getAccountType() {return accountType; }

    
}




