package bankapp.account.service.check;

import bankapp.account.dao.AccountDao;
import bankapp.account.exceptions.AccountNotFoundException;
import bankapp.account.exceptions.PrimaryAccountNotFoundException;
import bankapp.account.model.Account;
import bankapp.account.model.PrimaryAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

import static bankapp.core.common.BankCode.WOOIN_BANK;


/**
 * {@inheritDoc}
 * <p>
 * 이 구현체는 AccountDao를 사용하여 데이터베이스에 직접 조회함으로써
 * 계좌 정보 확인 및 조회 로직을 수행합니다.
 */

// TODO: 테스트 코드 작성

@Component
public class DefaultAccountCheckService implements AccountCheckService {

    private final AccountDao accountDao;

    @Autowired
    public DefaultAccountCheckService(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    @Override
    public boolean isAccountNumberExist(String accountNumber){

        if(accountNumber == null) {
            return false;
        }

        Optional<Account> accountOptional = accountDao.findByAccountNumber(accountNumber);
        return accountOptional.isPresent();

    }

    @Override
    public PrimaryAccount findPrimaryAccountByMemberId(Long memberId) throws PrimaryAccountNotFoundException{

        List<Account> accountList = accountDao.findByMemberId(memberId);

        for(Account account : accountList){
            if(account.getAccountType().equals("PRIMARY")){
                return (PrimaryAccount) account;
            }
        }

        throw new PrimaryAccountNotFoundException("해당 회원의 주계좌(PRIMARY)를 찾을 수 없습니다. memberId: " + memberId);
    }

    @Override
    public Account findAccountByAccountNumber(String accountNumber) throws AccountNotFoundException{
        if(accountNumber == null) {
            throw new AccountNotFoundException("해당 하는 계좌를 찾을 수 없습니다.");
        }

        Optional<Account> accountOptional = accountDao.findByAccountNumber(accountNumber);

        if(accountOptional.isEmpty()) {
            throw new AccountNotFoundException("해당 하는 계좌를 찾을 수 없습니다.");
        }

        return accountOptional.get();
    }

    @Override
    public boolean isExternalBank(String bankCode) {
        return !WOOIN_BANK.equals(bankCode);
    }



}
