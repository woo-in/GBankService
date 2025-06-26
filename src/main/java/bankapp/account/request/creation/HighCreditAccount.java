package bankapp.account.request.creation;


public class HighCreditAccount extends BankAccount {

    // 신용등급(1toA , 2toB , 3toC)
    final private int grade ;

    // 생성자
    public HighCreditAccount(int accountNumber , String customerName , double balance , int ratio , int grade , String accountType){
        super(accountNumber,customerName,balance , ratio , accountType);
        this.grade = grade ;
    }

   @SuppressWarnings("unused")
   public int getGrade() { return grade; }

}


