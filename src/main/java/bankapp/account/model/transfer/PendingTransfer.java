package bankapp.account.model.transfer;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PendingTransfer {

    private String requestId;
    private Long senderMemberId;
    private Long senderAccountId;
    private TransferStatus status;
    private String receiverBankName;
    private String receiverAccountNumber;
    private String receiverName;
    private BigDecimal amount;
    private String message;
    private Long senderLedgerId;
    private Long receiverLedgerId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
