package bankapp.account.model.account;


import bankapp.account.request.open.OpenPrimaryAccountRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
public class PrimaryAccount extends Account {

    public PrimaryAccount() {
        this.accountType = "PRIMARY";
    }
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
