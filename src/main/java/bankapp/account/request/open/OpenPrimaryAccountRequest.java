package bankapp.account.request.open;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OpenPrimaryAccountRequest {

    private Long memberId;
    private BigDecimal balance;
    private String nickname;


    public OpenPrimaryAccountRequest() { }
    public OpenPrimaryAccountRequest(Long memberId, BigDecimal balance, String nickname) {
        this.memberId = memberId;
        this.balance = balance;
        this.nickname = nickname;
    }
}
