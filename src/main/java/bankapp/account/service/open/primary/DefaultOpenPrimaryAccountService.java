package bankapp.account.service.open.primary;

import bankapp.account.dao.AccountDao;
import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.manager.MemberManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import java.math.BigDecimal;


@Component
public class DefaultOpenPrimaryAccountService implements OpenPrimaryAccountService{

    private final AccountDao accountDao;
    private final MemberManager memberManager;


    @Autowired
    public DefaultOpenPrimaryAccountService(AccountDao accountDao , MemberManager memberManager) {
        this.accountDao = accountDao;
        this.memberManager = memberManager;
    }

    public PrimaryAccount openPrimaryAccount(OpenPrimaryAccountRequest openPrimaryAccountRequest) {

        // 0. 값 지역저장
        Long memberId = openPrimaryAccountRequest.getMemberId();
        BigDecimal balance = openPrimaryAccountRequest.getBalance();
        String nickname = openPrimaryAccountRequest.getNickname();

        // 1. 값 검증 (예외 처리)

        // 예치금이 정상적인 값( 0 이상의 값)인가 ?
        if(balance.compareTo(BigDecimal.ZERO) < 0){
            throw new InvalidDepositAmountException("입금액은 0원 이상이어야 합니다.");
        }

        // 멤버가 실제로 존재하는 멤버인가 ?
        if(!memberManager.isMemberIdExist(memberId)){
            throw new MemberNotFoundException("memberId " + memberId + "not found.");
        }

        // 2. 계좌 생성 ( accountType , accountNumber 추가)
        PrimaryAccount newAccount = PrimaryAccount.from(openPrimaryAccountRequest);
        newAccount.setAccountType("PRIMARY");
        newAccount.setAccountNumber(createAccountNumber());
        return accountDao.insertAccount(newAccount);
    }

    // 임시로 고유한 계좌 번호 생성하는 메서드
    private String createAccountNumber() {
        // 1. 은행 고유 코드 (예: 110)
        String bankCode = "110";

        // 2. 오늘 날짜 (예: 2025년 8월 13일 -> 250813)
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // 3. 6자리 랜덤 숫자 생성 (100000 ~ 999999)
        int randomPart = ThreadLocalRandom.current().nextInt(100000, 1000000);

        // 4. 모두 조합하여 최종 계좌번호 생성 (예: 110-250813-123456)
        return bankCode + "-" + datePart + "-" + randomPart;

        // 실제로는 순번 시스템으로 만든다.
        // 중앙에서 관리 , [지점/상품코드] - [생성일자 등] - [순번] - [검증번호]

    }


}
