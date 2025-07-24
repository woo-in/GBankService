package bankapp.service.withdraw;


import bankapp.account.withdraw.AccountWithdrawRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import bankapp.dao.BankAccountDao;
import bankapp.exceptions.InsufficientFundsException;
import bankapp.exceptions.InvalidAccountException;
import bankapp.exceptions.InvalidAccountException.Role;

@Component
public class DefaultWithdrawService implements WithdrawService {

    // 데이터 베이스 연동
    private final BankAccountDao bankAccountDao;

    @Autowired
    public DefaultWithdrawService(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    // 출금 서비스
    @Override
    @Transactional
    public void withdraw(AccountWithdrawRequest accountWithdrawRequest) throws IllegalArgumentException, InsufficientFundsException , InvalidAccountException {

        int accountNumber = accountWithdrawRequest.getAccountNumber();
        double amount = accountWithdrawRequest.getAmount();

        if (amount < 0.0) {
            throw new IllegalArgumentException("Negative number error");
        }

        // 계좌가 존재하지 않음
        if(!bankAccountDao.isAccountExist(accountNumber)) {
            throw new InvalidAccountException(Role.GENERAL);
        }

        // 예치금이 적음
        if(amount > bankAccountDao.selectBalance(accountNumber)) {
            throw new InsufficientFundsException("Insufficient Funds error");
        }

        // 출금
        bankAccountDao.updateBalanceMinus(accountNumber, amount);

    }
}
