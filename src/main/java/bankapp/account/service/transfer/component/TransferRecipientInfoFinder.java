package bankapp.account.service.transfer.component;

import bankapp.account.exceptions.AccountNotFoundException;
import bankapp.account.exceptions.RecipientAccountNotFoundException;
import bankapp.account.model.account.Account;
import bankapp.account.service.check.AccountCheckService;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class TransferRecipientInfoFinder {

    private final AccountCheckService accountCheckService;

    @Autowired
    public TransferRecipientInfoFinder(AccountCheckService accountCheckService) {
        this.accountCheckService = accountCheckService;
    }

    /**
     * 계좌번호를 사용하여 수취인의 실명을 조회합니다.
     * @param accountNumber 조회할 계좌번호
     * @return 조회된 수취인의 이름
     * @throws RecipientAccountNotFoundException 계좌 또는 연결된 회원을 찾을 수 없는 경우
     */
    public String findRecipientName(String accountNumber) throws RecipientAccountNotFoundException {
        try {
            Account account = accountCheckService.findAccountByAccountNumber(accountNumber);

            Member member = Optional.ofNullable(account.getMember())
                    .orElseThrow(() -> new MemberNotFoundException("멤버 정보가 유효하지 않습니다."));

            return member.getName();
        } catch (AccountNotFoundException | MemberNotFoundException e) {
            throw new RecipientAccountNotFoundException("수취인 계좌를 찾을 수 없습니다.");
        }
    }

}
