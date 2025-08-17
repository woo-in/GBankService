package bankapp.account.request.transfer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferAuthRequest {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
