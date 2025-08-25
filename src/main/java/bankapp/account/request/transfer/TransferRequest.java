package bankapp.account.request.transfer;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    /**
     * 보내는 사람의 계좌 ID.
     * 이 값은 사용자가 직접 입력하는 것이 아니라,
     * 컨트롤러에서 로그인 세션 정보를 통해 안전하게 설정해야 합니다.
     */
    private Long fromAccountId;

    /**
     * 받는 사람의 은행 코드. 사용자가 직접 선택합니다.
     */
    @NotBlank(message = "은행을 선택해주세요.")
    private String toBankCode;

    /**
     * 받는 사람의 계좌 번호. 사용자가 직접 입력합니다.
     */
    @Pattern(
            regexp = "^(\\d{10}|\\d{11}|\\d{12}|\\d{14}|\\d{15})$",
            message = "계좌번호는 10,11,12,14,15 자리 숫자여야 합니다."
    )
    private String toAccountNumber;

    /**
     * 보낼 금액. 사용자가 직접 입력합니다.
     */
    @NotNull(message = "보낼 금액을 입력해주세요.")
    @Positive(message = "송금액은 0보다 커야 합니다.")
    private BigDecimal amount;

    /**
     * 받는 분에게 표시될 메시지 (선택 사항).
     */
    @Size(max = 100, message = "메시지는 100자 이하로 입력해주세요.")
    private String message;

    /**
     * 보내는 사람의 계좌 비밀번호 (이체 인증용). 사용자가 직접 입력합니다.
     */
    @NotBlank(message = "계좌 비밀번호를 입력해주세요.")
    private String password;



    TransferRequest(){}
}
