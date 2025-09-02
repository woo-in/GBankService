package bankapp.account.service.open.component;

import bankapp.core.common.BankCode;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountNumberGenerator {

    /**
     * '은행코드-날짜-랜덤숫자' 조합의 새로운 계좌번호를 생성합니다.
     * @return 생성된 계좌번호 문자열 (예: 110-250816-123456)
     */
    public String generate() {
        String bankCode = BankCode.WOOIN_BANK;
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        int randomPart = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return bankCode + "-" + datePart + "-" + randomPart;
    }
}
