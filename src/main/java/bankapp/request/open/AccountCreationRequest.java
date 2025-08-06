package bankapp.request.open;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AccountCreationRequest {

    @NotNull(message = "계좌번호를 입력하세요.")
    @Range(min = 100001 , max = 999999 , message = "계좌번호는 100001 ~ 999999 사이의 정수여야 합니다.")
    private Integer accountNumber;

    @NotBlank(message = "이름을 입력하세요.")
    @Pattern(regexp = "^[A-Za-z]{2,20}$", message = "고객 이름은 영어 2자에서 20자 사이여야 합니다.")
    private String customerName;

    @NotNull(message = "입금액을 입력하세요.")
    @PositiveOrZero(message = "입금액은 0 이상이어야 합니다.")
    private Double balance;

    @NotNull(message = "이자율을 입력하세요.")
    @Range(min = 1 , max = 100 , message = "이자율은 0 ~ 100 사이의 정수여야 합니다.")
    private Integer ratio;

    private String accountType;

    public AccountCreationRequest(){ }

    public AccountCreationRequest(Integer accountNumber , String customerName , Double balance , Integer ratio , String accountType){
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = balance;
        this.ratio = ratio;
        this.accountType = accountType;
    }



}
