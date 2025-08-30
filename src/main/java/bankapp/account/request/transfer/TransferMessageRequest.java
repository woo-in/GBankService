package bankapp.account.request.transfer;


import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TransferMessageRequest {

    @Size(max = 20, message = "메시지는 20자 이내로 입력해주세요.")
    private String message;

}
