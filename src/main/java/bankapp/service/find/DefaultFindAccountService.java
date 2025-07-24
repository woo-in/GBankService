package bankapp.service.find;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import bankapp.account.model.BankAccount;
import bankapp.dao.BankAccountDao;
import bankapp.exceptions.InvalidAccountException;
import bankapp.exceptions.InvalidAccountException.Role;

@Component
public class DefaultFindAccountService implements FindAccountService{

    // 데이터 베이스 연동
    private final BankAccountDao bankAccountDao ;

    @Autowired
    public DefaultFindAccountService(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    // 계좌 조회 , 반환 서비스
    @Transactional
    @Override
    public BankAccount findAccount(int accountNumber) throws InvalidAccountException  {

        // 계좌가 존재하지 않음
        if(!bankAccountDao.isAccountExist(accountNumber)) {
            throw new InvalidAccountException(Role.GENERAL);
        }

        // 계좌 반환
        return bankAccountDao.selectBankAccount(accountNumber);
    }
}

