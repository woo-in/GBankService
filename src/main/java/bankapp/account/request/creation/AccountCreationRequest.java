package bankapp.account.request.creation;

public class AccountCreationRequest {

    final private int accountNumber;
    final private String customerName;
    final private double balance;
    final private int ratio;
    final private String accountType;

    public AccountCreationRequest(int accountNumber , String customerName , double balance , int ratio , String accountType){
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
