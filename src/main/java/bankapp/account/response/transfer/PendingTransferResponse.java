package bankapp.account.response.transfer;

import bankapp.account.model.account.PrimaryAccount;
import bankapp.account.model.transfer.PendingTransfer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PendingTransferResponse {

    private String senderAccountNumber;
    private BigDecimal senderBalance;

    private String receiverBankName;
    private String receiverAccountNumber;
    private String receiverName;

    private BigDecimal amount;
    private String message;

    public static PendingTransferResponse from(PendingTransfer pendingTransfer , PrimaryAccount senderPrimaryAccount){

        PendingTransferResponse pendingTransferResponse = new PendingTransferResponse(pendingTransfer);
        pendingTransferResponse.setSenderAccountNumber(senderPrimaryAccount.getAccountNumber());
        pendingTransferResponse.setSenderBalance(senderPrimaryAccount.getBalance());

        return pendingTransferResponse;
    }

    private PendingTransferResponse(PendingTransfer pendingTransfer){
         this.receiverBankName = pendingTransfer.getReceiverBankName();
         this.receiverAccountNumber = pendingTransfer.getReceiverAccountNumber();
         this.receiverName = pendingTransfer.getReceiverName();
         this.amount = pendingTransfer.getAmount();
         this.message = pendingTransfer.getMessage();
    }


}
