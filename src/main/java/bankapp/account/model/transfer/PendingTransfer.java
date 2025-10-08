package bankapp.account.model.transfer;

import bankapp.account.model.account.Account;
import bankapp.member.model.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class PendingTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "sender_member_id" , nullable = false)
    private Member senderMember;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "sender_account_id" , nullable = false)
    private Account senderAccount;


    @Column(nullable = false)
    private String receiverBankName;

    @Column(nullable = false)
    private String receiverAccountNumber;

    @Column(nullable = false)
    private String receiverName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    private BigDecimal amount;
    private String message;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected PendingTransfer() {}

    public PendingTransfer(Member senderMember, Account senderAccount, String receiverBankName, String receiverAccountNumber, String receiverName, TransferStatus status, LocalDateTime expiresAt) {
        this.senderMember = senderMember;
        this.senderAccount = senderAccount;
        this.receiverBankName = receiverBankName;
        this.receiverAccountNumber = receiverAccountNumber;
        this.receiverName = receiverName;
        this.status = status;
        this.expiresAt = expiresAt;
    }

}
