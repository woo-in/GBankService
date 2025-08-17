package bankapp.account.request.transfer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferAmountRequest {

    @NotNull(message = "보낼 금액을 입력해주세요.")
    @Positive(message = "송금액은 0보다 커야 합니다.")
    private BigDecimal amount;
}


