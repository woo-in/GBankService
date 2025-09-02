package bankapp.account.service.open.primary;

import bankapp.account.dao.account.AccountDao;
import bankapp.account.model.account.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.account.service.open.component.AccountNumberGenerator;
import bankapp.account.service.open.component.AccountOpeningValidator;
import bankapp.member.exceptions.MemberNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
    private final AccountOpeningValidator validator;
    private final AccountNumberGenerator accountNumberGenerator;

    @Autowired
    public DefaultOpenPrimaryAccountService(AccountDao accountDao,
                                            AccountOpeningValidator validator,
                                            AccountNumberGenerator accountNumberGenerator) {
        this.accountDao = accountDao;
        this.validator = validator;
        this.accountNumberGenerator = accountNumberGenerator;
    }


    @Override
    public PrimaryAccount openPrimaryAccount(OpenPrimaryAccountRequest openPrimaryAccountRequest) throws InvalidDepositAmountException, MemberNotFoundException {
        validator.validate(openPrimaryAccountRequest);

        PrimaryAccount newAccount = PrimaryAccount.from(openPrimaryAccountRequest);
        newAccount.setAccountNumber(accountNumberGenerator.generate());
        newAccount.setAccountType("PRIMARY");

        return accountDao.insertAccount(newAccount);
    }





}
