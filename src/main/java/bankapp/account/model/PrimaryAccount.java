package bankapp.account.model;


import bankapp.account.request.open.OpenPrimaryAccountRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PrimaryAccount {

    private Long accountId;
    private Long memberId;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private String nickname;
    private LocalDateTime createdAt;

    public PrimaryAccount() { }
    public PrimaryAccount(Long accountId, Long memberId, String accountNumber, BigDecimal balance, String accountType, String nickname, LocalDateTime createdAt) {
        this.accountId = accountId;
        this.memberId = memberId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public static PrimaryAccount from(OpenPrimaryAccountRequest openPrimaryAccountRequest){
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setMemberId(openPrimaryAccountRequest.getMemberId());
        primaryAccount.setBalance(openPrimaryAccountRequest.getBalance());
        primaryAccount.setNickname(openPrimaryAccountRequest.getNickname());

        return primaryAccount;
    }




}
