package bankapp.account.service.transfer.component;

import bankapp.account.exceptions.ExternalTransferNotSupportedException;
import bankapp.account.exceptions.RecipientAccountNotFoundException;
import bankapp.account.exceptions.SameAccountTransferException;
import bankapp.account.model.account.PrimaryAccount;
import bankapp.account.request.transfer.TransferRecipientRequest;
import bankapp.account.service.check.AccountCheckService;
import bankapp.core.util.AccountNumberFormatter;
import bankapp.member.model.Member;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferRecipientValidator {

    private final AccountCheckService accountCheckService;

    @Autowired
    public TransferRecipientValidator(AccountCheckService accountCheckService ) {
        this.accountCheckService = accountCheckService;
    }

    /**
     * 송금 수취인 정보 요청에 대한 유효성을 검증하고,
     * 검증 완료된 데이터를 담은 컨텍스트 객체를 반환합니다. (2가지 책임 담당)
     * @return 검증 성공 시, 송금 처리에 필요한 주요 정보(출금 계좌, 수취인 은행코드 , 수취인 계좌번호)
     */
    public ValidationResult validate(TransferRecipientRequest request, Member loginMember) throws ExternalTransferNotSupportedException ,  RecipientAccountNotFoundException , SameAccountTransferException{
        String toBankCode = request.getToBankCode();
        String toAccountNumber = AccountNumberFormatter.format(request.getToAccountNumber());

        if (accountCheckService.isExternalBank(toBankCode)) {
            throw new ExternalTransferNotSupportedException("타행 이체는 현재 서비스 준비 중입니다.");
        }
        if (!accountCheckService.isAccountNumberExist(toAccountNumber)) {
            throw new RecipientAccountNotFoundException("수취인 계좌를 찾을 수 없습니다.");
        }

        PrimaryAccount fromAccount = accountCheckService.findPrimaryAccountByMemberId(loginMember.getMemberId());

        if (toAccountNumber.equals(fromAccount.getAccountNumber())) {
            throw new SameAccountTransferException("동일한 계좌로는 송금할 수 없습니다.");
        }

        return new ValidationResult(fromAccount, toBankCode, toAccountNumber);
    }

    @Data
    public static class ValidationResult {

        private PrimaryAccount fromAccount;
        private String toBankCode;
        private String toAccountNumber;

        public ValidationResult(PrimaryAccount fromAccount, String toBankCode, String toAccountNumber) {
            this.fromAccount = fromAccount;
            this.toBankCode = toBankCode;
            this.toAccountNumber = toAccountNumber;
        }

        //public ValidationResult() {}
    }
}
