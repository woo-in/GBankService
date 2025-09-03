package bankapp.account.request.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TransferRecipientRequest {

    @NotBlank(message = "은행을 선택해주세요.")
    private String toBankCode;

    @Pattern(
            regexp = "^(\\d{10}|\\d{11}|\\d{12}|\\d{14}|\\d{15})$",
            message = "계좌번호는 10,11,12,14,15 자리 숫자여야 합니다."
    )
    private String toAccountNumber;


    public TransferRecipientRequest(String toBankCode, String toAccountNumber) {
        this.toBankCode = toBankCode;
        this.toAccountNumber = toAccountNumber;
    }

    public TransferRecipientRequest() {}
}


