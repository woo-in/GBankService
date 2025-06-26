package bankapp.service.transfer;

import bankapp.dao.BankAccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import bankapp.exceptions.*;

// qualifier 등 설정
@Component
@Qualifier("defaultTransferService")
public class DefaultTransferService implements TransferService{

    // 데이터 베이스 연동
	private final BankAccountDao bankAccountDao ;

    @Autowired
    public DefaultTransferService(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    // 송금 서비스(500 원 수수료)
    @Override
    @Transactional
    public void transfer(int fromAccountId, int toAccountId, double amount) throws IllegalArgumentException, InvalidAccountException, InsufficientFundsException {

        if (amount < 0.0) {
            throw new IllegalArgumentException("Negative number error");
        }

        // SENDER 계좌 존재 안함
        if (!bankAccountDao.isAccountExist(fromAccountId)) {
            throw new InvalidAccountException(InvalidAccountException.Role.SENDER);
        }
        // RECEIVER 계좌 존재 안함
        if (!bankAccountDao.isAccountExist(toAccountId)) {
            throw new InvalidAccountException(InvalidAccountException.Role.RECEIVER);
        }

        // 송금액 + 수수료가 현재 잔액보다 큼
        if ((amount + 500) > bankAccountDao.selectBalance(fromAccountId)) {
            throw new InsufficientFundsException("Insufficient Funds error");
        }

        // 업데이트 (시스템 계정에 수수료 누적)
        final int systemAccountId = 0;
        bankAccountDao.updateBalancePlus(systemAccountId, 500);
        bankAccountDao.updateBalanceMinus(fromAccountId, amount + 500);
        bankAccountDao.updateBalancePlus(toAccountId, amount);

    }

}


