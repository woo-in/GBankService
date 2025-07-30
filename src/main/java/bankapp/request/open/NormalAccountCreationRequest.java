package bankapp.request.open;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormalAccountCreationRequest extends AccountCreationRequest {


    // 생성자

    public NormalAccountCreationRequest(){ }

    public NormalAccountCreationRequest(int accountNumber , String customerName , double balance , int ratio , String accountType){
        super(accountNumber,customerName,balance,ratio , accountType);
    }

}
