package bankapp.account.service.open.primary;

import bankapp.account.dao.AccountDao;
import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.service.check.MemberCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.math.BigDecimal;

// TODO: 테스트 코드 작성

/**
 * {@inheritDoc}
 * <p>
 * 이 구현체는 AccountDao와 MemberCheckService와 협력하여,
 * 회원 존재 여부와 초기 입금액을 검증한 후, 새로운 계좌번호를 생성하여
 * 데이터베이스에 신규 주계좌 정보를 저장하는 방식으로 로직을 수행합니다.
 */

@Service
public class DefaultOpenPrimaryAccountService implements OpenPrimaryAccountService{

    private final AccountDao accountDao;
    private final MemberCheckService memberCheckService;
    @Autowired
    public DefaultOpenPrimaryAccountService(AccountDao accountDao , MemberCheckService memberCheckService) {
        this.accountDao = accountDao;
        this.memberCheckService = memberCheckService;
    }

    @Override
    public PrimaryAccount openPrimaryAccount(OpenPrimaryAccountRequest openPrimaryAccountRequest) throws InvalidDepositAmountException, MemberNotFoundException {

        Long memberId = openPrimaryAccountRequest.getMemberId();
        BigDecimal balance = openPrimaryAccountRequest.getBalance();

        if(balance.compareTo(BigDecimal.ZERO) < 0){
            throw new InvalidDepositAmountException("입금액은 0원 이상이어야 합니다.");
        }

        if(!memberCheckService.isMemberIdExist(memberId)){
            throw new MemberNotFoundException("memberId " + memberId + "not found.");
        }

        PrimaryAccount newAccount = PrimaryAccount.from(openPrimaryAccountRequest);
        newAccount.setAccountType("PRIMARY");
        newAccount.setAccountNumber(createAccountNumber());
        return accountDao.insertAccount(newAccount);
    }


    /**
     * '은행코드-날짜-랜덤숫자' 조합의 새로운 계좌번호를 생성합니다.
     * @return 생성된 계좌번호 문자열 (예: 110-250816-123456)
     */
    private String createAccountNumber() {
        String bankCode = "110";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        int randomPart = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return bankCode + "-" + datePart + "-" + randomPart;
    }


}
