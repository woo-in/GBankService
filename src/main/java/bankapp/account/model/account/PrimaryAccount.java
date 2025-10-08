package bankapp.account.model.account;


import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.member.model.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("PRIMARY")
public class PrimaryAccount extends Account {

    protected PrimaryAccount() {}

    public PrimaryAccount(Member member, String accountNumber, BigDecimal balance, String nickname, AccountStatus status) {
        super(member, accountNumber, balance, nickname,status);
    }

    public PrimaryAccount(Member member, String accountNumber, BigDecimal balance, AccountStatus status) {
        super(member, accountNumber, balance, status);
    }

    public static PrimaryAccount from(OpenPrimaryAccountRequest openPrimaryAccountRequest, Member member,String accountNumber)  {
         return new PrimaryAccount(member , accountNumber , openPrimaryAccountRequest.getBalance() , openPrimaryAccountRequest.getNickname() , AccountStatus.ACTIVE);
    }

}
