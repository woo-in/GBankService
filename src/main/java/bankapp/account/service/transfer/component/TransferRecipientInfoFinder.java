package bankapp.account.service.transfer.component;

import bankapp.account.exceptions.AccountNotFoundException;
import bankapp.account.exceptions.RecipientAccountNotFoundException;
import bankapp.account.model.account.Account;
import bankapp.account.service.check.AccountCheckService;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.service.check.MemberCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferRecipientInfoFinder {

    private final AccountCheckService accountCheckService;
    private final MemberCheckService memberCheckService;

    @Autowired
    public TransferRecipientInfoFinder(AccountCheckService accountCheckService, MemberCheckService memberCheckService) {
        this.accountCheckService = accountCheckService;
        this.memberCheckService = memberCheckService;
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
            return memberCheckService.findMemberByAccount(account).getName();
        } catch (AccountNotFoundException | MemberNotFoundException e) {
            throw new RecipientAccountNotFoundException("수취인 계좌를 찾을 수 없습니다.");
        }
    }

}
