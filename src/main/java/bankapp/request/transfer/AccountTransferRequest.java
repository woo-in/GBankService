package bankapp.request.transfer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AccountTransferRequest {

    @NotNull(message = "송금 계좌 ID를 입력하세요.")
    @Range(min = 100001 , max = 999999 , message = "송금 계좌 ID는 100001부터 999999 사이의 값이어야 합니다.")
    private Integer senderNumber;

    @NotNull(message = "수취 계좌 ID를 입력하세요.")
    @Range(min = 100001 , max = 999999 , message = "수취 계좌 ID는 100001부터 999999 사이의 값이어야 합니다.")
    private Integer receiverNumber;


    @NotNull(message = "송금액을 입력하세요.")
    @PositiveOrZero(message = "송금액은 0 이상이어야 합니다.")
    private Double amount;


    public AccountTransferRequest() { }
    public AccountTransferRequest(Integer senderNumber , Integer receiverNumber , Double amount) {
        this.senderNumber = senderNumber;
        this.receiverNumber = receiverNumber;
        this.amount = amount;
    }
}
