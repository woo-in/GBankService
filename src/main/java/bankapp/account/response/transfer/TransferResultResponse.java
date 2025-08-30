package bankapp.account.response.transfer;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferResultResponse {

    private String receiverBankName;
    private String receiverAccountNumber;
    private String receiverName;
    private BigDecimal amount;

    public TransferResultResponse(String receiverBankName, String receiverAccountNumber, String receiverName, BigDecimal amount) {
        this.receiverBankName = receiverBankName;
        this.receiverAccountNumber = receiverAccountNumber;
        this.receiverName = receiverName;
        this.amount = amount;
    }
}
