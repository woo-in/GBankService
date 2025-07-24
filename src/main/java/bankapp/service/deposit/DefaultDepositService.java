package bankapp.service.deposit;

import bankapp.account.deposit.AccountDepositRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import bankapp.dao.BankAccountDao;
import bankapp.exceptions.InvalidAccountException;
import bankapp.exceptions.InvalidAccountException.Role;

@Component
public class DefaultDepositService implements DepositService{


    // 데이터 베이스 연동
    private final BankAccountDao bankAccountDao ;

    @Autowired
    public DefaultDepositService(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    // 입금 서비스
    @Transactional
    @Override
    public void deposit(AccountDepositRequest accountDepositRequest) throws IllegalArgumentException , InvalidAccountException  {

        int accountNumber = accountDepositRequest.getAccountNumber();
        double amount = accountDepositRequest.getAmount();

        if (amount < 0.0) {
            throw new IllegalArgumentException("Negative number error");
        }

        // 계좌가 존재하지 않음
        if(!bankAccountDao.isAccountExist(accountNumber)) {
            throw new InvalidAccountException(Role.GENERAL);
        }
        // 입금
        bankAccountDao.updateBalancePlus(accountNumber, amount);
    }
}
