package bankapp.request.open;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class HighCreditAccountCreationRequest  extends AccountCreationRequest {

    // 신용등급(1toA , 2toB , 3toC)
    @NotNull(message = "신용등급을 선택하세요.")
    @Range(min = 1 , max = 3 , message = "신용등급은 1(A), 2(B), 3(C) 중 하나를 선택해야 합니다.")
    private Integer grade ;

    // 생성자

    public HighCreditAccountCreationRequest(){ }

    public HighCreditAccountCreationRequest(Integer accountNumber , String customerName , Double balance , Integer ratio , Integer grade , String accountType){
        super(accountNumber,customerName,balance , ratio , accountType);
        this.grade = grade ;
    }



}
