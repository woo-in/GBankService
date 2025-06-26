package bankapp.account.request.creation;

public class NormalAccountCreationRequest extends AccountCreationRequest{

    // 생성자
    public NormalAccountCreationRequest(int accountNumber , String customerName , double balance , int ratio , String accountType){
        super(accountNumber,customerName,balance,ratio , accountType);
    }

}
