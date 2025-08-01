package bankapp.request.open;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormalAccountCreationRequest extends AccountCreationRequest {


    // 생성자

    public NormalAccountCreationRequest(){ }

    public NormalAccountCreationRequest(Integer accountNumber , String customerName , Double balance , Integer ratio , String accountType){
        super(accountNumber,customerName,balance,ratio , accountType);
    }

}
